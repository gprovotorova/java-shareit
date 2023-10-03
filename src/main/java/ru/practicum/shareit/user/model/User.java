package ru.practicum.shareit.user.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.common.Create;
import ru.practicum.shareit.common.Update;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;


/**
 * TODO Sprint add-controllers.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {

    private Long userId;

    @NotNull(groups = {Create.class})
    private String name;

    @Email(groups = {Create.class, Update.class}, message = "Invalid email address.")
    @NotNull(groups = {Create.class}, message = "Email address cannot be empty.")
    private String email;
}
