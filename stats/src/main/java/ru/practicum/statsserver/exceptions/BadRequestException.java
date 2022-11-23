package ru.practicum.statsserver.exceptions;

public class BadRequestException extends RuntimeException {
    public BadRequestException(String mesage) {
        super(mesage);
    }
}
