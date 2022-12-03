package ru.practicum.ewmservice.conrollers;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewmservice.common.marker.Create;
import ru.practicum.ewmservice.common.marker.Update;
import ru.practicum.ewmservice.model.event.dto.EventFullDto;
import ru.practicum.ewmservice.model.event.dto.EventShortDto;
import ru.practicum.ewmservice.model.event.dto.NewEventDto;
import ru.practicum.ewmservice.model.event.dto.UpdateEventRequest;
import ru.practicum.ewmservice.model.request.dto.ParticipationRequestDto;
import ru.practicum.ewmservice.model.request.dto.RequestDto;
import ru.practicum.ewmservice.service.PrivateService;

import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping(path = "/users")
@Slf4j
@Validated
public class PrivateController {
    private final PrivateService privateService;


    //Private: События

    //Получение событий, добавленных текущим пользователем
    @GetMapping("/{userId}/events")
    public List<EventShortDto> getEvent(@Positive @PathVariable Long userId,
                                        @PositiveOrZero @RequestParam(defaultValue = "0") Integer from,
                                        @Positive @RequestParam(defaultValue = "10") Integer size) {
        log.info("PrivateController: GET /users/{}/events: getEvent() userId = {}, from = {}, size = {}",
                userId, userId, from, size);
        return privateService.getEvent(userId, from, size);
    }

    //Изменение события, добавленного текущим пользователем
    @PatchMapping("/{userId}/events")
    public EventFullDto updateEvent(@Positive @PathVariable Long userId,
                                    @Validated({Update.class}) @RequestBody UpdateEventRequest updateEventRequest) {
        log.info("PrivateController: PATCH /users/{}/events: updateEvent() userId = {}, updateEventRequest = {}",
                userId, userId, updateEventRequest);
        return privateService.updateEvent(userId, updateEventRequest);
    }

    //Добавление нового события
    @PostMapping("/{userId}/events")
    public EventFullDto addEvent(@Positive @PathVariable Long userId,
                                 @Validated({Create.class}) @RequestBody NewEventDto newEventDto) {
        log.info("PrivateController: POST /users/{}/events: addEvent() userId = {}, newEventDto = {}",
                userId, userId, newEventDto);
        return privateService.addEvent(userId, newEventDto);
    }

    //Получение полной информации о событии, добавленном текущим пользователем
    @GetMapping("/{userId}/events/{eventId}")
    public EventFullDto findEventById(@Positive @PathVariable Long userId,
                                      @Positive @PathVariable Long eventId) {
        log.info("PrivateController: GET /users/{}/events/{}: findEventById() userId = {}, eventId = {}",
                userId, eventId, userId, eventId);
        return privateService.findEventById(userId, eventId);
    }

    //Отмена события, добавленного текущим пользователем
    @PatchMapping("/{userId}/events/{eventId}")
    public EventFullDto canceledEventById(@Positive @PathVariable Long userId,
                                          @Positive @PathVariable Long eventId) {
        log.info("PrivateController: PATCH /users/{}/events/{}: canceledEventById() userId = {}, eventId = {}",
                userId, eventId, userId, eventId);
        return privateService.canceledEventById(userId, eventId);
    }

    //Получение информации о запросах на участие в событии текущего пользователя
    @GetMapping("/{userId}/events/{eventId}/requests")
    public List<RequestDto> getParticipationRequest(@Positive @PathVariable Long userId,
                                                    @Positive @PathVariable Long eventId) {
        log.info("PrivateController: GET /users/{}/events/{}/requests: getParticipationRequest() userId = {}, eventId = {}",
                userId, eventId, userId, eventId);
        return privateService.getParticipationRequest(userId, eventId);
    }

    //Подтверждение чужой заявки на участие в событии текущего пользователя
    @PatchMapping("/{userId}/events/{eventId}/requests/{reqId}/confirm")
    public RequestDto confirmParticipationRequest(@Positive @PathVariable Long userId,
                                                  @Positive @PathVariable Long eventId,
                                                  @Positive @PathVariable Long reqId) {
        log.info("PrivateController: PATCH /users/{}/events/{}/requests/{}/confirm: confirmParticipationRequest() userId = {}, eventId = {}, reqId ={}",
                userId, eventId, reqId, userId, eventId, reqId);
        return privateService.confirmParticipationRequest(userId, eventId, reqId);
    }

    //Отклонение чужой заявки на участие в событии текщего пользователя
    @PatchMapping("/{userId}/events/{eventId}/requests/{reqId}/reject")
    public RequestDto rejectParticipationRequest(@Positive @PathVariable Long userId,
                                                 @Positive @PathVariable Long eventId,
                                                 @Positive @PathVariable Long reqId) {
        log.info("PrivateController: PATCH /users/{}/events/{}/requests/{}/reject: confirmParticipationRequest() userId = {}, eventId = {}, reqId ={}",
                userId, eventId, reqId, userId, eventId, reqId);
        return privateService.rejectParticipationRequest(userId, eventId, reqId);
    }


    //Private: Запросы на участие

    //Получение информации о заявках текущего пользователя на участие в чужих событиях
    @GetMapping("/{userId}/requests")
    public List<ParticipationRequestDto> getMyRequests(@Positive @PathVariable Long userId) {
        log.info("PrivateController: GET /users/{}/requests : getMyRequests() userId = {}", userId, userId);
        return privateService.getMyRequests(userId);

    }

    //Добавление запроса от текущего пользователя на участие в событии
    @PostMapping("/{userId}/requests")
    public ParticipationRequestDto addRequest(@Positive @PathVariable Long userId,
                                              @Positive @RequestParam Long eventId) {
        log.info("PrivateController: POST /users/{}/requests : addRequest() userId = {}, eventID = {}",
                userId, userId, eventId);
        return privateService.addRequest(userId, eventId);
    }

    //Отмена своего запроса на участие в событии
    @PatchMapping("/{userId}/requests/{reqId}/cancel")
    public ParticipationRequestDto cancelRequest(@Positive @PathVariable Long userId,
                                                 @Positive @PathVariable Long reqId) {
        log.info("PrivateController: PATCH /users/{}/requests/{}/cancel : cancelRequest() userId = {}, reqID = {}",
                userId, reqId, userId, reqId);
        return privateService.cancelRequest(userId, reqId);
    }
}
