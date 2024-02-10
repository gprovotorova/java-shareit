package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.ObjectNotFoundException;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.request.service.ItemRequestServiceImpl;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@Transactional
@Rollback
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@SpringBootTest
public class ItemRequestIntegrationTest {
    @Autowired
    private ItemRequestRepository itemRequestRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ItemRequestServiceImpl itemRequestService;

    private static final LocalDateTime DATE =
            LocalDateTime.of(2023, 12, 10, 12, 30, 0);

    @Transactional
    @Test
    void getRequestById_shouldThrowException() {
        Long userId = 1L;
        Long requestId = 1L;
        assertThrows(ObjectNotFoundException.class, () -> itemRequestService.getRequestById(userId, requestId));
    }

    @Transactional
    @Test
    void getRequestById_shouldReturnRequest() {
        User galina = new User(
                1L,
                "Galina",
                "galina@mail.ru");
        User anna = new User(
                2L,
                "Anna",
                "anna@mail.ru");
        galina = userRepository.save(galina);
        anna = userRepository.save(anna);

        ItemRequest itemRequest = new ItemRequest();
        itemRequest.setDescription("Request description");
        itemRequest.setRequester(anna);
        itemRequest.setCreated(DATE);
        itemRequest = itemRequestRepository.save(itemRequest);

        ItemRequestDto resultItemRequest = itemRequestService.getRequestById(anna.getId(), itemRequest.getId());

        assertNotNull(resultItemRequest, "The request must not be null.");
        assertEquals("Request description",
                resultItemRequest.getDescription(), "The request description must match.");
        assertNotNull(resultItemRequest.getCreated(), "Creation date must not be null.");
    }
}
