package ru.practicum.shareit.item.controller;

<<<<<<< HEAD
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.GetMapping;
import ru.practicum.shareit.common.Create;
import ru.practicum.shareit.item.dto.ItemDtoWithBooking;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemServiceImpl;
import ru.practicum.shareit.comments.dto.CommentDto;

import java.util.List;

@RestController
@RequestMapping("/items")
@Slf4j
@AllArgsConstructor
public class ItemController {

=======
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.common.Create;
import ru.practicum.shareit.common.Update;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemServiceImpl;

import java.util.List;
import java.util.stream.Collectors;

/**
 * TODO Sprint add-controllers.
 */
@RestController
@RequestMapping("/items")
@Slf4j
@RequiredArgsConstructor
public class ItemController {

    @Autowired
>>>>>>> 61d3a36fb68671b2bc56a32d663def57fc07f660
    private final ItemServiceImpl itemService;

    @PostMapping
    public ItemDto create(@Validated({Create.class}) @RequestHeader("X-Sharer-User-Id") long userId,
                          @RequestBody ItemDto itemDto) {
        log.info("Creating item {}", itemDto);
        return itemService.createItem(itemDto, userId);
    }

    @PatchMapping("/{itemId}")
<<<<<<< HEAD
    public ItemDto update(@PathVariable long itemId,
                          @RequestHeader("X-Sharer-User-Id") long userId,
                          @RequestBody ItemDto itemDto) {
=======
    public ItemDto update(@Validated({Update.class}) @PathVariable long itemId,
                          @RequestHeader("X-Sharer-User-Id") long userId,
                          @RequestBody ItemDto itemDto) {
        itemDto.setId(itemId);
>>>>>>> 61d3a36fb68671b2bc56a32d663def57fc07f660
        log.info("Updating item {}", itemDto);
        return itemService.updateItem(itemDto, userId, itemId);
    }

<<<<<<< HEAD
    @GetMapping(value = "/{itemId}")
    public ItemDtoWithBooking get(@RequestHeader("X-Sharer-User-Id") long userId, @PathVariable long itemId) {
        log.info("Get item id={}", itemId);
        return itemService.getItemById(userId, itemId);
    }

    @GetMapping
    public List<ItemDtoWithBooking> getItemsByUser(@RequestHeader("X-Sharer-User-Id") long userId) {
=======
    @GetMapping("/{itemId}")
    public ItemDto get(@PathVariable long itemId) {
        log.info("Get item id={}", itemId);
        return itemService.getItem(itemId);
    }

    @GetMapping
    public List<ItemDto> getItemsByUser(@RequestHeader("X-Sharer-User-Id") long userId) {
>>>>>>> 61d3a36fb68671b2bc56a32d663def57fc07f660
        log.info("Get all items user={}", userId);
        return itemService.getItemsByUser(userId);
    }

    @GetMapping("/search")
<<<<<<< HEAD
    public List<ItemDto> searchItemByQuery(@RequestParam("text") String text) {
        log.info("Search item text={}", text);
        return itemService.searchItemByQuery(text);
    }

    @PostMapping("/{itemId}/comment")
    public CommentDto addComment(@RequestHeader("X-Sharer-User-Id") long userId,
                                 @PathVariable long itemId,
                                 @RequestBody CommentDto commentDto){
        log.info("Add comment for item {} by user {}", itemId, userId);
        return itemService.addComment(userId, itemId, commentDto);
=======
    public List<ItemDto> search(@RequestParam(name = "text") String text) {
        log.info("Search item text={}", text);
        return itemService.searchItem(text).stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
>>>>>>> 61d3a36fb68671b2bc56a32d663def57fc07f660
    }
}
