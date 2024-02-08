package ru.practicum.shareit.request.service;

import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import java.time.LocalDateTime;
import java.util.List;

public interface ItemRequestService {
    ItemRequestDto createRequest(ItemRequestDto requestDto, Long userId, LocalDateTime date);

    List<ItemRequestDto> getRequestsByOwner(Long userId);

    //List<ItemRequestDto> getAllRequests(Long userId, Integer from, Integer size);

    List<ItemRequestDto> getAllRequests(Long userId, Pageable page);

    ItemRequestDto getRequestById(Long userId, Long requestId);
}
