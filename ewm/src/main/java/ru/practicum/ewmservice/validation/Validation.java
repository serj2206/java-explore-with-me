package ru.practicum.ewmservice.validation;

import ru.practicum.ewmservice.exceptions.ValidationException;

import java.time.LocalDateTime;

public class Validation {

    public static void eventDateValidation(LocalDateTime evenDate) {
        if (evenDate.isBefore(LocalDateTime.now().plusHours(2)))
            throw new ValidationException("Дата и время, на которые намечено событие не может быть раньше, чем через два часа от текущего момента");
    }
}
