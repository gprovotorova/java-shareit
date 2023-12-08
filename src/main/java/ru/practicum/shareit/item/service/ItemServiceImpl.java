package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.enums.BookingStatus;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.ObjectNotFoundException;
import ru.practicum.shareit.exception.ObjectValidationException;
import ru.practicum.shareit.item.dto.ItemDtoWithBooking;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapperWithBooking;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.comments.dto.CommentDto;
import ru.practicum.shareit.comments.mapper.CommentMapper;
import ru.practicum.shareit.comments.model.Comment;
import ru.practicum.shareit.comments.repository.CommentRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;
import static ru.practicum.shareit.item.mapper.ItemMapper.toItemDto;

@Service
@RequiredArgsConstructor
@Transactional
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;

    @Override
    public ItemDto createItem(ItemDto itemDto, long userId) {
        if (itemDto.getName() == null || itemDto.getName().isBlank()) {
            throw new ObjectValidationException("Name is empty.");
        }
        if (itemDto.getDescription() == null || itemDto.getDescription().isBlank()) {
            throw new ObjectValidationException("Description is empty.");
        }
        if (itemDto.getAvailable() == null) {
            throw new ObjectValidationException("Available is empty.");
        }
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ObjectNotFoundException("User with id= " + userId + " not found."));
        Item item = ItemMapper.toItem(itemDto, user);
        item.setOwner(user);
        return toItemDto(itemRepository.save(item));
    }

    @Override
    public ItemDto updateItem(ItemDto itemDto, long userId, long itemId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ObjectNotFoundException("User with id=" + userId + " not found."));
        Item item = ItemMapper.toItem(itemDto, user);
        Item updatedItem = itemRepository.findById(itemId)
                .orElseThrow(() -> new ObjectNotFoundException("Item with id=" + itemId + " not found."));
        if (item.getDescription() != null && !item.getDescription().isBlank()) {
            updatedItem.setDescription(item.getDescription());
        }
        if (item.getName() != null && !item.getName().isBlank()) {
            updatedItem.setName(item.getName());
        }
        if (item.getAvailable() != null) {
            updatedItem.setAvailable(item.getAvailable());
        }
        if (user != null && !updatedItem.getOwner().getId().equals(userId)) {
            throw new ObjectNotFoundException("User with id=" + userId + " not found.");
        }
        return toItemDto(itemRepository.save(updatedItem));
    }

    @Override
    public ItemDtoWithBooking getItemById(long userId, long itemId) {
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new ObjectNotFoundException("Item with id=" + itemId + " not found."));
        List<Comment> comments = getReviewsByItemId(item);
        if (item.getOwner().getId().equals(userId)) {
            Booking lastBooking =
                    bookingRepository.getFirstByItemIdAndStatusNotAndStartBeforeOrderByEndDesc(itemId,
                            BookingStatus.REJECTED, LocalDateTime.now());
            Booking nextBooking =
                    bookingRepository.getFirstByItemIdAndStatusNotAndStartAfterOrderByStart(itemId,
                            BookingStatus.REJECTED, LocalDateTime.now());
            return ItemMapperWithBooking.toItemDtoWithBooking(comments, lastBooking, nextBooking, item);
        } else
            return ItemMapperWithBooking.toItemDtoWithBooking(comments, null, null, item);
    }

    @Override
    public List<ItemDtoWithBooking> getItemsByUser(Long userId) {
        LocalDateTime dateTime = LocalDateTime.now();
        return itemRepository.findByOwnerIdOrderByIdAsc(userId)
                .stream()
                .map(item -> {
                    List<Comment> comments = getReviewsByItemId(item);
                    Booking lastBooking =
                            bookingRepository.getFirstByItemIdAndEndBeforeOrderByEndDesc(item.getId(), dateTime);
                    Booking nextBooking =
                            bookingRepository.getTopByItemIdAndStartAfterOrderByStartAsc(item.getId(), dateTime);
                    return ItemMapperWithBooking.toItemDtoWithBooking(comments, lastBooking, nextBooking, item);
                })
                .collect(toList());
    }

    @Override
    public List<ItemDto> searchItemByQuery(Long userId, String text) {
        if (text.isEmpty() || text.isBlank()) {
            return new ArrayList<>();
        }

        List<ItemDto> items = itemRepository.searchByQuery(text)
                .stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
        return items;
    }

    @Override
    public CommentDto addComment(Long userId, Long itemId, CommentDto commentDto) {
        if (commentDto.getText().isEmpty() || commentDto.getText().isBlank()) {
            throw new ObjectValidationException("This comment is empty.");
        }
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ObjectNotFoundException("User with id=" + userId + " not found."));
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new ObjectNotFoundException("Item with id=" + itemId + " not found."));

        Comment comment = CommentMapper.toComment(commentDto, item, user);
        List<Booking> booking =
                bookingRepository.getByBookerIdStatePast(comment.getUser().getId(), LocalDateTime.now());
        if (booking.isEmpty()) {
            throw new ObjectValidationException("The user has not booked any item.");
        }
        comment.setCreated(LocalDateTime.now());
        return CommentMapper.toCommentDto(commentRepository.save(comment));
    }

    private List<Comment> getReviewsByItemId(Item item) {
        return commentRepository.getByItem_IdOrderByCreatedDesc(item.getId());
    }
}
