package ru.practicum.shareit.request.controller;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.PathVariable;
import ru.practicum.shareit.request.ItemRequestClient;
import ru.practicum.shareit.request.dto.ItemRequestShortDto;

import javax.validation.constraints.Min;

@RestController
@RequestMapping(path = "/requests")
@Slf4j
@AllArgsConstructor
@Validated
public class ItemRequestController {

    private final ItemRequestClient itemRequestClient;

    @PostMapping
    public ResponseEntity save(@RequestHeader("X-Sharer-User-Id") Long userId,
                               @Validated @RequestBody ItemRequestShortDto requestDto) {
        log.info("Creating request {} from user {}", requestDto, userId);
        return itemRequestClient.createRequest(requestDto, userId);
    }

    @GetMapping
    public ResponseEntity getRequestsByOwner(@RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("Get requests by user {}", userId);
        return itemRequestClient.getRequestsByOwner(userId);
    }

    @GetMapping("/all")
    public ResponseEntity getAllRequests(@RequestHeader("X-Sharer-User-Id") Long userId,
                                         @RequestParam(defaultValue = "0") @Min(0) int from,
                                         @RequestParam(defaultValue = "10") @Min(1) int size) {
        log.info("Get requests by user page by page {}", userId);
        return itemRequestClient.getAllRequests(userId, from, size);
    }

    @GetMapping("/{requestId}")
    public ResponseEntity getRequestById(@RequestHeader("X-Sharer-User-Id") Long userId,
                                         @PathVariable Long requestId) {
        log.info("Get request by requestId {} from user {}", requestId, userId);
        return itemRequestClient.getRequestById(userId, requestId);
    }
}
