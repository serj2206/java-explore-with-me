package ru.practicum.ewmservice.model.user.dto;

import lombok.*;
import ru.practicum.ewmservice.common.marker.Create;
import ru.practicum.ewmservice.common.marker.Update;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

@EqualsAndHashCode
@ToString
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class NewUserRequest {

    @NotNull(groups = {Create.class})
    @NotBlank(groups = {Create.class})
    private String name;

    @Email(groups = {Create.class})
    @NotNull(groups = {Create.class})
    @NotBlank(groups = {Create.class})
    private String email;
}
