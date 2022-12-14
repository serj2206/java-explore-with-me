package ru.practicum.ewmservice.model.compilation.dto;

import lombok.*;
import ru.practicum.ewmservice.common.marker.Create;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;


@EqualsAndHashCode
@ToString
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class NewCompilationDto {

    @NotNull(groups = {Create.class})
    private List<Long> events;

    private boolean pinned = false;

    @NotNull(groups = {Create.class})
    @NotBlank(groups = {Create.class})
    private String title;
}
