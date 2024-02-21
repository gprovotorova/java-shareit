package ru.practicum.shareit.comment;

import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.comments.CommentDto;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
public class CommentDtoTest {
    @Autowired
    private JacksonTester<CommentDto> json;
    private final CommentDto comment = new CommentDto(
            1L,
            "comment to item",
            "Galina",
            LocalDateTime.of(2023, 12, 25, 13, 30, 0));

    @SneakyThrows
    @Test
    void testCommentDto() {
        JsonContent<CommentDto> result = json.write(comment);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.text")
                .isEqualTo("comment to item");
        assertThat(result).extractingJsonPathStringValue("$.authorName").isEqualTo("Galina");
        assertThat(result).extractingJsonPathStringValue("$.created")
                .isEqualTo("2023-12-25T13:30:00");
    }
}