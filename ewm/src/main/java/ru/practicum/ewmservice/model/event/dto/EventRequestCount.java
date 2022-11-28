package ru.practicum.ewmservice.model.event.dto;

import lombok.*;
import ru.practicum.ewmservice.model.event.Event;

@EqualsAndHashCode
@ToString
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class EventRequestCount {
    private Event event;

    private Long requestConfirmCount;
}
