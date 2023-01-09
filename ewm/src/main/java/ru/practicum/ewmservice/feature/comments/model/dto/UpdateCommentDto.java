package ru.practicum.ewmservice.feature.comments.model.dto;

import lombok.*;
import ru.practicum.ewmservice.common.marker.Update;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@ToString
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UpdateCommentDto {
    @NotBlank(groups = {Update.class})
    @NotNull(groups = {Update.class})
    private String text;
}
