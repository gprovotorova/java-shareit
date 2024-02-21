package ru.practicum.shareit.user.dto;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import ru.practicum.shareit.common.Create;
import ru.practicum.shareit.common.Update;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserDto {
    private Long id;

    @NotNull(groups = {Create.class}, message = "User name cannot be empty.")
    private String name;

    @Email(groups = {Create.class, Update.class}, message = "Invalid email address.")
    @NotNull(groups = {Create.class}, message = "Email address cannot be empty.")
    private String email;
}
