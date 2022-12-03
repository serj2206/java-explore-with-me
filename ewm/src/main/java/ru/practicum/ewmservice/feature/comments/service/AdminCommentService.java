package ru.practicum.ewmservice.feature.comments.service;

import com.querydsl.core.types.dsl.BooleanExpression;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.ewmservice.common.FromSizeRequest;
import ru.practicum.ewmservice.exceptions.BadRequestException;
import ru.practicum.ewmservice.exceptions.ClassErrorException;
import ru.practicum.ewmservice.exceptions.ViolationRuleException;
import ru.practicum.ewmservice.feature.comments.model.Comment;
import ru.practicum.ewmservice.feature.comments.model.CommentStatus;
import ru.practicum.ewmservice.feature.comments.model.QComment;
import ru.practicum.ewmservice.feature.comments.model.dto.CommentFullDto;
import ru.practicum.ewmservice.feature.comments.model.dto.UpdateCommentDto;
import ru.practicum.ewmservice.feature.comments.model.mapper.CommentMapper;
import ru.practicum.ewmservice.feature.comments.repository.CommentRepository;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdminCommentService {
    private final CommentRepository commentRepository;


    //Поиск комментариев
    public List<CommentFullDto> getComments(String text,
                                            Long eventId,
                                            String rangeStart,
                                            String rangeEnd,
                                            Long commentatorId,
                                            String status,
                                            Integer from,
                                            Integer size) {

        Sort sortById = Sort.by(Sort.Direction.ASC, "id");
        Pageable pageable = FromSizeRequest.of(from, size, sortById);


        List<BooleanExpression> conditions = new ArrayList<>();
        LocalDateTime start;
        LocalDateTime end;

        if (text != null && !text.isBlank() && !text.isEmpty()) {
            conditions.add(QComment.comment.text.contains(text));
        }

        if (eventId != null) {
            conditions.add(QComment.comment.event.id.eq(eventId));
        }

        if (rangeStart != null && rangeEnd == null) {
            start = LocalDateTime.parse(rangeStart, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            conditions.add(QComment.comment.createdOn.after(start));
        }

        if (rangeStart == null && rangeEnd != null) {
            end = LocalDateTime.parse(rangeEnd, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            conditions.add(QComment.comment.createdOn.before(end));
        }

        if (rangeStart != null && rangeEnd != null) {
            start = LocalDateTime.parse(rangeStart, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            end = LocalDateTime.parse(rangeEnd, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            conditions.add(QComment.comment.createdOn.between(start, end));
        }

        if (rangeStart == null && rangeEnd == null) {
            start = LocalDateTime.now();
            conditions.add(QComment.comment.createdOn.before(start));
        }

        if (commentatorId != null) {
            conditions.add(QComment.comment.commentator.id.eq(commentatorId));
        }

        if (status != null && !status.isBlank() && !status.isEmpty()) {
            CommentStatus commentStatus = CommentStatus
                    .from(status).orElseThrow(() -> new BadRequestException("Unknown state: " + status));
            conditions.add(QComment.comment.commentStatus.eq(commentStatus));
        }

        BooleanExpression finalCondition = conditions.stream()
                .reduce(BooleanExpression::and)
                .orElseThrow(() -> new ClassErrorException("Не корректная работа метода сервиса управления"));

        return commentRepository.findAll(finalCondition, pageable)
                .stream()
                .map(CommentMapper::toCommentFullDto)
                .collect(Collectors.toList());
    }

    public List<CommentFullDto> findCommentByEvent(Long eventId, Integer from, Integer size) {

        Sort sortById = Sort.by(Sort.Direction.ASC, "id");
        Pageable pageable = FromSizeRequest.of(from, size, sortById);

        Page<Comment> comments = commentRepository.findCommentByEvent_Id(eventId, pageable);
        if (comments == null || comments.getSize() == 0) {
            throw new NoSuchElementException("Комментарии не найдены");
        }
        return (comments.stream()
                .map(CommentMapper::toCommentFullDto)
                .collect(Collectors.toList()));
    }

    public CommentFullDto getCommentById(Long commId) {
        Comment comment = commentRepository
                .findById(commId)
                .orElseThrow(() -> new NoSuchElementException(String.format("Комментарий с id = %d не найден", commId)));
        return CommentMapper.toCommentFullDto(comment);
    }

    public CommentFullDto updateComment(Long commId, UpdateCommentDto updateCommentDto) {
        Comment comment = commentRepository
                .findById(commId)
                .orElseThrow(() -> new NoSuchElementException(String.format("Комментарий с id = %d не найден", commId)));
        if (updateCommentDto.getText() != null) {
            comment.setText(updateCommentDto.getText());
            comment.setCommentChanged(true);
        }
        return CommentMapper.toCommentFullDto(commentRepository.save(comment));
    }

    public void deleteComment(Long commId) {
        commentRepository.deleteById(commId);
    }

    public CommentFullDto blockedComment(Long commId) {
        Comment comment = commentRepository.findById(commId)
                .orElseThrow(() -> new NoSuchElementException(String.format("Комментарий с id = %d не найден", commId)));
        if (comment.getCommentStatus().equals(CommentStatus.BLOCKED)) {
            throw new ViolationRuleException("Комментарий уже заблокирован");
        }
        comment.setCommentStatus(CommentStatus.BLOCKED);
        return CommentMapper.toCommentFullDto(commentRepository.save(comment));
    }

    public CommentFullDto publishedComment(Long commId) {
        Comment comment = commentRepository.findById(commId)
                .orElseThrow(() -> new NoSuchElementException(String.format("Комментарий с id = %d не найден", commId)));
        if (comment.getCommentStatus().equals(CommentStatus.PUBLISHED)) {
            throw new ViolationRuleException("Комментарий уже имеет право публиковаться");
        }
        comment.setCommentStatus(CommentStatus.PUBLISHED);
        return CommentMapper.toCommentFullDto(commentRepository.save(comment));
    }

}
