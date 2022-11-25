package ru.practicum.ewmservice.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.ewmservice.client.PublicClient;
import ru.practicum.ewmservice.common.FromSizeRequest;
import ru.practicum.ewmservice.exceptions.ViolationRuleException;
import ru.practicum.ewmservice.exceptions.ValidationException;
import ru.practicum.ewmservice.model.category.Category;
import ru.practicum.ewmservice.model.event.Event;
import ru.practicum.ewmservice.model.event.State;
import ru.practicum.ewmservice.model.event.dto.EventFullDto;
import ru.practicum.ewmservice.model.event.dto.EventShortDto;
import ru.practicum.ewmservice.model.event.dto.NewEventDto;
import ru.practicum.ewmservice.model.event.dto.UpdateEventRequest;
import ru.practicum.ewmservice.model.event.mapper.EventMapper;
import ru.practicum.ewmservice.model.request.Request;
import ru.practicum.ewmservice.model.request.RequestStatus;
import ru.practicum.ewmservice.model.event.dto.EventRequestCount;
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


    //Private: События

    //Получение событий, добавленных текущим пользователем
    public List<EventShortDto> getEvent(long userId, int from, int size) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException(String.format("Пользователь c id = %d не найден", userId)));
        List<EventShortDto> eventShortDtoList;

        Sort sortById = Sort.by(Sort.Direction.ASC, "id");

        Pageable pageable = FromSizeRequest.of(from, size, sortById);

        Page<EventRequestCount> eventRequestCountPage = eventRepository.findEventByInitiatorId(userId, pageable);

        return this.toEventShortDto(eventRequestCountPage);
    }

    private List<EventShortDto> toEventShortDto(Page<EventRequestCount> eventRequestCountPage) {

        //Самая ранняя дата, когда было cоздано событие из списка событий
        LocalDateTime start = eventRequestCountPage.stream()
                .min(Comparator.comparing(erc -> erc.getEvent().getCreatedOn()))
                .get()
                .getEvent()
                .getPublishedOn();

        //Самая поздняя дата, когда состоится событие из списка событий
        LocalDateTime end = eventRequestCountPage.stream()
                .max(Comparator.comparing(erc -> erc.getEvent().getEventDate()))
                .get()
                .getEvent()
                .getPublishedOn();

        StringBuilder urisBuilder = new StringBuilder();
        for (EventRequestCount erc : eventRequestCountPage) {
            urisBuilder.append("/event/").append(erc.getEvent().getId()).append(", ");
        }
        String uris = urisBuilder.toString();

        //Запрос списка статистической информации
        List<ViewStats> viewStatsList = publicClient.findStats(
                start.toString(),
                end.toString(),
                uris,
                true);

        List<EventShortDto> eventShortDtoList = new ArrayList<>();

        for (EventRequestCount erc : eventRequestCountPage) {
            Optional<ViewStats> viewStatsOptional = viewStatsList.stream()
                    .filter(v -> v.getUri().contains(erc.getEvent().getId().toString()))
                    .findAny();
            int views = 0;
            if (viewStatsOptional.isPresent()) {
                views = viewStatsOptional.get().getHits();
            }
            eventShortDtoList.add(EventMapper.eventShortDto(erc, views));
        }

        return eventShortDtoList;
    }

    //Изменение события, добавленного текущим пользователем
    public EventFullDto updateEvent(long userId, UpdateEventRequest updateEventRequest) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException(String.format("Пользователь c id = %d не найден", userId)));

        Category category = categoryRepository.findById(updateEventRequest.getCategory())
                .orElseThrow(() -> new NoSuchElementException(String.format("Категория c id = %d не найдена", updateEventRequest.getCategory())));

        Event eventUpdate = EventMapper.toUpdateEvent(category, updateEventRequest);

        Event event = eventRepository.save(eventUpdate);
        LocalDateTime start = eventUpdate.getPublishedOn();
        LocalDateTime end = eventUpdate.getEventDate();
        String uris = "/event/" + eventUpdate.getId();

        List<ViewStats> viewStatsList = publicClient.findStats(
                start.toString(),
                end.toString(),
                uris,
                true);

        Integer views = viewStatsList.get(0).getHits();
        Integer confirmedRequests = requestRepository.countByEventIdAndStatus(eventUpdate.getId(), RequestStatus.CONFIRM);

        return EventMapper.toEventFullDto(event, views, confirmedRequests);
    }

    //Добавление нового события
    public EventFullDto addEvent(long userId, NewEventDto newEventDto) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException("Пользователь не найден"));
        Category category = categoryRepository.findById(newEventDto.getCategory()).orElseThrow();
        LocalDateTime createdOn = LocalDateTime.now();
        //дата и время на которые намечено событие не может быть раньше, чем через два часа от текущего момента
        Validation.eventDateValidation(newEventDto.getEventDate());
        Event event = EventMapper.toEvent(newEventDto, category, createdOn, user);

        return EventMapper.toEventFullDto(eventRepository.save(event), null, null);
    }

    //Получение полной информации о событии, добавленном текущим пользователем
    public EventFullDto findEventById(long userId, long eventId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException("Пользователь не найден"));

        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NoSuchElementException("Событие не найдено"));

        if (userId != event.getInitiator().getId()) {
            throw new ValidationException("Нет прав: событие создано другим пользователю");
        }
        Integer views = this.toViews(event, true);

        Integer confirmedRequests = requestRepository.countByEventIdAndStatus(eventId, RequestStatus.CONFIRM);

        return EventMapper.toEventFullDto(event, views, confirmedRequests);
    }

    private Integer toViews(Event event, Boolean unique) {
        String uris = "/event/" + event.getId();

        List<ViewStats> viewStatsList = publicClient.findStats(
                event.getPublishedOn().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")),
                event.getEventDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")), uris, unique);

        if (viewStatsList == null) return null;
        Integer views = 0;
        for (ViewStats vs : viewStatsList) {
            views += vs.getHits();
        }
        return views;
    }

    //Отмена события, добавленного текущим пользователем
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
            event.setState(State.CANCELED);
            event = eventRepository.save(event);
        } else throw new ViolationRuleException(
                String.format("Событие не может быть завершено, т.к. его статус = %s", event.getState().toString()));

        Integer views = this.toViews(event, true);
        Integer confirmedRequests = requestRepository.countByEventIdAndStatus(eventId, RequestStatus.CONFIRM);

        return EventMapper.toEventFullDto(event, views, confirmedRequests);
    }

    //Получение информации о запросах на участие в событии текущего пользователя
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
            throw new ValidationException("Нет прав: событие создано другим пользователю");
        }
        if (request.getEvent().getId() != eventId) {
            throw new ValidationException(
                    String.format("Запрос на участие с id = %d не принадлежит событию с id = %d", reqId, eventId));
        }
        if (event.isRequestModeration() || event.getParticipantLimit() == 0) {
            throw new ValidationException(
                    String.format("Подтверждение заявок на участие в событии с id = %d не требуется", eventId));
        }

        Integer confirmedRequests = requestRepository.countByEventIdAndStatus(eventId, RequestStatus.CONFIRM);
        if (confirmedRequests < event.getParticipantLimit()) {
            request.setStatus(RequestStatus.CONFIRM);
            request = requestRepository.save(request);
            confirmedRequests = requestRepository.countByEventIdAndStatus(eventId, RequestStatus.CONFIRM);

            if (confirmedRequests == event.getParticipantLimit()) {
                List<Request> requestList = requestRepository.findRequestByEventIdAndStatus(eventId, RequestStatus.PENDING);
                for (Request req : requestList) {
                    req.setStatus(RequestStatus.REJECTED);
                }
                requestRepository.saveAll(requestList);
            }

            return RequestMapper.toRequestDto(request);
        } else {
            throw new ValidationException(
                    String.format("Для события с id = %d достигнут лимит участников", eventId));
        }
    }

    //Отклонение чужой заявки на участие в событии текущего пользователя
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
    public List<ParticipationRequestDto> getMyRequests(long userId) {
        userRepository.findById(userId);
        List<Request> requestList = requestRepository.findByRequesterId(userId);
        List<ParticipationRequestDto> participationRequestDtoList =
                requestList.stream().map(RequestMapper::toParticipationRequestDto).collect(Collectors.toList());
        return participationRequestDtoList;
    }

    //Добавление запроса от текущего пользователя на участие в событии
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

        Integer requestCount = requestRepository.countByEventIdAndStatus(eventId, RequestStatus.CONFIRM);

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
        if (!event.isRequestModeration()) {
            request = RequestMapper.toRequest(event, requester, LocalDateTime.now(), RequestStatus.CONFIRM);
            return RequestMapper.toParticipationRequestDto(requestRepository.save(request));
        }

        //Если пре-модерация присутствует - для заявки устанавливается статус PENDING
        request = RequestMapper.toRequest(event, requester, LocalDateTime.now(), RequestStatus.PENDING);
        return RequestMapper.toParticipationRequestDto(requestRepository.save(request));
    }

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

        request.setStatus(RequestStatus.СANCELLED);

        return RequestMapper.toParticipationRequestDto(requestRepository.save(request));
    }
}
