package ru.practicum.ewmservice.model.request.dto;

import lombok.*;
import ru.practicum.ewmservice.model.event.Event;
import ru.practicum.ewmservice.model.request.Request;
import ru.practicum.ewmservice.model.request.RequestStatus;
import ru.practicum.ewmservice.model.user.User;

import java.time.LocalDateTime;

@EqualsAndHashCode
@ToString
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ParticipationRequestDto {

    private Long id;

    private Long event;

    private Long requester;

    private LocalDateTime created;

    private RequestStatus status;

    public ParticipationRequestDto(Request request) {
        this.id = request.getId();
        this.event = request.getEvent().getId();
        this.requester = request.getRequester().getId();
        this.created = request.getCreated();
        this.status = request.getStatus();
    }
}
