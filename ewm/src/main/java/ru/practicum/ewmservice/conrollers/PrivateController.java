package ru.practicum.ewmservice.conrollers;

import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewmservice.model.event.dto.EventFullDto;
import ru.practicum.ewmservice.model.event.dto.EventShortDto;
import ru.practicum.ewmservice.model.event.dto.NewEventDto;
import ru.practicum.ewmservice.model.event.dto.UpdateEventRequest;
import ru.practicum.ewmservice.model.request.dto.ParticipationRequestDto;
import ru.practicum.ewmservice.service.PrivateService;

import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@RequiredArgsConstructor
@RestController("/users")
@Validated
public class PrivateController {
    private final PrivateService privateService;


    //Private: События

    //Получение событий, добавленных текущим пользователем
    @GetMapping("/{userId}/events")
    public List<EventShortDto> getEvent(@Positive @PathVariable Long userId,
                                        @PositiveOrZero @RequestParam(defaultValue = "0") Integer from,
                                        @Positive @RequestParam(defaultValue = "10") Integer size) {

        return privateService.getEvent(userId, from, size);
    }

    //Изменение события, добавленного текущим пользователем
    @PatchMapping("/{userId}/events")
    public EventFullDto updateEvent(@Positive @PathVariable Long userId,
                                    @Validated @RequestBody UpdateEventRequest updateEventRequest) {

        return privateService.updateEvent(userId, updateEventRequest);
    }

    //Добавление нового события
    @PostMapping("/{userId}/events")
    public EventFullDto addEvent(@Positive @PathVariable Long userId,
                                 @Validated @RequestBody NewEventDto newEventDto) {

        return privateService.addEvent(userId, newEventDto);
    }

    //Получение полной информации о событии, добавленном текущим пользователем
    @GetMapping("/{userId}/events/{eventId}")
    public EventFullDto findEventById(@Positive @PathVariable Long userId,
                                      @Positive @PathVariable Long eventId) {

        return privateService.findEventById(userId, eventId);
    }

    //Отмена события, добавленного текущим пользователем
    @PatchMapping("/{userId}/events/{eventId}")
    public EventFullDto canceledEventById(@Positive @PathVariable Long userId,
                                          @Positive @PathVariable Long eventId) {

        return privateService.canceledEventById(userId, eventId);
    }

    //Получение информации о запросах на участие в событии текущего пользователя
    @GetMapping("/{userId}/events/{eventId}/requests")
    public List<ParticipationRequestDto> getParticipationRequest(@Positive @PathVariable Long userId,
                                                                 @Positive @PathVariable Long eventId) {

        return privateService.getParticipationRequest(userId, eventId);
    }

    //Подтверждение чужой заявки на участие в событии текущего пользователя
    @GetMapping("/{userId}/events/{eventId}/requests/{reqId}/confirm")
    public ParticipationRequestDto confirmParticipationRequest(@Positive @PathVariable Long userId,
                                                               @Positive @PathVariable Long eventId,
                                                               @Positive @PathVariable Long reqId) {

        return privateService.confirmParticipationRequest(userId, eventId, reqId);
    }

    //Отклонение чужой заявки на участие в событии текщего пользователя
    @PatchMapping("/{userId}/events/{eventId}/requests/{reqId}/reject")
    public ParticipationRequestDto rejectParticipationRequest(@Positive @PathVariable Long userId,
                                                              @Positive @PathVariable Long eventId,
                                                              @Positive @PathVariable Long reqId) {

        return privateService.rejectParticipationRequest(userId, eventId, reqId);
    }


    //Private: Запросы на участие

    //Получение информации о заявках текущего пользователя на участие в чужих событиях
    @GetMapping("/{userId}/requests")
    public List<ParticipationRequestDto> getMyRequests(@Positive @PathVariable Long userId) {

        return privateService.getMyRequests(userId);

    }

    //Добавление запроса от текущего пользователя на участие в событии
    @PostMapping("/{userId}/requests")
    public ParticipationRequestDto addRequest(@Positive @PathVariable Long userId,
                                              @Positive @RequestParam Long eventId) {

        return privateService.addRequest(userId, eventId);
    }

    //Отмена своего запроса на участие в событии
    @PatchMapping("/{userId}/requests/{reqId}/cancel")
    public ParticipationRequestDto cancelRequest(@Positive @PathVariable Long userId,
                                                 @Positive @PathVariable Long reqId) {

        return privateService.cancelRequest(userId, reqId);
    }
}
