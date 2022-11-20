package ru.practicum.ewmservice.model.categorie.dto;

import lombok.*;
import ru.practicum.ewmservice.common.marker.Update;
import ru.practicum.ewmservice.model.categorie.Category;

import javax.validation.constraints.Pattern;
import javax.validation.constraints.Positive;

@EqualsAndHashCode
@ToString
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CategoryDto {

    @Positive(groups = {Update.class})
    private int id;

    private String name;

    public CategoryDto(Category category) {
        this.id = category.getId();
        this.name = category.getName();
    }
}
