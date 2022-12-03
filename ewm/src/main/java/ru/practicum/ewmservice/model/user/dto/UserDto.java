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
public class UserDto {
    long id;
    String name;
    String email;

    public UserDto(User user) {
        this.id = user.getId();
        this.name = user.getName();
        this.email = user.getEmail();
    }

}
