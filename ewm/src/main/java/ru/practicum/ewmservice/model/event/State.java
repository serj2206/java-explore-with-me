package ru.practicum.ewmservice.model.event;

import java.util.Optional;

public enum State {
    PENDING,   //Ожидающий
    PUBLISHED, //Опубликованный
    CANCELED;   //Завершенный

    public static Optional<State> from(String stringState) {
        for (State state : values()) {
            if (state.name().equalsIgnoreCase(stringState)) {
                return Optional.of(state);
            }
        }
        return Optional.empty();
    }
}
