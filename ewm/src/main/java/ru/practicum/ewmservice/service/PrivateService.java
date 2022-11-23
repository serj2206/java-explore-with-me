package ru.practicum.ewmservice.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.ewmservice.client.PublicClient;
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
import ru.practicum.ewmservice.model.request.dto.ParticipationRequestDto;
import ru.practicum.ewmservice.model.request.mapping.RequestMapping;
import ru.practicum.ewmservice.model.statistic.ViewStats;
import ru.practicum.ewmservice.model.user.User;
import ru.practicum.ewmservice.repository.CategoryRepository;
import ru.practicum.ewmservice.repository.EventRepository;
import ru.practicum.ewmservice.repository.RequestRepository;
import ru.practicum.ewmservice.repository.UserRepository;
import ru.practicum.ewmservice.validation.Validation;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class PrivateService {

    private final EventRepository eventRepository;
    private final RequestRepository requestRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final PublicClient publicClient;


    //Pivate: События

    //Получение событий, добавленных текущим пользователем
    public List<EventShortDto> getEvent(long userId, int from, int size) {

        return null;
    }

    //Изменение события, добавленного текущим пользователем
    public EventFullDto updateEvent(long userId, UpdateEventRequest updateEventRequest) {

        return null;
    }

    //Добавление нового события
    public EventFullDto addEvent(long userId, NewEventDto newEventDto) {

        User user = userRepository.findById(userId).orElseThrow();
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
                .orElseThrow(() -> new NoSuchElementException("Событие не найден"));

        if (userId != event.getInitiator().getId()) {
            throw new ValidationException("Нет прав: событие принадлежит другому участнику");
        }

        ViewStats viewStats = (ViewStats) publicClient.findStats(
                        event.getPublishedOn().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")),
                        event.getEventDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")), null, true)
                .getBody();

        Integer confirmedRequests = (int) requestRepository.count(); //Дописать запрос

        return EventMapper.toEventFullDto(event, viewStats.getHits(), confirmedRequests);
    }

    //Отмена события, добавленного текущим пользователем
    public EventFullDto canceledEventById(long userId, long eventId) {
        //Отменить можно только событие в состоянии ожидания модерации.
        return null;
    }

    //Получение информации о запросах на участие в событии текущего пользователя
    public List<ParticipationRequestDto> getParticipationRequest(long userId, long eventId) {

        return null;
    }

    //Подтверждение чужой заявки на участие в событии текущего пользователя
    public ParticipationRequestDto confirmParticipationRequest(long userId, long eventId, long reqId) {
        /*
        Если для события лимит заявок равен 0 или отключена пре-модерация заявок, то подтверждение заявок не требуется.
        Нельзя подтвердить заявку, если уже достигнут лимит по заявкам на данное событиею
        Если при подтверждении данной заявки, лимит заявок для события исчерпан,
        то все неподтверждённые заявки необходимо отклонить
        */
        return null;
    }

    //Отклонение чужой заявки на участие в событии текущего пользователя
    public ParticipationRequestDto rejectParticipationRequest(long userId, long eventId, long reqId) {

        return null;
    }


    //Private: Запросы на участие

    //Получение информации о заявках текущего пользователя на участие в чужих событиях
    public List<ParticipationRequestDto> getMyRequests(long userId) {
        userRepository.findById(userId);
        List<Request> requestList = requestRepository.findByRequesterId(userId);
        List<ParticipationRequestDto> participationRequestDtoList =
                requestList.stream().map(RequestMapping::toParticipationRequestDto).collect(Collectors.toList());
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
        if (requestCount >= event.getParticipantLimit()) {
            //У события достигнут лимит запросов на участие - необходимо вернуть ошибку
            throw new ViolationRuleException("У события достигнут лимит запросов на участие");
        }
        //Если пре-модерация отсутствует - заявка подтверждается автоматически
        if (!event.isRequestModeration()) {
            request = RequestMapping.toRequest(event, requester, LocalDateTime.now(), RequestStatus.CONFIRM);
            return RequestMapping.toParticipationRequestDto(requestRepository.save(request));
        }

        //Если пре-модерация присутствует - для заявки устанавливается статус PENDING
        request = RequestMapping.toRequest(event, requester, LocalDateTime.now(), RequestStatus.PENDING);
        return RequestMapping.toParticipationRequestDto(requestRepository.save(request));
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

        return RequestMapping.toParticipationRequestDto(requestRepository.save(request));
    }
}
