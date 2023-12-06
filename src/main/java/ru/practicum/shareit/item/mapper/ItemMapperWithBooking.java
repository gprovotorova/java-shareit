package ru.practicum.shareit.item.mapper;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.dto.ItemDtoWithBooking;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.comments.model.Comment;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class ItemMapperWithBooking {
    public static ItemDtoWithBooking toItemDtoWithBooking(List<Comment> comments,
                                                          Booking lastBooking,
                                                          Booking nextBooking,
                                                          Item item) {
        List<ItemDtoWithBooking.Comment> itemComments = comments.stream()
                .map(commentTemp -> {
                    ItemDtoWithBooking.Comment comment = new ItemDtoWithBooking.Comment();
                    comment.setId(commentTemp.getId());
                    comment.setText(commentTemp.getText());
                    comment.setAuthorName(commentTemp.getUser().getName());
                    comment.setCreated(commentTemp.getCreated());
                    return comment;
                }).collect(Collectors.toList());

        ItemDtoWithBooking.Booking lastBookingTemp = new ItemDtoWithBooking.Booking();
        if (lastBooking != null) {
            lastBookingTemp.setId(lastBooking.getId());
            lastBookingTemp.setBookerId(lastBooking.getBooker().getId());
        } else {
            lastBookingTemp = null;
        }

        ItemDtoWithBooking.Booking nextBookingTemp = new ItemDtoWithBooking.Booking();
        if (nextBooking != null) {
            nextBookingTemp.setId(nextBooking.getId());
            nextBookingTemp.setBookerId(nextBooking.getBooker().getId());
        } else {
            nextBookingTemp = null;
        }

        return ItemDtoWithBooking.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.getAvailable())
                .lastBooking(lastBookingTemp)
                .nextBooking(nextBookingTemp)
                .comments(itemComments)
                .build();
    }
}
