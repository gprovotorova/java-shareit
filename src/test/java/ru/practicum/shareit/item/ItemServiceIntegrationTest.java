package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.enums.BookingStatus;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.common.PageMaker;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoWithBooking;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.item.service.ItemServiceImpl;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@Transactional
@Rollback
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@SpringBootTest
public class ItemServiceIntegrationTest {

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private ItemRequestRepository itemRequestRepository;

    @Autowired
    private ItemServiceImpl itemService;

    private static final LocalDateTime date =
            LocalDateTime.of(2023, 12, 10, 12, 30, 0);
    private static final int FROM = 0;
    private static final int SIZE = 10;

    @Transactional
    @Test
    void getItemsByUser_shouldReturnListOfItems() {
        User galina = new User(1L,
                "Galina",
                "galina@mail.ru");
        galina = userRepository.save(galina);
        User anna = new User(2L,
                "Anna",
                "anna@mail.ru");
        anna = userRepository.save(anna);
        User ivan = new User(
                3L,
                "Ivan",
                "ivan@mail.ru");
        ivan = userRepository.save(ivan);

        ItemRequest itemRequest = new ItemRequest();
        itemRequest.setDescription("Request description");
        itemRequest.setRequester(anna);
        itemRequest.setCreated(LocalDateTime.now());
        itemRequest = itemRequestRepository.save(itemRequest);

        ItemDto itemDto = new ItemDto();
        itemDto.setName("Book");
        itemDto.setDescription("Description");
        itemDto.setAvailable(true);
        itemDto.setRequestId(itemRequest.getId());

        Item item = ItemMapper.toItem(itemDto, galina);
        item.setRequestId(itemRequest.getId());
        item = itemRepository.save(item);

        Booking booking = new Booking();
        booking.setId(1L);
        booking.setStart(date);
        booking.setEnd(date.plusDays(2));
        booking.setItem(item);
        booking.setBooker(anna);
        booking.setStatus(BookingStatus.APPROVED);

        Booking nextBooking = new Booking();
        nextBooking.setId(2L);
        nextBooking.setStart(date.plusDays(3));
        nextBooking.setEnd(date.plusDays(5));
        nextBooking.setItem(item);
        nextBooking.setBooker(ivan);
        nextBooking.setStatus(BookingStatus.APPROVED);

        itemService.createItem(itemDto, galina.getId());
        booking = bookingRepository.save(booking);
        nextBooking = bookingRepository.save(nextBooking);

        Pageable page = PageMaker.makePageableWithSort(FROM, SIZE);

        List<ItemDtoWithBooking> list = itemService.getItemsByUser(galina.getId(), page);

        assertNotNull(list, "The list must not be null.");
        assertEquals(2, list.size(), "List size must be 2.");

        bookingRepository.delete(booking);
        bookingRepository.delete(nextBooking);
    }
}
