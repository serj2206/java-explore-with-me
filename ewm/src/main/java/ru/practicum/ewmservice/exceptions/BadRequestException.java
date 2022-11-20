package ru.practicum.ewmservice.exceptions;

public class BadRequestException extends RuntimeException {
    public BadRequestException(String mesage) {
        super(mesage);
    }
}
