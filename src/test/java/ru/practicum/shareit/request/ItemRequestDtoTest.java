package ru.practicum.shareit.request;

import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
public class ItemRequestDtoTest {
    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
    @Autowired
    private JacksonTester<ItemRequestDto> json;

    private static final LocalDateTime DATE =
            LocalDateTime.of(2023, 12, 10, 12, 30, 0);
    private UserDto userDto = new UserDto(
            2L,
            "Ivan",
            "ivan@mail.ru"
    );
    private ItemRequestDto request = new ItemRequestDto(
            1L,
            "I'm looking for a table",
            UserMapper.toUser(userDto),
            DATE,
            null
    );

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
                .isEqualTo("2023-12-10T12:30:00");
        assertThat(result).extractingJsonPathArrayValue("$.items").isEqualTo(null);
    }
}
