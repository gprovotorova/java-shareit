package ru.practicum.shareit.request;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.request.service.ItemRequestService;
import ru.practicum.shareit.request.service.ItemRequestServiceImpl;

import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@ExtendWith(MockitoExtension.class)
public class ItemRequestRepositoryTest {
    @Mock
    private ItemRequestRepository requestRepository;
    private ItemRequestService requestService;

    @Test
    void getById_shouldThrowExceptionIfWrongId() {
        requestService = new ItemRequestServiceImpl(requestRepository, null, null);

        assertThrows(NullPointerException.class,
                () -> requestService.getRequestById(1L, 100L));
    }
}
