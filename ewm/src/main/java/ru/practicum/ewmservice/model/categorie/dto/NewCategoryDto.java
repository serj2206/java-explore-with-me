package ru.practicum.ewmservice.model.categorie.dto;

import lombok.*;
import ru.practicum.ewmservice.common.marker.Create;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

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
