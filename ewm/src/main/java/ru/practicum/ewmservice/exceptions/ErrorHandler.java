package ru.practicum.ewmservice.exceptions;

import lombok.extern.slf4j.Slf4j;
import org.hibernate.exception.ConstraintViolationException;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Slf4j
@RestControllerAdvice
public class ErrorHandler {
    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleBadRequestException(final BadRequestException e) {
        log.warn("400 {}", e.getMessage());
        List<String> errors = null;
        return new ErrorResponse(
                errors,
                e.getMessage(),
                "Неверный запрос",
                HttpStatus.BAD_REQUEST.toString(),
                LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleBadRequestException(final MethodArgumentNotValidException e) {
        log.warn("400 {}", e.getMessage());
        List<String> errors = e.getAllErrors().stream().map(o -> o.getDefaultMessage()).collect(Collectors.toList());
        return new ErrorResponse(
                errors,
                e.getMessage(),
                "Неверный запрос",
                HttpStatus.BAD_REQUEST.toString(),
                LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorResponse handleConflictException(final ConstraintViolationException e) {
        log.warn("409 {}", e.getSQLException().getMessage());
        List<String> errors = null;
        return new ErrorResponse(
                errors,
                e.getSQLException().getMessage(),
                "Запрос приводит к нарушению целостности данных",
                HttpStatus.CONFLICT.toString(),
                LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleNoSuchElementException(final NoSuchElementException e) {
        log.warn("404 {}", e.getMessage());
        List<String> errors = null;
        return new ErrorResponse(
                errors,
                e.getMessage(),
                "Значение не найдено",
                HttpStatus.NOT_FOUND.toString(),
                LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleMissingServletRequestParameterException(final MissingServletRequestParameterException e) {
        log.warn("400 {}", e.getMessage());
        List<String> errors = null;
        return new ErrorResponse(
                errors,
                e.getMessage(),
                "Неверный запрос",
                HttpStatus.BAD_REQUEST.toString(),
                LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleDateTimeParseException(final DateTimeParseException e) {
        log.warn("400 {}", e.getMessage());
        List<String> errors = null;
        return new ErrorResponse(
                errors,
                e.getMessage(),
                "Неверный формат даты и времени",
                HttpStatus.BAD_REQUEST.toString(),
                LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ErrorResponse handleViolationRuleException(final ViolationRuleException e) {
        log.warn("403 {}", e.getMessage());
        List<String> errors = null;
        return new ErrorResponse(
                errors,
                e.getMessage(),
                "Неверный формат даты и времени",
                HttpStatus.FORBIDDEN.toString(),
                LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
    }

}
