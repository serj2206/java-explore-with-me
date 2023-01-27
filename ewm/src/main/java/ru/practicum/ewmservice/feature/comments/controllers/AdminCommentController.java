package ru.practicum.ewmservice.feature.comments.controllers;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewmservice.feature.comments.model.dto.CommentFullDto;
import ru.practicum.ewmservice.feature.comments.model.dto.UpdateCommentDto;
import ru.practicum.ewmservice.feature.comments.service.AdminCommentService;

import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping(path = "/admin")
@Slf4j
@Validated
public class AdminCommentController {
    private final AdminCommentService adminCommentService;

    //Получить все комментарии по событию с подробной информацией
    @GetMapping("/events/{eventId}/comments")
    public List<CommentFullDto> findCommentByEvent(@Positive @PathVariable Long eventId,
                                                   @PositiveOrZero @RequestParam(defaultValue = "0") int from,
                                                   @Positive @RequestParam(defaultValue = "10") int size) {
        log.info("AdminCommentController: GET admin/events/{}/comments: getCommentsByEvent() id = {}", eventId, eventId);
        return adminCommentService.findCommentByEvent(eventId, from, size);
    }

    //Поиск комментариев:
    // по тексту,
    // по дате,
    // по комментатору,
    // по событию
    // по статусу
    @GetMapping("/comments")
    public List<CommentFullDto> getComments(@RequestParam(required = false) String text,
                                            @RequestParam(required = false) Long eventId,
                                            @RequestParam(required = false) String rangeStart,
                                            @RequestParam(required = false) String rangeEnd,
                                            @RequestParam(required = false) Long commentatorId,
                                            @RequestParam(required = false) String status,
                                            @PositiveOrZero @RequestParam(defaultValue = "0") int from,
                                            @Positive @RequestParam(defaultValue = "10") int size) {
        log.info("AdminCommentController: GET /comments: getComments() text ={}, " +
                        "eventId = {}, rangeStart = {}, rangeEnd ={}, commentatorId = {}, status ={}, from = {}, size = {}",
                text, eventId, rangeStart, rangeEnd, commentatorId, status, from, size);
        return adminCommentService.getComments(text, eventId, rangeStart, rangeEnd, commentatorId, status, from, size);
    }

    //Загрузка комментария по ID
    @GetMapping("/comments/{commId}")
    public CommentFullDto getCommentById(@Positive @PathVariable Long commId) {
        log.info("AdminCommentController: GET /comments/{}: getCommentById() commId ={}",
                commId, commId);
        return adminCommentService.getCommentById(commId);
    }

    //Редактирование комментария
    @PatchMapping("/comments/{commId}")
    public CommentFullDto updateComment(@Positive @PathVariable Long commId,
                                        @RequestBody UpdateCommentDto updateCommentDto) {
        log.info("AdminCommentController: PATCH /comments/{}: updateComment() commId ={} adminUpdateCommentDto = {}",
                commId, commId, updateCommentDto);
        return adminCommentService.updateComment(commId,updateCommentDto);
    }

    //Удаление комментария
    @DeleteMapping("/comments/{commId}")
    public void deleteComment(@Positive @PathVariable Long commId) {
        log.info("PrivateCommentController: DELETE /admin/comments/{commId}: deleteComment() commId ={}", commId);
        adminCommentService.deleteComment(commId);
    }

    //Блокировка комментария
    @PatchMapping("/comments/{commId}/blocked")
    public CommentFullDto blockedComment(@Positive @PathVariable Long commId) {
        log.info("PrivateCommentController: PATCH /admin/comments/{}/blocked: blockedComment() commId ={}",commId, commId);
        return adminCommentService.blockedComment(commId);
    }

    //Публикация комментария (разблокировка)
    @PatchMapping("/comments/{commId}/published")
    public CommentFullDto publishedComment(@Positive @PathVariable Long commId) {
        log.info("PrivateCommentController: PATCH /admin/comments/{}/published: publishedComment() commId ={}",commId, commId);
        return adminCommentService.publishedComment(commId);
    }

}
