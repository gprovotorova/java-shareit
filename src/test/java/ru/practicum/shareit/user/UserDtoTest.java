package ru.practicum.shareit.user;

import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.user.dto.UserDto;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
public class UserDtoTest {
    @Autowired
    private JacksonTester<UserDto> json;
    private final UserDto galina = new UserDto(
            1L,
            "Galina",
            "galina@mail.ru");

    @SneakyThrows
    @Test
    void testJsonUserDto() {
        JsonContent<UserDto> result = json.write(galina);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.name").isEqualTo("Galina");
        assertThat(result).extractingJsonPathStringValue("$.email").isEqualTo("galina@mail.ru");
    }
}
