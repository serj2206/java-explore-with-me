package ru.practicum.ewmservice.feature.comments.model.dto;

import lombok.*;
import ru.practicum.ewmservice.model.event.dto.EventForCommentDto;
import ru.practicum.ewmservice.model.user.dto.UserShortDto;

@ToString
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CommentShortDto {
    private Long id;
    private String text;
    private UserShortDto commentator;
    private EventForCommentDto eventForCommentDto;
    private String createdOn;
    private Boolean commentChanged;
}

