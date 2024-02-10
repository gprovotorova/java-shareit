package ru.practicum.shareit.request.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.ObjectNotFoundException;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.mapper.ItemRequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.data.domain.Sort.Direction.DESC;
import static ru.practicum.shareit.request.mapper.ItemRequestMapper.toItemRequestDto;

@Service
@RequiredArgsConstructor
public class ItemRequestServiceImpl implements ItemRequestService {

    private final ItemRequestRepository requestRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    @Override
    @Transactional
    public ItemRequestDto createRequest(ItemRequestDto requestDto, Long userId, LocalDateTime date) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ObjectNotFoundException("User with id= " + userId + " not found."));
        ItemRequest request = ItemRequestMapper.toItemRequest(requestDto, user, date);
        return toItemRequestDto(requestRepository.save(request), new ArrayList<>());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ItemRequestDto> getRequestsByOwner(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new ObjectNotFoundException("User with id= " + userId + " not found.");
        }
        List<ItemRequest> requests = requestRepository.findAllByRequesterId(userId,
                Sort.by(DESC, "created"));
        return requests.stream()
                .map(itemRequest -> ItemRequestMapper.toItemRequestDto(itemRequest,
                        ItemMapper.toItemDto(itemRepository.findByRequestId(itemRequest.getRequester().getId()))))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ItemRequestDto> getAllRequests(Long userId, Pageable page) {
        userRepository.existsById(userId);
        if (page.isUnpaged()) {
            List<ItemRequest> requests = requestRepository.findAllByRequesterId(userId,
                    Sort.by(DESC, "created"));
            return requests
                    .stream()
                    .map(itemRequest -> ItemRequestMapper.toItemRequestDto(itemRequest,
                            ItemMapper.toItemDto(itemRepository.findByRequestId(itemRequest.getRequester().getId()))))
                    .collect(Collectors.toList());
        } else {
            return requestRepository.findByRequesterIdNot(userId, page)
                    .stream()
                    .map(itemRequest -> ItemRequestMapper.toItemRequestDto(itemRequest,
                            ItemMapper.toItemDto(itemRepository.findByRequestId(itemRequest.getRequester().getId()))))
                    .collect(Collectors.toList());
        }
    }

    @Override
    @Transactional(readOnly = true)
    public ItemRequestDto getRequestById(Long userId, Long requestId) {
        if (!userRepository.existsById(userId)) {
            throw new ObjectNotFoundException("User with id= " + userId + " not found.");
        }
        ItemRequest itemRequest = requestRepository.findById(requestId)
                .orElseThrow(() -> new ObjectNotFoundException("Request with id= " + requestId + " not found."));
        return ItemRequestMapper.toItemRequestDto(itemRequest,
                ItemMapper.toItemDto(itemRepository.findByRequestId(itemRequest.getRequester().getId())));
    }
}
