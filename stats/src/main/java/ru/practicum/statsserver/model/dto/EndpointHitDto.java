package ru.practicum.statsserver.model.dto;

import lombok.*;
import ru.practicum.statsserver.common.marker.Create;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@EqualsAndHashCode
@ToString
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor

public class EndpointHitDto {

    @NotNull(groups = {Create.class})
    @NotBlank(groups = {Create.class})
    private String app;

    @NotNull(groups = {Create.class})
    @NotBlank(groups = {Create.class})
    private String uri;

    @NotNull(groups = {Create.class})
    @NotBlank(groups = {Create.class})
    private String ip;

    @NotNull(groups = {Create.class})
    private String timestamp;
}
