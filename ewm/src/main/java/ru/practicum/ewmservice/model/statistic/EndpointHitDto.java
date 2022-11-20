package ru.practicum.ewmservice.model.statistic;

import lombok.*;
import ru.practicum.ewmservice.common.marker.Create;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

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
    private LocalDateTime timestamp;
}
