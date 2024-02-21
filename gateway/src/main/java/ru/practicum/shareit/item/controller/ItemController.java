package ru.practicum.shareit.item.controller;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
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
import ru.practicum.shareit.common.Update;
import ru.practicum.shareit.item.ItemClient;
import ru.practicum.shareit.comments.CommentDto;
import ru.practicum.shareit.item.dto.ItemShortDto;

import javax.validation.Valid;

@RestController
@RequestMapping("/items")
@Slf4j
@AllArgsConstructor
@Validated
public class ItemController {

    private final ItemClient itemClient;

    @PostMapping
    public ResponseEntity create(@RequestHeader("X-Sharer-User-Id") long userId,
                                 @RequestBody @Validated({Create.class}) ItemShortDto itemDto) {
        log.info("Creating item {}", itemDto);
        return itemClient.createItem(itemDto, userId);
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity update(@PathVariable long itemId,
                                 @RequestHeader("X-Sharer-User-Id") long userId,
                                 @RequestBody @Validated({Update.class}) ItemShortDto itemDto) {
        log.info("Updating item {}", itemDto);
        return itemClient.updateItem(userId, itemId, itemDto);
    }

    @GetMapping(value = "/{itemId}")
    public ResponseEntity getById(@RequestHeader("X-Sharer-User-Id") long userId,
                                  @PathVariable long itemId) {
        log.info("Get item id={}", itemId);
        return itemClient.getItemById(userId, itemId);
    }

    @GetMapping
    public ResponseEntity getItemsByUser(@RequestHeader("X-Sharer-User-Id") long userId,
                                         @RequestParam(defaultValue = "0") Integer from,
                                         @RequestParam(defaultValue = "10") Integer size) {
        log.info("Get all items user={}", userId);
        return itemClient.getItemsByUser(userId, from, size);
    }

    @GetMapping("/search")
    public ResponseEntity searchItemByQuery(@RequestHeader("X-Sharer-User-Id") Long userId,
                                            @RequestParam String text,
                                            @RequestParam(defaultValue = "0") Integer from,
                                            @RequestParam(defaultValue = "10") Integer size) {
        log.info("Search item text={}", text);
        return itemClient.searchItemByQuery(userId, text, from, size);
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity addComment(@RequestHeader("X-Sharer-User-Id") long userId,
                                     @PathVariable("itemId") long itemId,
                                     @RequestBody @Valid CommentDto commentDto) {
        log.info("Add comment for item {} by user {}", itemId, userId);
        return itemClient.addComment(userId, itemId, commentDto);
    }
}
