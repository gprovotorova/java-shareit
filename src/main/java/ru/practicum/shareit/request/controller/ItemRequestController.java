package ru.practicum.shareit.request.controller;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.PathVariable;
import ru.practicum.shareit.common.PageMaker;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.service.ItemRequestService;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping(path = "/requests")
@Slf4j
@AllArgsConstructor
public class ItemRequestController {

    private final ItemRequestService requestService;

    @PostMapping
    public ItemRequestDto save(@RequestHeader("X-Sharer-User-Id") Long userId,
                               @Validated @RequestBody ItemRequestDto requestDto) {
        log.info("Creating request {} from user {}", requestDto, userId);
        return requestService.createRequest(requestDto, userId, LocalDateTime.now());
    }

    @GetMapping
    public List<ItemRequestDto> getRequestsByOwner(@RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("Get requests by user {}", userId);
        return requestService.getRequestsByOwner(userId);
    }

    @GetMapping("/all")
    public List<ItemRequestDto> getAllRequests(@RequestHeader("X-Sharer-User-Id") Long userId,
                                               @RequestParam(required = false) Integer from,
                                               @RequestParam(required = false) Integer size) {
        log.info("Get requests by user page by page {}", userId);
        Pageable page = PageMaker.makePageableWithSort(from, size);
        return requestService.getAllRequests(userId, page);
    }

    @GetMapping("/{requestId}")
    public ItemRequestDto getRequestById(@RequestHeader("X-Sharer-User-Id") Long userId,
                                         @PathVariable Long requestId) {
        log.info("Get request by requestId {} from user {}", requestId, userId);
        return requestService.getRequestById(userId, requestId);
    }
}
