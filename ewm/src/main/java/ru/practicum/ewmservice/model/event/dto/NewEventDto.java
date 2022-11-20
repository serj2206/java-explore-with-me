package ru.practicum.ewmservice.model.event.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

import lombok.*;
import ru.practicum.ewmservice.common.marker.Create;
import ru.practicum.ewmservice.model.location.Location;

import java.time.LocalDateTime;

@EqualsAndHashCode
@ToString
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class NewEventDto {

    @NotNull(groups = {Create.class})
    @NotBlank(groups = {Create.class})
    private String title;

    @NotNull(groups = {Create.class})
    @NotBlank(groups = {Create.class})
    private String annotation;

    @NotNull(groups = {Create.class})
    @NotBlank(groups = {Create.class})
    private String description;

    @Positive(groups = {Create.class})
    private int category;

    private Boolean paid;

    @NotNull(groups = {Create.class})
    private Location location;

    @NotNull(groups = {Create.class})
    private LocalDateTime eventDate;

    private Boolean requestModeration;

    private Integer participantLimit;
}
