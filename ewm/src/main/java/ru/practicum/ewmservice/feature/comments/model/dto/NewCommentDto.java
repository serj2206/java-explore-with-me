package ru.practicum.ewmservice.feature.comments.model.dto;

import lombok.*;
import ru.practicum.ewmservice.common.marker.Create;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;


@ToString
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class NewCommentDto {
    @NotBlank(groups = {Create.class})
    @NotNull(groups = {Create.class})
    private String text;

}
