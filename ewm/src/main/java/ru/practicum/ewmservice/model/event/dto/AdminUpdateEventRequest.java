package ru.practicum.ewmservice.model.event.dto;

import lombok.*;
import ru.practicum.ewmservice.model.location.Location;

@EqualsAndHashCode
@ToString
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AdminUpdateEventRequest {

    private String annotation;

    private Integer category;

    private String description;

    private String eventDate;

    private Location location;

    private Boolean paid;

    private Long participantLimit;

    private Boolean requestModeration;

    private String title;
}
