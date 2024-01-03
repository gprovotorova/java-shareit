package ru.practicum.shareit.request;

import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.mapper.ItemRequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
public class RequestDtoTest {
    @Autowired
    private JacksonTester<ItemRequestDto> json;

    private final UserDto userDto = new UserDto(
            2L,
            "Ivan",
            "ivan@mail.ru");
    private final ItemRequestDto request = new ItemRequestDto(
            1L,
            "I'm looking for a table",
            UserMapper.toUser(userDto),
            LocalDateTime.of(2023, 12, 29, 12, 0, 0),
            null);

    @SneakyThrows
    @Test
    void testRequestDto() {
        JsonContent<ItemRequestDto> result = json.write(request);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.description")
                .isEqualTo("I'm looking for a table");
        assertThat(result).extractingJsonPathNumberValue("$.requester.id").isEqualTo(2);
        assertThat(result).extractingJsonPathStringValue("$.requester.name").isEqualTo("Ivan");
        assertThat(result).extractingJsonPathStringValue("$.requester.email")
                .isEqualTo("ivan@mail.ru");
        assertThat(result).extractingJsonPathStringValue("$.created")
                .isEqualTo("2023-12-29T12:00:00");
        assertThat(result).extractingJsonPathArrayValue("$.items").isEqualTo(null);
    }
}
