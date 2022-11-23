package ru.practicum.ewmservice.model.request.mapping;

import ru.practicum.ewmservice.model.event.Event;
import ru.practicum.ewmservice.model.request.Request;
import ru.practicum.ewmservice.model.request.RequestStatus;
import ru.practicum.ewmservice.model.request.dto.ParticipationRequestDto;
import ru.practicum.ewmservice.model.user.User;

import java.time.LocalDateTime;

public class RequestMapping {
    public static Request toRequest(Event event, User requester, LocalDateTime created, RequestStatus requestStatus) {
        return new Request(null, event, requester, created, requestStatus);
    }

    public static ParticipationRequestDto toParticipationRequestDto (Request request) {
        return new ParticipationRequestDto(request);
    }
}
