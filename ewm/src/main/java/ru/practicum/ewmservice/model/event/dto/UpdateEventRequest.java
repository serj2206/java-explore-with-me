package ru.practicum.ewmservice.model.event.dto;

import lombok.*;
import ru.practicum.ewmservice.common.marker.Update;

import javax.validation.constraints.Positive;

@EqualsAndHashCode
@ToString
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UpdateEventRequest {

    @Positive(groups = {Update.class})
    private Long eventId;

    private String title;

    private String annotation;

    private String description;

    private Integer category;

    private Boolean paid;

    private Long participantLimit;

    private String eventDate;

}
