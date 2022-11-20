package ru.practicum.ewmservice.model.user.mapper;

import ru.practicum.ewmservice.model.user.User;
import ru.practicum.ewmservice.model.user.dto.NewUserRequest;
import ru.practicum.ewmservice.model.user.dto.UserDto;
import ru.practicum.ewmservice.model.user.dto.UserShortDto;

public class UserMapper {
    public static User toUser(NewUserRequest newUserRequest) {
        User user = User.builder()
                .email(newUserRequest.getEmail())
                .name(newUserRequest.getName())
                .build();
        return user;
    }

    public static UserDto toUserDto(User user) {
        return new UserDto(user);
    }

    public static UserShortDto toUserShortDto(User user) {
        return new UserShortDto(user);
    }
}
