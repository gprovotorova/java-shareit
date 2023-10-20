package ru.practicum.shareit.user.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.common.Create;
import ru.practicum.shareit.common.Update;

<<<<<<< HEAD
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Id;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Column;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @NotNull(groups = {Create.class}, message = "User name cannot be empty.")
    @Column(name = "name", nullable = false)
=======
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
>>>>>>> 61d3a36fb68671b2bc56a32d663def57fc07f660
    private String name;

    @Email(groups = {Create.class, Update.class}, message = "Invalid email address.")
    @NotNull(groups = {Create.class}, message = "Email address cannot be empty.")
<<<<<<< HEAD
    @Column(name = "email",unique = true, nullable = false)
=======
>>>>>>> 61d3a36fb68671b2bc56a32d663def57fc07f660
    private String email;
}
