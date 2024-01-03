package ru.practicum.shareit.item;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.exception.ObjectNotFoundException;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.item.service.ItemServiceImpl;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@SpringBootTest
@ExtendWith(MockitoExtension.class)
public class ItemRepositoryTest {
    @Mock
    private ItemRepository itemRepository;

    @Test
    void getById_shouldThrowExceptionIfWrongId() {
        ItemService itemService = new ItemServiceImpl(itemRepository, null,
                null, null);
        when(itemRepository.findById(any(Long.class))).thenReturn(Optional.empty());

        assertThrows(ObjectNotFoundException.class,
                () -> itemService.getItemById(1L, 100L));
    }
}
