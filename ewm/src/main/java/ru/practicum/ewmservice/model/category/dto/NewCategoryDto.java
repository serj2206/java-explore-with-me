package ru.practicum.ewmservice.model.category.dto;

import lombok.*;
import ru.practicum.ewmservice.common.marker.Create;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@EqualsAndHashCode
@ToString
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class NewCategoryDto {


    @NotNull(groups = {Create.class})
    @NotBlank(groups = {Create.class})
    private String name;
}
