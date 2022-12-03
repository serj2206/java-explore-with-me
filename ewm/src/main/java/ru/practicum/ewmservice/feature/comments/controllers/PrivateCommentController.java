package ru.practicum.ewmservice.feature.comments.controllers;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewmservice.common.marker.Create;
import ru.practicum.ewmservice.common.marker.Update;
import ru.practicum.ewmservice.feature.comments.model.dto.CommentShortDto;
import ru.practicum.ewmservice.feature.comments.model.dto.NewCommentDto;
import ru.practicum.ewmservice.feature.comments.model.dto.UpdateCommentDto;
import ru.practicum.ewmservice.feature.comments.service.PrivateCommentService;

import javax.validation.constraints.Positive;

@RequiredArgsConstructor
@RestController
@RequestMapping(path = "/users")
@Slf4j
@Validated
public class PrivateCommentController {
    private final PrivateCommentService privateCommentService;

    //Добавление комментария
    @PostMapping("/{userId}/events/{eventId}/comments")
    public CommentShortDto addComment(@Positive @PathVariable Long userId,
                                      @Positive @PathVariable Long eventId,
                                      @Validated({Create.class}) @RequestBody NewCommentDto newCommentDto) {
        log.info("PrivateCommentController: POST /users/{}/events/{}/comments: addComment() newCommentDto = {}, userId = {}, eventId = {}",
                userId, eventId, newCommentDto, userId, eventId);
        return privateCommentService.addComment(userId, eventId, newCommentDto);
    }

    //Редактирование комментария
    @PatchMapping("/{userId}/comments/{commId}")
    public CommentShortDto updateComment(@Positive @PathVariable Long userId,
                                         @Positive @PathVariable Long commId,
                                         @Validated({Update.class}) @RequestBody UpdateCommentDto updateComment) {
        log.info("PrivateCommentController: PATCH /users/{}/comments/{}: updateComment() updateComment = {} , userId = {}, commId ={}",
                userId, commId, updateComment, userId, commId);
        return privateCommentService.updateComment(userId, commId, updateComment);
    }

    //Удаление комментария
    @DeleteMapping("/{userId}/comments/{commId}")
    public void deleteComment(@Positive @PathVariable Long userId,
                              @Positive @PathVariable Long commId) {
        log.info("PrivateCommentController: DELETE /users/{}/comments/{}: deleteComment() userId = {},  commId ={}",
                userId, commId, userId, commId);
        privateCommentService.deleteComment(userId, commId);
    }
}
