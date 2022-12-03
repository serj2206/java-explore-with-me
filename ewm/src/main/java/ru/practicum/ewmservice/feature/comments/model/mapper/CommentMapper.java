package ru.practicum.ewmservice.feature.comments.model.mapper;

import org.springframework.stereotype.Component;
import ru.practicum.ewmservice.feature.comments.model.Comment;
import ru.practicum.ewmservice.feature.comments.model.dto.CommentFullDto;
import ru.practicum.ewmservice.feature.comments.model.dto.CommentShortDto;
import ru.practicum.ewmservice.feature.comments.model.dto.NewCommentDto;
import ru.practicum.ewmservice.model.event.Event;
import ru.practicum.ewmservice.model.event.dto.EventForCommentDto;
import ru.practicum.ewmservice.model.event.mapper.EventMapper;
import ru.practicum.ewmservice.model.user.User;
import ru.practicum.ewmservice.model.user.mapper.UserMapper;

import java.time.format.DateTimeFormatter;

@Component
public class CommentMapper {
    public static CommentFullDto toCommentFullDto(Comment comment) {

        EventForCommentDto eventForCommentDto;
        CommentFullDto commentFullDto = new CommentFullDto();

        if (comment.getEvent() != null) {
            eventForCommentDto = EventMapper.toEventForCommentDto(comment.getEvent());
        } else eventForCommentDto = null;

        commentFullDto.setId(comment.getId());
        commentFullDto.setText(comment.getText());
        commentFullDto.setCommentator(UserMapper.toUserDto(comment.getCommentator()));
        commentFullDto.setEventForCommentDto(eventForCommentDto);
        commentFullDto.setCreatedOn(comment.getCreatedOn().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        commentFullDto.setCommentChanged(comment.getCommentChanged());

        commentFullDto.setCommentStatus(comment.getCommentStatus());

        return commentFullDto;
    }

    public static CommentShortDto toCommentShortDto(Comment comment) {

        EventForCommentDto eventForCommentDto;
        CommentShortDto commentShortDto = new CommentShortDto();

        if (comment.getEvent() != null) {
            eventForCommentDto = EventMapper.toEventForCommentDto(comment.getEvent());
        } else eventForCommentDto = null;

        commentShortDto.setId(comment.getId());
        commentShortDto.setText(comment.getText());
        commentShortDto.setCommentator(UserMapper.toUserShortDto(comment.getCommentator()));
        commentShortDto.setEventForCommentDto(eventForCommentDto);
        commentShortDto.setCreatedOn(comment.getCreatedOn().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        commentShortDto.setCommentChanged(comment.getCommentChanged());

        return commentShortDto;
    }

    public static Comment toComment(NewCommentDto newCommentDto, Event event, User commentator) {
        Comment comment = new Comment();
        comment.setText(newCommentDto.getText());
        comment.setEvent(event);
        comment.setCommentator(commentator);
        return comment;
    }


}
