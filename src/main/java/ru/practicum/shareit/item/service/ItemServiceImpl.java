package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
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
                .orElseThrow(()-> new ObjectNotFoundException("User with id= " + userId + " not found."));
        Item item = ItemMapper.toItem(itemDto, user);
        item.setOwner(user);
        return ItemMapper.toItemDto(itemRepository.save(item));
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
        return ItemMapper.toItemDto(itemRepository.save(updatedItem));
    }

    @Override
    public ItemDtoWithBooking getItemById(long userId, long itemId) {
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new ObjectNotFoundException("Item with id=" + itemId + " not found."));
        List<Comment> comments = getReviewsByItemId(item);
        if(item.getOwner().getId().equals(userId)){
            LocalDateTime dateTime = LocalDateTime.now();
            Booking lastBooking =
                    bookingRepository.getFirstByItemIdAndEndBeforeOrderByEndDesc(itemId, dateTime);
            Booking nextBooking =
                    bookingRepository.getTopByItemIdAndStartAfterOrderByStartAsc(itemId, dateTime);
            return ItemMapperWithBooking.toItemDtoWithBooking(comments, lastBooking, nextBooking, item);
        } else
        return ItemMapperWithBooking.toItemDtoWithBooking(comments, null, null, item);
    }

    @Override
    public List<ItemDtoWithBooking> getItemsByUser(Long userId) {
        return itemRepository.findByOwnerIdOrderByIdAsc(userId)
                .stream()
                .map(item -> {
                    List<Comment> comments = getReviewsByItemId(item);
                    Booking lastBooking =
                            bookingRepository.getFirstByItemIdAndEndBeforeOrderByEndDesc(item.getId(),
                                    LocalDateTime.now());
                    Booking nextBooking =
                            bookingRepository.getTopByItemIdAndStartAfterOrderByStartAsc(item.getId(),
                                    LocalDateTime.now());
                    return ItemMapperWithBooking.toItemDtoWithBooking(comments, lastBooking, nextBooking, item);
                })
                .collect(Collectors.toList());
    }

    @Override
    public List<ItemDto> searchItemByQuery(String text) {
        if (text.isEmpty()) {
            return new ArrayList<>();
        }
        return itemRepository.searchByQuery(text)
                .stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    @Override
    public CommentDto addComment(Long userId, Long itemId, CommentDto commentDto){
        if(commentDto.getText().isEmpty() || commentDto.getText().isBlank()){
            throw new ObjectValidationException("This comment is empty.");
        }
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ObjectNotFoundException("User with id=" + userId + " not found."));
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new ObjectNotFoundException("Item with id=" + itemId + " not found."));

        Comment comment = CommentMapper.toComment(commentDto, item, user);
        List<Booking> booking =
                bookingRepository.getByBookerIdStatePast(comment.getUser().getId(), LocalDateTime.now());
        if(booking.isEmpty()){
            throw new ObjectValidationException("The user has not booked any item.");
        }
        comment.setCreated(LocalDateTime.now());
        return CommentMapper.toCommentDto(commentRepository.save(comment));
    }

    private List<Comment> getReviewsByItemId(Item item) {
        return commentRepository.getByItem_IdOrderByCreatedDesc(item.getId());
    }
}
