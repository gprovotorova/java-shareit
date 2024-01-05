package ru.practicum.shareit.booking;

import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.enums.BookingStatus;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
public class BookingDtoTest {
    @Autowired
    private JacksonTester<BookingDto> json;
    private final BookingDto.Item item = new BookingDto.Item(
            1L,
            "Book");
    private final BookingDto.User user = new BookingDto.User(
            1L,
            "Anna");
    private final BookingDto booking = new BookingDto(
            1L,
            LocalDateTime.of(2023, 12, 10, 10, 50, 0),
            LocalDateTime.of(2023, 12, 29, 12, 10, 0),
            item,
            1L,
            user,
            BookingStatus.WAITING);

    @SneakyThrows
    @Test
    void testBookingDto() {
        JsonContent<BookingDto> result = json.write(booking);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.start")
                .isEqualTo("2023-12-10T10:50:00");
        assertThat(result).extractingJsonPathStringValue("$.end")
                .isEqualTo("2023-12-29T12:10:00");
        assertThat(result).extractingJsonPathStringValue("$.status").isEqualTo("WAITING");
        assertThat(result).extractingJsonPathNumberValue("$.user.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.booker.name").isEqualTo("Anna");
        assertThat(result).extractingJsonPathNumberValue("$.item.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.item.name").isEqualTo("Book");
    }
}
