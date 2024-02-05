package ru.practicum.shareit.item;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.enums.BookingStatus;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.booking.service.BookingServiceImpl;
import ru.practicum.shareit.comments.dto.CommentDto;
import ru.practicum.shareit.comments.model.Comment;
import ru.practicum.shareit.comments.repository.CommentRepository;
import ru.practicum.shareit.exception.InvalidPathVariableException;
import ru.practicum.shareit.exception.ObjectNotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoWithBooking;
import ru.practicum.shareit.item.mapper.ItemMapperWithBooking;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.item.service.ItemServiceImpl;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.times;
import static org.mockito.ArgumentMatchers.any;

@ExtendWith(MockitoExtension.class)
public class ItemServiceTest {
    @Mock
    private UserRepository userRepository;
    @Mock
    private ItemRepository itemRepository;
    @Mock
    private ItemRequestRepository itemRequestRepository;
    @Mock
    private BookingRepository bookingRepository;
    @Mock
    private CommentRepository commentRepository;
    @InjectMocks
    private ItemServiceImpl itemService;
    @InjectMocks
    private BookingServiceImpl bookingService;

    private static final int FROM = 0;
    private static final int SIZE = 10;
    private static final String SEARCH_TEXT = "notebook";
    private static final LocalDateTime DATE =
            LocalDateTime.of(2023, 12, 10, 12, 30, 0);

    private final User galina = new User(
            1L,
            "Galina",
            "galina@mail.ru");
    private final User anna = new User(
            2L,
            "Anna",
            "anna@mail.ru");
    private final ItemDto notebookDto = new ItemDto(
            1L,
            "Notebook",
            "Notebook description",
            true,
            galina,
            null);
    private final Item notebook = new Item(
            1L,
            "Notebook",
            "Notebook description",
            true,
            galina,
            null,
            null);
    private final Booking lastBooking =
            new Booking(1L, DATE, DATE.plusDays(2), notebook, anna, BookingStatus.APPROVED);
    private final Booking nextBooking =
            new Booking(1L, DATE.plusDays(3), DATE.plusDays(4), notebook, anna, BookingStatus.APPROVED);

    private final ItemDtoWithBooking notebookDtoWithBooking =
            ItemMapperWithBooking.toItemDtoWithBooking(new ArrayList<>(), lastBooking, nextBooking, notebook);
    private final List<Item> items = List.of(
            new Item(1L,
                    "Notebook",
                    "Notebook description",
                    true,
                    galina,
                    null,
                    null),
            new Item(2L,
                    "Book",
                    "Book description",
                    true,
                    galina,
                    null,
                    null)
    );

    @Transactional
    @Test
    void createItem_shouldCreateItem() {
        when(userRepository.findById(galina.getId())).thenReturn(Optional.of(galina));
        when(itemRepository.save(any(Item.class))).thenReturn(notebook);

        ItemDto result = itemService.createItem(notebookDto, galina.getId());

        assertNotNull(result);
        assertEquals(notebookDto.getName(), result.getName());

        verify(userRepository, times(1)).findById(galina.getId());
        verify(itemRepository, times(1)).save(any(Item.class));
    }

    @Transactional
    @Test
    void createItem_shouldThrowExceptionIfUserIdIsIncorrect() {
        when(userRepository.findById(any(Long.class)))
                .thenThrow(new ObjectNotFoundException("User with id=" + galina.getId() + " not found."));

        assertThrows(ObjectNotFoundException.class, () -> itemService.createItem(notebookDto, galina.getId()));

        verify(userRepository, times(1)).findById(galina.getId());
        verify(itemRequestRepository, times(0)).findById(any(Long.class));
        verify(itemRepository, times(0)).save(any(Item.class));
    }

    @Transactional
    @Test
    void updateItem_shouldUpdateItem() {
        when(userRepository.findById(any(Long.class))).thenReturn(Optional.of(galina));
        when(itemRepository.findById(any(Long.class))).thenReturn(Optional.of(notebook));
        when(itemRepository.save(any(Item.class))).thenReturn(notebook);

        ItemDto result = itemService.updateItem(notebookDto, galina.getId(), notebook.getId());

        assertNotNull(result);
        assertEquals(notebookDto.getName(), result.getName());
        assertEquals(notebookDto.getDescription(), result.getDescription());
        assertEquals(notebookDto.getAvailable(), result.getAvailable());

        verify(userRepository, times(1)).findById(galina.getId());
        verify(itemRepository, times(1)).findById(notebook.getId());
        verify(itemRepository, times(1)).save(any(Item.class));
    }

    @Transactional
    @Test
    void updateItem_shouldThrowExceptionIfItemIdIsIncorrect() {
        assertThrows(ObjectNotFoundException.class, () -> itemService.createItem(notebookDto, galina.getId()));

        verify(userRepository, times(1)).findById(galina.getId());
        verify(itemRequestRepository, times(0)).findById(any(Long.class));
        verify(itemRepository, times(0)).save(any(Item.class));
    }

    @Transactional
    @Test
    void updateItem_shouldThrowExceptionIfUserIdIsIncorrect() {
        assertThrows(ObjectNotFoundException.class,
                () -> itemService.updateItem(notebookDto, 100L, notebookDto.getId()));

        verify(userRepository, times(1)).findById(100L);
        verify(itemRequestRepository, times(0)).findById(any(Long.class));
        verify(itemRepository, times(0)).save(any(Item.class));
    }

    @Transactional
    @Test
    void updateItem_shouldThrowExceptionIfNotUserUpdating() {
        when(userRepository.findById(any(Long.class)))
                .thenThrow(new ObjectNotFoundException("Item with id=" + notebookDto.getId() + " not found."));

        assertThrows(ObjectNotFoundException.class, () -> itemService.createItem(notebookDto, galina.getId()));

        verify(userRepository, times(1)).findById(galina.getId());
        verify(itemRequestRepository, times(0)).findById(any(Long.class));
        verify(itemRepository, times(0)).save(any(Item.class));
    }

    @Transactional
    @Test
    void updateItem_shouldUpdateIfItemNameIsNull() {
        ItemDto notebookDtoNullName = new ItemDto(
                1L,
                null,
                "Notebook description",
                true,
                galina,
                null);

        Item notebookNullName = new Item(
                1L,
                null,
                "Notebook description",
                true,
                galina,
                null,
                null);

        when(userRepository.findById(any(Long.class))).thenReturn(Optional.of(galina));
        when(itemRepository.findById(notebookDtoNullName.getId())).thenReturn(Optional.of(notebookNullName));
        when(itemRepository.save(any(Item.class))).thenReturn(notebookNullName);

        ItemDto updatedItem = itemService.updateItem(new ItemDto(notebookDtoNullName.getId(),
                        notebookDtoNullName.getName(), notebookDtoNullName.getDescription(),
                        notebookDtoNullName.getAvailable(), notebookDtoNullName.getOwner(),
                        notebookDtoNullName.getRequestId()),
                galina.getId(), notebookDtoNullName.getId());

        assertEquals(notebookDtoNullName.getDescription(), updatedItem.getDescription());
        assertEquals(notebookDtoNullName.getAvailable(), updatedItem.getAvailable());

        verify(userRepository, times(1)).findById(galina.getId());
        verify(itemRequestRepository, times(0)).findById(any(Long.class));
    }

    @Transactional
    @Test
    void updateItem_shouldUpdateIfItemDescriptionIsNull() {
        ItemDto notebookDtoNullDescription = new ItemDto(
                1L,
                "Notebook",
                null,
                true,
                galina,
                null);

        Item notebookNullDescription = new Item(
                1L,
                "Notebook",
                null,
                true,
                galina,
                null,
                null);

        when(userRepository.findById(any(Long.class))).thenReturn(Optional.of(galina));
        when(itemRepository.findById(notebookDtoNullDescription.getId()))
                .thenReturn(Optional.of(notebookNullDescription));
        when(itemRepository.save(any(Item.class))).thenReturn(notebookNullDescription);

        ItemDto updatedItem = itemService.updateItem(new ItemDto(notebookDtoNullDescription.getId(),
                        notebookDtoNullDescription.getName(), notebookDtoNullDescription.getDescription(),
                        notebookDtoNullDescription.getAvailable(), notebookDtoNullDescription.getOwner(),
                        notebookDtoNullDescription.getRequestId()),
                galina.getId(), notebookDtoNullDescription.getId());

        assertEquals(notebookDtoNullDescription.getName(), updatedItem.getName());
        assertEquals(notebookDtoNullDescription.getAvailable(), updatedItem.getAvailable());

        verify(userRepository, times(1)).findById(galina.getId());
        verify(itemRequestRepository, times(0)).findById(any(Long.class));
    }

    @Transactional
    @Test
    void updateItem_shouldUpdateIfItemAvailableIsNull() {
        ItemDto notebookDtoNullAvailable = new ItemDto(
                1L,
                "Notebook",
                "Notebook description",
                null,
                galina,
                null);

        Item notebookNullAvailable = new Item(
                1L,
                "Notebook",
                "Notebook description",
                null,
                galina,
                null,
                null);

        when(userRepository.findById(any(Long.class))).thenReturn(Optional.of(galina));
        when(itemRepository.findById(notebookDtoNullAvailable.getId())).thenReturn(Optional.of(notebookNullAvailable));
        when(itemRepository.save(any(Item.class))).thenReturn(notebookNullAvailable);

        ItemDto updatedItem = itemService.updateItem(new ItemDto(notebookDtoNullAvailable.getId(),
                        notebookDtoNullAvailable.getName(), notebookDtoNullAvailable.getDescription(),
                        notebookDtoNullAvailable.getAvailable(), notebookDtoNullAvailable.getOwner(),
                        notebookDtoNullAvailable.getRequestId()),
                galina.getId(), notebookDtoNullAvailable.getId());

        assertEquals(notebookDtoNullAvailable.getName(), updatedItem.getName());
        assertEquals(notebookDtoNullAvailable.getAvailable(), updatedItem.getAvailable());

        verify(userRepository, times(1)).findById(galina.getId());
        verify(itemRequestRepository, times(0)).findById(any(Long.class));
    }

    @Transactional
    @Test
    void getItemById_shouldThrowExceptionIfIdIsIncorrect() {
        when(itemRepository.findById(notebook.getId())).thenReturn(Optional.empty());

        ObjectNotFoundException exception = assertThrows(ObjectNotFoundException.class,
                () -> itemService.getItemById(galina.getId(), notebook.getId()));

        assertEquals("Item with id=" + notebook.getId() + " not found.", exception.getMessage());

        verify(itemRepository, times(1)).findById(notebook.getId());
    }

    @Transactional
    @Test
    void getItemById_shouldReturnItem() {
        when(itemRepository.findById(notebook.getId())).thenReturn(Optional.of(notebook));
        when(bookingRepository.getFirstByItemIdAndStatusNotAndStartBeforeOrderByEndDesc(any(Long.class),
                any(BookingStatus.class), any(LocalDateTime.class))).thenReturn(lastBooking);
        when(bookingRepository.getFirstByItemIdAndStatusNotAndStartAfterOrderByStart(any(Long.class),
                any(BookingStatus.class), any(LocalDateTime.class))).thenReturn(nextBooking);

        ItemDtoWithBooking savedItem = itemService.getItemById(galina.getId(), notebook.getId());

        assertEquals(notebookDtoWithBooking, savedItem);

        verify(itemRepository, times(1)).findById(notebook.getId());
    }

    @Transactional
    @Test
    void getItemsByUser_shouldReturnByUserId() {
        Booking lastBookingFirstItem =
                new Booking(1L, DATE, DATE.plusDays(2), items.get(0), anna, BookingStatus.APPROVED);
        Booking nextBookingFirstItem =
                new Booking(1L, DATE.plusDays(3), DATE.plusDays(4), items.get(0), anna, BookingStatus.APPROVED);
        Booking lastBookingSecondItem =
                new Booking(1L, DATE.plusDays(5), DATE.plusDays(6), items.get(1), anna, BookingStatus.APPROVED);
        Booking nextBookingSecondItem =
                new Booking(1L, DATE.plusDays(7), DATE.plusDays(8), items.get(1), anna, BookingStatus.APPROVED);

        List<ItemDtoWithBooking> itemDtoWithBooking = List.of(
                ItemMapperWithBooking.toItemDtoWithBooking(
                        new ArrayList<>(),
                        lastBookingFirstItem,
                        nextBookingFirstItem,
                        items.get(0)),
                ItemMapperWithBooking.toItemDtoWithBooking(
                        new ArrayList<>(),
                        lastBookingSecondItem,
                        nextBookingSecondItem,
                        items.get(1)));

        PageImpl pageRequests = new PageImpl(items);

        when(userRepository.findById(any(Long.class))).thenReturn(Optional.of(galina));
        when(itemRepository.findByOwnerIdOrderByIdAsc(any(Long.class), any(Pageable.class))).thenReturn(pageRequests);

        List<ItemDtoWithBooking> savedItemDtoWithBooking = itemService.getItemsByUser(galina.getId(), FROM, SIZE);

        assertEquals(itemDtoWithBooking.size(), savedItemDtoWithBooking.size());

        verify(userRepository, times(1)).findById(galina.getId());
    }

    @Transactional
    @Test
    void getItemsByUser_shouldReturnByUserIdIfLimitIsNull() {
        Booking lastBookingFirstItem =
                new Booking(1L, DATE, DATE.plusDays(2), items.get(0), anna, BookingStatus.APPROVED);
        Booking nextBookingFirstItem =
                new Booking(1L, DATE.plusDays(3), DATE.plusDays(4), items.get(0), anna, BookingStatus.APPROVED);
        Booking lastBookingSecondItem =
                new Booking(1L, DATE.plusDays(5), DATE.plusDays(6), items.get(1), anna, BookingStatus.APPROVED);
        Booking nextBookingSecondItem =
                new Booking(1L, DATE.plusDays(7), DATE.plusDays(8), items.get(1), anna, BookingStatus.APPROVED);

        List<ItemDtoWithBooking> itemDtoWithBooking = List.of(
                ItemMapperWithBooking.toItemDtoWithBooking(
                        new ArrayList<>(),
                        lastBookingFirstItem,
                        nextBookingFirstItem,
                        items.get(0)),
                ItemMapperWithBooking.toItemDtoWithBooking(
                        new ArrayList<>(),
                        lastBookingSecondItem,
                        nextBookingSecondItem,
                        items.get(1)));

        when(userRepository.findById(any(Long.class))).thenReturn(Optional.of(galina));
        when(itemRepository.findByOwnerIdOrderByIdAsc(any(Long.class))).thenReturn(items);

        List<ItemDtoWithBooking> savedItemDtoWithBooking = itemService.getItemsByUser(galina.getId(), FROM, null);

        assertEquals(itemDtoWithBooking.size(), savedItemDtoWithBooking.size());

        verify(userRepository, times(1)).findById(galina.getId());
    }

    @Transactional
    @Test
    void searchItemByQuery_shouldReturnItemIfLimitIsNull() {
        when(userRepository.findById(any(Long.class))).thenReturn(Optional.of(galina));
        when(itemRepository.searchByQuery(eq(SEARCH_TEXT))).thenReturn(List.of(notebook));

        List<ItemDto> savedItems = itemService.searchItemByQuery(galina.getId(), SEARCH_TEXT, FROM, null);

        assertFalse(savedItems.isEmpty());
        assertEquals(notebookDto, savedItems.get(0));

        verify(itemRepository, times(1)).searchByQuery(any(String.class));
    }

    @Transactional
    @Test
    void searchItemByQuery_shouldReturnItem() {
        List<ItemDto> listDto = List.of(
                new ItemDto(
                        1L,
                        "Notebook",
                        "Notebook description",
                        true,
                        galina,
                        null),
                new ItemDto(
                        2L,
                        "Book",
                        "Book description",
                        true,
                        galina,
                        null)
        );

        Page<Item> page = new PageImpl<>(items);

        when(userRepository.findById(any(Long.class))).thenReturn(Optional.of(galina));
        when(itemRepository.searchByQuery(eq(SEARCH_TEXT), any(Pageable.class))).thenReturn(page);

        List<ItemDto> savedItems = itemService.searchItemByQuery(galina.getId(), SEARCH_TEXT, FROM, SIZE);

        assertFalse(savedItems.isEmpty(), "The list must not be empty.");
        assertEquals(listDto.size(), savedItems.size(), "The list sizes must match.");

        for (int i = 0; i < listDto.size(); i++) {
            ItemDto expectedItemDto = listDto.get(i);
            ItemDto actualItemDto = savedItems.get(i);

            assertEquals(expectedItemDto.getId(), actualItemDto.getId(),
                    "Ids of elements must match.");
            assertEquals(expectedItemDto.getName(), actualItemDto.getName(),
                    "Element names must match.");
            assertEquals(expectedItemDto.getDescription(), actualItemDto.getDescription(),
                    "Item descriptions must match.");
            assertEquals(expectedItemDto.getAvailable(), actualItemDto.getAvailable(),
                    "Element availability must match.");
        }
    }

    @Transactional
    @Test
    void searchItemByQuery_shouldThrowExceptionIfFromLessThanZero() {
        when(userRepository.findById(any(Long.class))).thenReturn(Optional.of(galina));

        assertThrows(InvalidPathVariableException.class,
                () -> itemService.searchItemByQuery(galina.getId(), SEARCH_TEXT, -1, SIZE));
    }

    @Transactional
    @Test
    void searchItemByQuery_shouldThrowExceptionIfSizeIsZero() {
        when(userRepository.findById(any(Long.class))).thenReturn(Optional.of(galina));

        assertThrows(InvalidPathVariableException.class,
                () -> itemService.searchItemByQuery(galina.getId(), SEARCH_TEXT, FROM, 0));
    }

    @Transactional
    @Test
    void addComment_shouldAddComment() {
        Comment comment = new Comment(1L, "Comment text", notebook, galina, DATE);

        CommentDto commentDto = new CommentDto(1L, "Comment text", "Galina", DATE);

        Booking booking = new Booking(
                1L,
                LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(2),
                notebook,
                anna,
                BookingStatus.APPROVED
        );

        when(itemRepository.findById(any(Long.class))).thenReturn(Optional.of(notebook));
        when(userRepository.findById(any(Long.class))).thenReturn(Optional.of(galina));
        when(userRepository.findById(any(Long.class))).thenReturn(Optional.of(anna));
        when(bookingRepository.save(any(Booking.class))).thenReturn(booking);

        bookingService.createBooking(
                new BookingDto(booking.getId(), booking.getStart(), booking.getEnd(),
                        new BookingDto.Item(notebook.getId(), notebook.getName()),
                        booking.getItem().getId(),
                        new BookingDto.User(anna.getId(), anna.getName()),
                        BookingStatus.WAITING), 2L);

        when(bookingRepository.getByBookerIdStatePast(any(Long.class), any(LocalDateTime.class)))
                .thenReturn(List.of(booking));
        when(commentRepository.save(any(Comment.class))).thenReturn(comment);

        CommentDto savedComment = itemService.addComment(anna.getId(), notebook.getId(), commentDto);

        assertNotNull(savedComment, "Created comment must not be null.");
        assertEquals(commentDto.getText(), savedComment.getText(), "The comment text must match.");
        assertEquals(galina.getName(), savedComment.getAuthorName(),
                "The name of the comment author must match.");
        assertNotNull(savedComment.getCreated(), "The comment creation date must not be null.");
    }
}
