package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.ObjectNotFoundException;
import ru.practicum.shareit.exception.ObjectValidationException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoWithBooking;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class ItemServiceTest {
    private final ItemService itemService;
    private final UserService userService;

    private final UserDto galina = new UserDto(
            1L,
            "Galina",
            "galina@mail.ru");
    private final UserDto anna = new UserDto(
            2L, "Anna", "anna@mail.ru");
    private final ItemDto item = new ItemDto(
            2L,
            "Notebook",
            "Notebook description",
            true,
            null,
            null);

    @Test
    void createItem_shouldThrowExceptionIfUserIdIsIncorrect() {
        assertThrows(ObjectNotFoundException.class,
                () -> itemService.createItem(item, 100L));
    }

    @Test
    void updateItem_shouldThrowExceptionIfItemIdIsIncorrect() {
        assertThrows(ObjectNotFoundException.class,
                () -> itemService.updateItem(item, galina.getId(), 100L));
    }

    @Test
    void updateItem_shouldThrowExceptionIfUserIdIsIncorrect() {
        assertThrows(ObjectNotFoundException.class,
                () -> itemService.updateItem(item, 100L, item.getId()));
    }

    @Test
    void updateItem_shouldThrowExceptionIfNotUserUpdating() {
        UserDto galinaDto = userService.createUser(galina);
        UserDto annaDto = userService.createUser(anna);
        ItemDto savedItem = itemService.createItem(item, galinaDto.getId());

        assertThrows(ObjectNotFoundException.class,
                () -> itemService.updateItem(new ItemDto(savedItem.getId(),
                        savedItem.getName(), savedItem.getDescription(),
                        savedItem.getAvailable(), savedItem.getOwner(), savedItem.getRequestId()),
                        annaDto.getId(), savedItem.getId()));
    }

    @Test
    void updateItem_shouldUpdateIfItemNameIsNull() {
        UserDto galinaDto = userService.createUser(galina);
        ItemDto savedItem = itemService.createItem(item, galinaDto.getId());
        savedItem.setName(null);
        ItemDto updatedItem = itemService.updateItem(new ItemDto(savedItem.getId(),
                savedItem.getName(), savedItem.getDescription(),
                savedItem.getAvailable(), savedItem.getOwner(), savedItem.getRequestId()),
                galinaDto.getId(), savedItem.getId());

        assertEquals(savedItem.getDescription(), updatedItem.getDescription());
        assertEquals(savedItem.getAvailable(), updatedItem.getAvailable());
    }

    @Test
    void updateItem_shouldUpdateIfItemDescriptionIsNull() {
        UserDto galinaDto = userService.createUser(galina);
        ItemDto savedItem = itemService.createItem(item, galinaDto.getId());
        savedItem.setDescription(null);
        ItemDto updatedItem = itemService.updateItem(new ItemDto(savedItem.getId(),
                        savedItem.getName(), savedItem.getDescription(),
                        savedItem.getAvailable(), savedItem.getOwner(), savedItem.getRequestId()),
                        galinaDto.getId(), savedItem.getId());

        assertEquals(savedItem.getName(), updatedItem.getName());
        assertEquals(savedItem.getAvailable(), updatedItem.getAvailable());
    }

    @Test
    void updateItem_shouldUpdateIfItemAvailableIsNull() {
        UserDto galinaDto = userService.createUser(galina);
        ItemDto savedItem = itemService.createItem(item, galinaDto.getId());
        savedItem.setAvailable(null);
        ItemDto updatedItem = itemService.updateItem(new ItemDto(savedItem.getId(),
                        savedItem.getName(), savedItem.getDescription(),
                        savedItem.getAvailable(), savedItem.getOwner(), savedItem.getRequestId()),
                        galinaDto.getId(), savedItem.getId());

        assertEquals(savedItem.getName(), updatedItem.getName());
        assertEquals(savedItem.getDescription(), updatedItem.getDescription());
    }

    @Test
    void getItemById_shouldThrowExceptionIfIdIsIncorrect() {
        assertThrows(ObjectNotFoundException.class,
                () -> itemService.getItemById(100L, 100L));
    }

    @Test
    void getItemsByUser_shouldReturnByUserIdIfLimitIsNull() {
        UserDto galinaDto = userService.createUser(galina);
        itemService.createItem(item, galinaDto.getId());
        List<ItemDtoWithBooking> items = itemService.getItemsByUser(galinaDto.getId(), 0, null);

        assertFalse(items.isEmpty());
    }

    @Test
    void getItemsByUser_shouldReturnByUserId() {
        UserDto galinaDto = userService.createUser(galina);
        itemService.createItem(item, galinaDto.getId());
        List<ItemDtoWithBooking> items = itemService.getItemsByUser(galinaDto.getId(), 0, 10);

        assertFalse(items.isEmpty());
    }

    @Test
    void searchItemByQuery_shouldReturnItemIfLimitIsNull() {
        UserDto galinaDto = userService.createUser(galina);
        itemService.createItem(item, galinaDto.getId());
        List<ItemDto> items = itemService.searchItemByQuery(galinaDto.getId(), "notebook", 0,null);

        assertFalse(items.isEmpty());
    }

    @Test
    void searchItemByQuery_shouldReturnItem() {
        UserDto galinaDto = userService.createUser(galina);
        itemService.createItem(item, galinaDto.getId());
        List<ItemDto> items = itemService.searchItemByQuery(galinaDto.getId(), "notebook", 0,10);

        assertFalse(items.isEmpty());
    }

    @Test
    void searchItemByQuery_shouldThrowExceptionIfFromLessThanZero() {
        UserDto galinaDto = userService.createUser(galina);
        itemService.createItem(item, galinaDto.getId());

        assertThrows(IllegalArgumentException.class,
                () -> itemService.searchItemByQuery(galinaDto.getId(), "notebook", -1,null));
    }

    @Test
    void searchItemByQuery_shouldThrowExceptionIfSizeIsZero() {
        UserDto galinaDto = userService.createUser(galina);
        itemService.createItem(item, galinaDto.getId());

        assertThrows(ObjectValidationException.class,
                () -> itemService.searchItemByQuery(galinaDto.getId(), "notebook", 0,0));
    }
}
