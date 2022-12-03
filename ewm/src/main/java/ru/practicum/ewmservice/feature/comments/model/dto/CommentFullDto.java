package ru.practicum.ewmservice.feature.comments.model.dto;

import lombok.*;
import ru.practicum.ewmservice.feature.comments.model.CommentStatus;
import ru.practicum.ewmservice.model.event.dto.EventForCommentDto;
import ru.practicum.ewmservice.model.user.dto.UserDto;

@ToString
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CommentFullDto {
    private Long id;
    private String text;
    private UserDto commentator;
    private EventForCommentDto eventForCommentDto;
    private String createdOn;
    private CommentStatus commentStatus;
    private Boolean commentChanged;
}


