package ru.practicum.ewmservice.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.ewmservice.client.PublicClient;
import ru.practicum.ewmservice.exceptions.ValidationException;
import ru.practicum.ewmservice.model.category.Category;
import ru.practicum.ewmservice.model.event.Event;
import ru.practicum.ewmservice.model.event.dto.EventFullDto;
import ru.practicum.ewmservice.model.event.dto.EventShortDto;
import ru.practicum.ewmservice.model.event.dto.NewEventDto;
import ru.practicum.ewmservice.model.event.dto.UpdateEventRequest;
import ru.practicum.ewmservice.model.event.mapper.EventMapper;
import ru.practicum.ewmservice.model.request.Request;
import ru.practicum.ewmservice.model.request.dto.ParticipationRequestDto;
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

@RequiredArgsConstructor
@Service
public class PrivateService {

    private final EventRepository eventRepository;
    private final RequestRepository requestRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final PublicClient publicClient;


    //Public: События

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

        userRepository.findById(userId);

        Event event = eventRepository.findById(eventId).orElseThrow();

        if (userId != event.getInitiator().getId()) throw new ValidationException("");

        ViewStats viewStats = (ViewStats) publicClient.findStats(
                        event.getPublishedOn().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")),
                        event.getEventDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")), 0, 1)
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


    //Public: Запросы на участие

    //Получение информации о заявках текущего пользователя на участие в чужих событиях
    public List<ParticipationRequestDto> getMyRequests(long userId) {

        return null;
    }

    //Добавление запроса от текущего пользователя на участие в событии
    public ParticipationRequestDto addRequest(long userId, long reqId) {
        /*
        Нельзя добавить повторный запрос
        Инициатор события не может добавить запрос на участие в своём событии
        Нельзя участвовать в неопубликованном событии
        Если у события достигнут лимит запросов на участие - необходимо вернуть ошибку
        Если для события отключена пре-модерация запросов на участие,
        то запрос должен автоматически перейти в состояние подтвержденного
        */
        Request request = requestRepository.findRequestById(reqId);

        return null;
    }

    //Отмена своего запроса на участие в событии
    public ParticipationRequestDto cancelRequest(long userId, long reqId) {

        Request request = requestRepository.findRequestById(reqId);
        return null;
    }
}
