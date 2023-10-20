package ru.practicum.shareit.exception.error;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
<<<<<<< HEAD
@NoArgsConstructor
public class ErrorResponse {
    private String message;
    public ErrorResponse(String message) {
        this.message = message;
    }
=======
@AllArgsConstructor
@NoArgsConstructor
public class ErrorResponse {
    private String message;
>>>>>>> 61d3a36fb68671b2bc56a32d663def57fc07f660
}
