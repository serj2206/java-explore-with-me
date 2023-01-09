package ru.practicum.ewmservice.feature.comments.model;

import java.util.Optional;

public enum CommentStatus {
    PUBLISHED, //опубикован
    BLOCKED;    //заблокирован

    public static Optional<CommentStatus> from(String stringStatus) {
        for (CommentStatus status : values()) {
            if (status.name().equalsIgnoreCase(stringStatus)) {
                return Optional.of(status);
            }
        }
        return Optional.empty();
    }
}
