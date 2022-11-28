package ru.practicum.ewmservice.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewmservice.client.PublicClient;
import ru.practicum.ewmservice.common.FromSizeRequest;
import ru.practicum.ewmservice.exceptions.ViolationRuleException;
import ru.practicum.ewmservice.exceptions.ValidationException;
import ru.practicum.ewmservice.model.category.Category;
import ru.practicum.ewmservice.model.event.Event;
import ru.practicum.ewmservice.model.event.State;
import ru.practicum.ewmservice.model.event.dto.*;
import ru.practicum.ewmservice.model.event.mapper.EventMapper;
import ru.practicum.ewmservice.model.request.Request;
import ru.practicum.ewmservice.model.request.RequestStatus;
import ru.practicum.ewmservice.model.request.dto.ParticipationRequestDto;
import ru.practicum.ewmservice.model.request.dto.RequestDto;
import ru.practicum.ewmservice.model.request.mapper.RequestMapper;
import ru.practicum.ewmservice.model.statistic.ViewStats;
import ru.practicum.ewmservice.model.user.User;
import ru.practicum.ewmservice.repository.CategoryRepository;
import ru.practicum.ewmservice.repository.EventRepository;
import ru.practicum.ewmservice.repository.RequestRepository;
import ru.practicum.ewmservice.repository.UserRepository;
import ru.practicum.ewmservice.validation.Validation;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class PrivateService {
    private final EventRepository eventRepository;
    private final RequestRepository requestRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final PublicClient publicClient;
    private final StatsService statsService;


    //Private: События

    //Получение событий, добавленных текущим пользователем
    @Transactional(readOnly = true)
    public List<EventShortDto> getEvent(long userId, int from, int size) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException(String.format("Пользователь c id = %d не найден", userId)));

        Sort sortById = Sort.by(Sort.Direction.ASC, "id");
        Pageable pageable = FromSizeRequest.of(from, size, sortById);
        Page<Event> eventPage = eventRepository.findEventByInitiatorId(userId, pageable);
        List<Event> events = eventPage.toList();
        return this.toEventShortDtoList(events);
    }

    private List<EventShortDto> toEventShortDtoList(List<Event> events) {
        List<EventShortDto> eventShortDtoList = new ArrayList<>();
        if (events.size() == 0) return eventShortDtoList;

        List<EventRequestCount> erc = eventRepository.findEventByEventListRequestConfirm(events);

        for (Event event : events) {
            Optional<EventRequestCount> ercOptional = erc
                    .stream()
                    .filter(er -> er.getEvent().getId() == event.getId())
                    .findAny();
            if (ercOptional.isPresent()) {
                eventShortDtoList.add(EventMapper.toEventShortDto(event, null, ercOptional.get().getRequestConfirmCount()));
                erc.remove(ercOptional.get());
            } else {
                eventShortDtoList.add(EventMapper.toEventShortDto(event, null, 0L));
            }
        }

        List<ViewStats> viewStatsList = statsService.getViewsStatsListByEventShort(eventShortDtoList);

        for (EventShortDto e : eventShortDtoList) {
            Optional<ViewStats> viewStatsOptional = viewStatsList.stream()
                    .filter(v -> v.getUri().contains(e.getId().toString()))
                    .findAny();
            if (viewStatsOptional.isPresent()) {
                e.setViews(viewStatsOptional.get().getHits());
                viewStatsList.remove(viewStatsOptional.get());
            } else e.setViews(0);
        }
        return eventShortDtoList;
    }

    @Transactional
    //Изменение события, добавленного текущим пользователем
    public EventFullDto updateEvent(long userId, UpdateEventRequest updateEventRequest) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException(String.format("Пользователь c id = %d не найден", userId)));
        Event eventDB = eventRepository.findById(updateEventRequest.getEventId())
                .orElseThrow(() -> new NoSuchElementException(String.format("Событие не найдено")));

        Category category = categoryRepository.findById(updateEventRequest.getCategory())
                .orElseThrow(() -> new NoSuchElementException(String.format("Категория c id = %d не найдена", updateEventRequest.getCategory())));

        Event eventUpdate = EventMapper.toUpdateEvent(category, updateEventRequest, eventDB);

        Event event = eventRepository.save(eventUpdate);

        Integer views = statsService.getViewsForOneEvent(event, true);
        Long confirmedRequests = requestRepository.countByEventIdAndStatus(event.getId(), RequestStatus.CONFIRMED);

        return EventMapper.toEventFullDto(event, views, confirmedRequests);
    }

    @Transactional
    //Добавление нового события
    public EventFullDto addEvent(long userId, NewEventDto newEventDto) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException("Пользователь не найден"));

        Category category = categoryRepository.findById(newEventDto.getCategory()).orElseThrow();

        LocalDateTime createdOn = LocalDateTime.now();
        //дата и время на которые намечено событие не может быть раньше, чем через два часа от текущего момента
        Validation.eventDateValidation(LocalDateTime.parse(newEventDto.getEventDate(), DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        Event event = EventMapper.toEvent(newEventDto, category, createdOn, user);

        return EventMapper.toEventFullDto(eventRepository.save(event), null, null);
    }

    //Получение полной информации о событии, добавленном текущим пользователем
    @Transactional(readOnly = true)
    public EventFullDto findEventById(long userId, long eventId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException("Пользователь не найден"));

        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NoSuchElementException("Событие не найдено"));

        if (userId != event.getInitiator().getId()) {
            throw new ValidationException("Нет прав: событие создано другим пользователю");
        }

        Integer views = statsService.getViewsForOneEvent(event, true);

        Long confirmedRequests = requestRepository.countByEventIdAndStatus(eventId, RequestStatus.CONFIRMED);

        return EventMapper.toEventFullDto(event, views, confirmedRequests);
    }

    //Отмена события, добавленного текущим пользователем
    @Transactional
    public EventFullDto canceledEventById(long userId, long eventId) {
        //Отменить можно только событие в состоянии ожидания модерации.

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException("Пользователь не найден"));

        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NoSuchElementException("Событие не найдено"));

        if (event.getInitiator().getId() != userId) {
            throw new ViolationRuleException(
                    String.format("Пользователь с id = %d не является инициатором события с id = %d", userId, eventId));
        }

        if (event.getState() != State.PENDING) {
            throw new ViolationRuleException(
                    String.format("Событие не может быть завершено, т.к. его статус = %s", event.getState().toString()));
        }

        event.setState(State.CANCELED);
        Event eventNew = eventRepository.save(event);
        Integer views = statsService.getViewsForOneEvent(eventNew, true);
        Long confirmedRequests = requestRepository.countByEventIdAndStatus(eventId, RequestStatus.CONFIRMED);

        return EventMapper.toEventFullDto(eventNew, views, confirmedRequests);
    }

    //Получение информации о запросах на участие в событии текущего пользователя
    @Transactional(readOnly = true)
    public List<RequestDto> getParticipationRequest(long userId, long eventId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException("Пользователь не найден"));

        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NoSuchElementException("Событие не найдено"));

        if (event.getInitiator().getId() != userId) {
            throw new ViolationRuleException(
                    String.format("Пользователь с id = %d не является инициатором события с id = %d", userId, eventId));
        }

        List<Request> requestList = requestRepository.findRequestByEventId(eventId);

        return requestList.stream()
                .map(RequestMapper::toRequestDto)
                .collect(Collectors.toList());
    }

    //Подтверждение чужой заявки на участие в событии текущего пользователя
    @Transactional
    public RequestDto confirmParticipationRequest(long userId, long eventId, long reqId) {
        /*
        Если для события лимит заявок равен 0 или отключена пре-модерация заявок, то подтверждение заявок не требуется.
        Нельзя подтвердить заявку, если уже достигнут лимит по заявкам на данное событие
        Если при подтверждении данной заявки, лимит заявок для события исчерпан,
        то все неподтверждённые заявки необходимо отклонить
        */
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException("Пользователь не найден"));
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NoSuchElementException("Событие не найдено"));
        Request request = requestRepository.findById(reqId)
                .orElseThrow(() -> new NoSuchElementException("Запрос не найден"));

        if (event.getInitiator().getId() != userId) {
            throw new ViolationRuleException("Нет прав: событие создано другим пользователю");
        }
        if (request.getEvent().getId() != eventId) {
            throw new ViolationRuleException(
                    String.format("Запрос на участие с id = %d не принадлежит событию с id = %d", reqId, eventId));
        }
        if (event.getPublishedOn().equals(false)) {
            throw new ViolationRuleException(String.format("Событие с id = %d не опубликовано", eventId));
        }

        Long confirmedRequests = requestRepository.countByEventIdAndStatus(eventId, RequestStatus.CONFIRMED);


        if (event.getParticipantLimit() == 0) {
            request.setStatus(RequestStatus.CONFIRMED);
            Request requestNew = requestRepository.save(request);
            return RequestMapper.toRequestDto(requestNew);
        }

        //Если количество одобренных заявок меньше количества разрешенных
        if (confirmedRequests < event.getParticipantLimit()) {
            request.setStatus(RequestStatus.CONFIRMED);
            Request requestNew = requestRepository.save(request);
            //Проверяем количество одобренных заявок
            //Если после одобрения заявки количество одобренных заявок равно количеству разрешенных
            //то все остальные заявки переводим в статус отклоненных
            if ((confirmedRequests + 1) == event.getParticipantLimit()) {
                List<Request> requestList = requestRepository.findRequestByEventIdAndStatus(eventId, RequestStatus.PENDING);
                for (Request req : requestList) {
                    req.setStatus(RequestStatus.REJECTED);
                }
                requestRepository.saveAll(requestList);
            }
            return RequestMapper.toRequestDto(requestNew);
        } else {
            throw new ViolationRuleException(
                    String.format("Для события с id = %d достигнут лимит участников", eventId));
        }
    }

    //Отклонение чужой заявки на участие в событии текущего пользователя
    @Transactional
    public RequestDto rejectParticipationRequest(long userId, long eventId, long reqId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException("Пользователь не найден"));
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NoSuchElementException("Событие не найдено"));
        Request request = requestRepository.findById(reqId)
                .orElseThrow(() -> new NoSuchElementException("Запрос не найден"));

        if (event.getInitiator().getId() != userId) {
            throw new ValidationException("Нет прав: событие создано другим пользователю");
        }
        if (request.getEvent().getId() != eventId) {
            throw new ValidationException(
                    String.format("Запрос на участие с id = %d не принадлежит событию с id = %d", reqId, eventId));
        }
        request.setStatus(RequestStatus.REJECTED);
        return RequestMapper.toRequestDto(requestRepository.save(request));
    }


    //Private: Запросы на участие

    //Получение информации о заявках текущего пользователя на участие в чужих событиях
    @Transactional
    public List<ParticipationRequestDto> getMyRequests(long userId) {
        userRepository.findById(userId);
        List<Request> requestList = requestRepository.findByRequesterId(userId);
        List<ParticipationRequestDto> participationRequestDtoList =
                requestList.stream().map(RequestMapper::toParticipationRequestDto).collect(Collectors.toList());
        return participationRequestDtoList;
    }

    //Добавление запроса от текущего пользователя на участие в событии
    @Transactional
    public ParticipationRequestDto addRequest(long userId, long eventId) {
        /*
        Нельзя добавить повторный запрос
        Инициатор события не может добавить запрос на участие в своём событии
        Нельзя участвовать в неопубликованном событии
        Если у события достигнут лимит запросов на участие - необходимо вернуть ошибку
        Если для события отключена пре-модерация запросов на участие,
        то запрос должен автоматически перейти в состояние подтвержденного
        */
        Request request;
        User requester = userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException("Пользователь не найден"));

        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NoSuchElementException("Событие не найдено"));

        Long requestCount = requestRepository.countByEventIdAndStatus(eventId, RequestStatus.CONFIRMED);

        if (!event.getState().equals(State.PUBLISHED)) {
            //Нельзя участвовать в неопубликованном событии
            throw new ViolationRuleException("Нельзя участвовать в неопубликованном событии");

        }
        if (event.getInitiator().getId() == userId) {
            //Инициатор события не может добавить запрос на участие в своём событии
            throw new ViolationRuleException("Инициатор события не может добавить запрос на участие в своём событии");

        }
        if (requestRepository.findRequestByEventIdAndRequesterId(eventId, userId) != null) {
            //Запрос на участие в событии с ID пользователя с ID уже есть
            throw new ViolationRuleException(String
                    .format("Запрос на участие в событии с ID = %d пользователя с ID = %d уже есть", eventId, userId));
        }
        if (event.getParticipantLimit() > 0 && requestCount == event.getParticipantLimit()) {
            //У события достигнут лимит запросов на участие - необходимо вернуть ошибку
            throw new ViolationRuleException("У события достигнут лимит запросов на участие");
        }
        //Если пре-модерация отсутствует - заявка подтверждается автоматически
        if (!event.getRequestModeration()) {
            request = RequestMapper.toRequest(event, requester, LocalDateTime.now(), RequestStatus.CONFIRMED);
            return RequestMapper.toParticipationRequestDto(requestRepository.save(request));
        }

        //Если пре-модерация присутствует - для заявки устанавливается статус PENDING
        request = RequestMapper.toRequest(event, requester, LocalDateTime.now(), RequestStatus.PENDING);
        return RequestMapper.toParticipationRequestDto(requestRepository.save(request));
    }

    @Transactional
    //Отмена своего запроса на участие в событии
    public ParticipationRequestDto cancelRequest(long userId, long reqId) {

        User requester = userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException("Пользователь не найден"));

        Request request = requestRepository
                .findById(reqId).orElseThrow(() -> new NoSuchElementException("Запрос не найден"));

        if (request.getRequester().getId() != userId) {
            //Нет прав: запрос принадлежит другому участнику
            throw new ViolationRuleException("Нет прав: запрос принадлежит другому участнику");
        }

        request.setStatus(RequestStatus.CANCELED);

        return RequestMapper.toParticipationRequestDto(requestRepository.save(request));
    }
}
