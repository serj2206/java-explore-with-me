package ru.practicum.ewmservice.exceptions;

public class ViolationRuleException extends RuntimeException {
    public ViolationRuleException(String message) {
        super(message);
    }
}
