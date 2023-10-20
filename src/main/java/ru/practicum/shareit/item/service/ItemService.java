package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.ItemDto;
<<<<<<< HEAD
import ru.practicum.shareit.item.dto.ItemDtoWithBooking;
import ru.practicum.shareit.comments.dto.CommentDto;
=======
import ru.practicum.shareit.item.model.Item;
>>>>>>> 61d3a36fb68671b2bc56a32d663def57fc07f660

import java.util.List;

public interface ItemService {
    ItemDto createItem(ItemDto itemDto, long userId);

    ItemDto updateItem(ItemDto itemDto, long userId, long itemId);

<<<<<<< HEAD
    ItemDtoWithBooking getItemById(long userId, long itemId);

    List<ItemDtoWithBooking> getItemsByUser(Long userId);

    List<ItemDto> searchItemByQuery(String text);

    CommentDto addComment(Long userId, Long itemId, CommentDto commentDto);
=======
    ItemDto getItem(long itemId);

    List<ItemDto> getItemsByUser(Long userId);

    List<Item> searchItem(String text);
>>>>>>> 61d3a36fb68671b2bc56a32d663def57fc07f660
}
