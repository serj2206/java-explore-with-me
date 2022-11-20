package ru.practicum.ewmservice.model.request.dto;

import lombok.*;
import ru.practicum.ewmservice.model.event.Event;
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

    private long id;

    private Event event;

    private User requester;

    private LocalDateTime created;

    private RequestStatus status;
}
