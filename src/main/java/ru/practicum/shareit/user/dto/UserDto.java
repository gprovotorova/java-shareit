package ru.practicum.shareit.user.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.common.Create;
import ru.practicum.shareit.common.Update;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserDto {
    private Long id;

<<<<<<< HEAD
    @NotNull(groups = {Create.class}, message = "User name cannot be empty.")
=======
    @NotNull(groups = {Create.class})
>>>>>>> 61d3a36fb68671b2bc56a32d663def57fc07f660
    private String name;

    @Email(groups = {Create.class, Update.class}, message = "Invalid email address.")
    @NotNull(groups = {Create.class}, message = "Email address cannot be empty.")
    private String email;
}
