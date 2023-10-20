package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoWithBooking;
import ru.practicum.shareit.comments.dto.CommentDto;

import java.util.List;

public interface ItemService {
    ItemDto createItem(ItemDto itemDto, long userId);

    ItemDto updateItem(ItemDto itemDto, long userId, long itemId);

    ItemDtoWithBooking getItemById(long userId, long itemId);

    List<ItemDtoWithBooking> getItemsByUser(Long userId);

    List<ItemDto> searchItemByQuery(String text);

    CommentDto addComment(Long userId, Long itemId, CommentDto commentDto);
}
