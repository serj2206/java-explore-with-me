package ru.practicum.ewmservice.feature.comments.controllers;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewmservice.feature.comments.model.dto.CommentShortDto;
import ru.practicum.ewmservice.feature.comments.service.PublicCommentService;

import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@RestController
@RequestMapping
@RequiredArgsConstructor
@Slf4j
@Validated
public class PublicCommentController {
    private final PublicCommentService publicCommentService;

    //Комментарии к событию
    @GetMapping("/events/{eventId}/comments")
    public List<CommentShortDto> findCommentsByEvent(@Positive @PathVariable Long eventId,
                                                     @PositiveOrZero @RequestParam(defaultValue = "0") int from,
                                                     @Positive @RequestParam(defaultValue = "10") int size) {
        log.info("PublicCommentsController: GET /events/{}/comments: getCommentsByEvent() eventId = {}", eventId, eventId);
        return publicCommentService.findCommentByEvent(eventId, from, size);
    }

    //Поиск комментариев:
    // по тексту,
    // по дате,
    // по комментатору,
    // по событию
    @GetMapping("/comments")
    public List<CommentShortDto> getComments(@RequestParam(required = false) String text,
                                             @RequestParam(required = false) Long eventId,
                                             @RequestParam(required = false) String rangeStart,
                                             @RequestParam(required = false) String rangeEnd,
                                             @RequestParam(required = false) Long commentatorId,
                                             @PositiveOrZero @RequestParam(defaultValue = "0") int from,
                                             @Positive @RequestParam(defaultValue = "10") int size) {
        log.info("PublicCommentsController: GET /comments: getComments() text ={}, eventId = {}, rangeStart = {}, rangeEnd ={}, commentatorId = {}, from = {}, size = {}",
                text, eventId, rangeStart, rangeEnd, commentatorId, from, size);
        return publicCommentService.getComments(text, eventId, rangeStart, rangeEnd, commentatorId, from, size);
    }
}
