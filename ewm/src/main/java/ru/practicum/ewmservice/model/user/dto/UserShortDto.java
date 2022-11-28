package ru.practicum.ewmservice.model.user.dto;

import lombok.*;
import ru.practicum.ewmservice.model.user.User;

@EqualsAndHashCode
@ToString
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserShortDto {
    long id;
    String name;

    public UserShortDto(User user) {
        this.id = user.getId();
        this.name = user.getName();
    }
}
