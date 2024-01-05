package ru.practicum.shareit.request.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.InvalidPathVariableException;
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
import static ru.practicum.shareit.request.mapper.ItemRequestMapper.toItemRequestDtoWithItems;

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
        return toItemRequestDtoWithItems(requestRepository.save(request), new ArrayList<>());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ItemRequestDto> getRequestsByOwner(Long userId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new ObjectNotFoundException("User with id= " + userId + " not found."));
        return requestRepository.findAllByRequesterId(userId, Sort.by(DESC, "created"))
                .stream()
                .map(itemRequest -> ItemRequestMapper.toItemRequestDtoWithItems(itemRequest,
                        ItemMapper.toItemDto(itemRepository.findByRequestId(itemRequest.getRequester().getId()))))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ItemRequestDto> getAllRequests(Long userId, Integer from, Integer size) {
        userRepository.findById(userId)
                .orElseThrow(() -> new ObjectNotFoundException("User with id= " + userId + " not found."));
        if (from == null || size == null) {
            return requestRepository.findAllByRequesterId(userId, Sort.by(DESC, "created"))
                    .stream()
                    .map(itemRequest -> ItemRequestMapper.toItemRequestDtoWithItems(itemRequest,
                            ItemMapper.toItemDto(itemRepository
                                    .findByRequestId(itemRequest.getRequester().getId()))))
                    .collect(Collectors.toList());
        } else if (from < 0 || size <= 0) {
            throw new InvalidPathVariableException("Incorrect page parameters");
        } else {
            int pageNumber = from / size;
            final Pageable page = PageRequest.of(pageNumber, size, Sort.by(DESC, "id"));
            return requestRepository.findByRequesterIdNot(userId, page)
                    .stream()
                    .map(itemRequest -> ItemRequestMapper.toItemRequestDtoWithItems(itemRequest,
                            ItemMapper.toItemDto(itemRepository.findByRequestId(itemRequest.getRequester().getId()))))
                    .collect(Collectors.toList());
        }
    }

    @Override
    @Transactional(readOnly = true)
    public ItemRequestDto getRequestById(Long userId, Long requestId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new ObjectNotFoundException("User with id= " + userId + " not found."));
        ItemRequest itemRequest = requestRepository.findById(requestId)
                .orElseThrow(() -> new ObjectNotFoundException("Request with id= " + requestId + " not found."));
        return ItemRequestMapper.toItemRequestDtoWithItems(itemRequest,
                ItemMapper.toItemDto(itemRepository.findByRequestId(itemRequest.getRequester().getId())));
    }
}
