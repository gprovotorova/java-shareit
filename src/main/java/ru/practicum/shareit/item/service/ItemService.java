package ru.practicum.shareit.item.service;

import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoWithBooking;
import ru.practicum.shareit.comments.dto.CommentDto;

import java.util.List;

public interface ItemService {
    ItemDto createItem(ItemDto itemDto, long userId);

    ItemDto updateItem(ItemDto itemDto, long userId, long itemId);

    ItemDtoWithBooking getItemById(long userId, long itemId);

    //List<ItemDtoWithBooking> getItemsByUser(Long userId, Integer from, Integer size);
    List<ItemDtoWithBooking> getItemsByUser(Long userId, Pageable page);

    //List<ItemDto> searchItemByQuery(Long userId, String text, Integer from, Integer size);
    List<ItemDto> searchItemByQuery(Long userId, String text, Pageable page);

    CommentDto addComment(Long userId, Long itemId, CommentDto commentDto);
}
