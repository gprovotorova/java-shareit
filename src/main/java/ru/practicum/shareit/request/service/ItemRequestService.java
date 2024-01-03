package ru.practicum.shareit.request.service;

import ru.practicum.shareit.request.dto.ItemRequestDto;

import java.time.LocalDateTime;
import java.util.List;

public interface ItemRequestService {
    ItemRequestDto createRequest(ItemRequestDto requestDto, Long userId, LocalDateTime date);

    List<ItemRequestDto> getRequestsByOwner(Long userId);

    List<ItemRequestDto> getAllRequests(Long userId, Integer from, Integer size);

    ItemRequestDto getRequestById(Long userId, Long requestId);
}
