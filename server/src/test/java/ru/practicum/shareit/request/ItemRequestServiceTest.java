package ru.practicum.shareit.request;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.common.PageMaker;
import ru.practicum.shareit.exception.ObjectNotFoundException;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.request.service.ItemRequestServiceImpl;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.times;
import static org.springframework.data.domain.Sort.Direction.DESC;

@ExtendWith(MockitoExtension.class)
public class ItemRequestServiceTest {
    @Mock
    private ItemRequestRepository itemRequestRepository;
    @Mock
    private ItemRepository itemRepository;
    @Mock
    private UserRepository userRepository;
    @InjectMocks
    private ItemRequestServiceImpl itemRequestService;

    private static final LocalDateTime DATE = LocalDateTime.now();
    private static final int FROM = 0;
    private static final int SIZE = 10;

    private final User galina = new User(1L, "Galina", "galina@mail.ru");
    private final User anna = new User(2L, "Anna", "anna@mail.ru");
    private final ItemRequest request = new ItemRequest(1L, "Request 1", galina, DATE);
    private final ItemRequest secondRequest = new ItemRequest(2L, "Request 2", galina, DATE.plusDays(1));
    private final ItemRequest thirdRequest = new ItemRequest(3L, "Request 3", anna, DATE.plusDays(2));
    private final List<ItemRequest> requests = List.of(request, secondRequest, thirdRequest);
    private final ItemRequestDto requestDto = new ItemRequestDto(1L, "Request 1", galina, DATE,
            new ArrayList<>());
    private final ItemRequestDto secondRequestDto =
            new ItemRequestDto(2L, "Request 2", galina, DATE.plusDays(1), new ArrayList<>());
    private final ItemRequestDto thirdRequestDto =
            new ItemRequestDto(3L, "Request 3", anna, DATE.plusDays(2), new ArrayList<>());
    private final List<ItemRequestDto> requestDtos = List.of(requestDto, secondRequestDto, thirdRequestDto);

    @Transactional
    @Test
    void createRequest_shouldCreateRequest() {
        when(userRepository.findById(galina.getId())).thenReturn(Optional.of(galina));
        when(itemRequestRepository.save(any(ItemRequest.class))).thenReturn(request);

        ItemRequestDto itemRequestServiceRequest =
                itemRequestService.createRequest(requestDto, galina.getId(), DATE);

        assertNotNull(itemRequestServiceRequest, "The created object must not be null.");
        assertEquals(request.getId(), itemRequestServiceRequest.getId(), "The request ID must match.");
        assertEquals(itemRequestServiceRequest.getDescription(), itemRequestServiceRequest.getDescription(),
                "The request description must match.");
        assertNotNull(itemRequestServiceRequest.getCreated(), "Creation date must not be null.");

        verify(userRepository, times(1)).findById(galina.getId());
        verify(itemRequestRepository, times(1)).save(any(ItemRequest.class));
    }

    @Transactional
    @Test
    void createRequest_shouldThrowExceptionIfUserIdIsIncorrect() {
        assertThrows(ObjectNotFoundException.class,
                () -> itemRequestService.createRequest(requestDto, 100L, DATE));
    }

    @Transactional
    @Test
    void getRequestsByOwner_shouldReturnRequests() {
        when(userRepository.existsById(any(Long.class))).thenReturn(true);
        when(itemRequestRepository.findAllByRequesterId(galina.getId(),
                Sort.by(DESC, "created"))).thenReturn(requests);
        when(itemRepository.findByRequestId(galina.getId())).thenReturn(Collections.emptyList());

        List<ItemRequestDto> savedRequestDtos = itemRequestService.getRequestsByOwner(galina.getId());

        assertEquals(requestDtos.size(), savedRequestDtos.size());
        assertEquals(requestDtos.get(0), savedRequestDtos.get(0));
        assertEquals(requestDtos.get(1), savedRequestDtos.get(1));
        assertEquals(requestDtos.get(2), savedRequestDtos.get(2));

        verify(userRepository, times(1)).existsById(galina.getId());
    }

    @Transactional
    @Test
    void getAllRequests_shouldReturnExistingRequests() {
        PageImpl pageRequests = new PageImpl(requests);

        when(userRepository.existsById(any(Long.class))).thenReturn(true);
        when(itemRequestRepository.findByRequesterIdNot(any(Long.class), any(Pageable.class))).thenReturn(pageRequests);
        when(itemRepository.findByRequestId(galina.getId())).thenReturn(Collections.emptyList());

        Pageable page = PageMaker.makePageableWithSort(FROM, SIZE);

        List<ItemRequestDto> savedRequestDtos = itemRequestService.getAllRequests(1L, page);

        assertEquals(requestDtos.size(), savedRequestDtos.size());
        assertEquals(requestDtos.get(0), savedRequestDtos.get(0));
        assertEquals(requestDtos.get(1), savedRequestDtos.get(1));
        assertEquals(requestDtos.get(2), savedRequestDtos.get(2));

        verify(userRepository, times(1)).existsById(galina.getId());
    }

    @Transactional
    @Test
    void getAllRequests_shouldThrowExceptionIfUserIdIsIncorrect() {
        Pageable page = PageMaker.makePageableWithSort(FROM, SIZE);

        when(userRepository.existsById(any(Long.class))).thenReturn(false);

        assertThrows(NullPointerException.class,
                () -> itemRequestService.getAllRequests(100L, page));
    }

    @Transactional
    @Test
    void getAllRequests_shouldReturnAllRequestsIfSizeIsNull() {
        when(userRepository.existsById(any(Long.class))).thenReturn(true);
        when(itemRequestRepository.findAllByRequesterId(galina.getId(),
                Sort.by(DESC, "created"))).thenReturn(requests);
        when(itemRepository.findByRequestId(galina.getId())).thenReturn(Collections.emptyList());

        Pageable page = PageMaker.makePageableWithSort(FROM, null);

        List<ItemRequestDto> savedRequestDtos = itemRequestService.getAllRequests(1L, page);

        assertEquals(requestDtos.size(), savedRequestDtos.size());
        assertEquals(requestDtos.get(0), savedRequestDtos.get(0));
        assertEquals(requestDtos.get(1), savedRequestDtos.get(1));
        assertEquals(requestDtos.get(2), savedRequestDtos.get(2));

        verify(userRepository, times(1)).existsById(galina.getId());
    }

    @Transactional
    @Test
    void getRequestById_shouldReturnRequest() {
        when(userRepository.existsById(any(Long.class))).thenReturn(true);
        when(itemRepository.findByRequestId(galina.getId())).thenReturn(Collections.emptyList());
        when(itemRequestRepository.findById(any(Long.class))).thenReturn(Optional.of(request));

        ItemRequestDto savedRequestDto = itemRequestService.getRequestById(1L, 1L);

        assertEquals(requestDto, savedRequestDto);
    }

    @Transactional
    @Test
    void getRequestById_shouldThrowExceptionIfRequestIdIsIncorrect() {
        assertThrows(ObjectNotFoundException.class,
                () -> itemRequestService.getRequestById(100L, 100L));
    }

    @Transactional
    @Test
    void getRequestById_shouldThrowExceptionIfUserIdIsIncorrect() {
        assertThrows(ObjectNotFoundException.class,
                () -> itemRequestService.getRequestById(100L, 100L));
    }
}
