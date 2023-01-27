package ru.practicum.ewmservice.feature.comments.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.ewmservice.exceptions.ViolationRuleException;
import ru.practicum.ewmservice.feature.comments.model.Comment;
import ru.practicum.ewmservice.feature.comments.model.CommentStatus;
import ru.practicum.ewmservice.feature.comments.model.dto.CommentShortDto;
import ru.practicum.ewmservice.feature.comments.model.dto.NewCommentDto;
import ru.practicum.ewmservice.feature.comments.model.dto.UpdateCommentDto;
import ru.practicum.ewmservice.feature.comments.model.mapper.CommentMapper;
import ru.practicum.ewmservice.feature.comments.repository.CommentRepository;
import ru.practicum.ewmservice.model.event.Event;
import ru.practicum.ewmservice.model.event.State;
import ru.practicum.ewmservice.model.user.User;
import ru.practicum.ewmservice.repository.EventRepository;
import ru.practicum.ewmservice.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.NoSuchElementException;


@Service
@RequiredArgsConstructor
@Slf4j
public class PrivateCommentService {
    private final CommentRepository commentRepository;
    private final UserRepository userRepository;
    private final EventRepository eventRepository;


    //Добавление комментария
    public CommentShortDto addComment(Long userId, Long eventId, NewCommentDto newCommentDto) {
        User commentator = userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException(String.format("Пользователь c id = %d не найден", userId)));

        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NoSuchElementException("Событие не найдено"));

        if (!event.getState().equals(State.PUBLISHED)) {
            throw new ViolationRuleException("Нельзя комментировать неопубликованное событие");
        }

        Comment comment = CommentMapper.toComment(newCommentDto, event, commentator);

        comment.setCreatedOn(LocalDateTime.now());
        comment.setCommentStatus(CommentStatus.PUBLISHED);
        comment.setCommentChanged(false);

        return CommentMapper.toCommentShortDto(commentRepository.save(comment));
    }


    //Обновление комментария
    public CommentShortDto updateComment(Long userId, Long commentId, UpdateCommentDto updateCommentDto) {

        Comment comment = commentRepository.findCommentByIdAndCommentatorId(commentId, userId);

        if (comment == null) {
            throw new ViolationRuleException(String.format("Пользователь с id = %d не может редактировать или удалить комментарий с id = %d", userId, commentId));
        }

        if (updateCommentDto.getText() != null) {
            comment.setText(updateCommentDto.getText());
        }

        if (comment.getCommentStatus().equals(CommentStatus.BLOCKED)) {
            throw new ViolationRuleException(String.format("Пользователь с id = %d не может редактировать или удалить комментарий с id = %d", userId, commentId));
        }

        comment.setCommentChanged(true);

        return CommentMapper.toCommentShortDto(commentRepository.save(comment));
    }

    //Удаление комментария
    public void deleteComment(Long userId, Long commentId) {
        Comment comment = commentRepository.findCommentByIdAndCommentatorId(commentId, userId);

        if (comment == null) {
            throw new NoSuchElementException(String.format("Пользователь с id = %d не может редактировать или удалить комментарий с id = %d", userId, commentId));
        }
        commentRepository.deleteById(commentId);
    }
}
