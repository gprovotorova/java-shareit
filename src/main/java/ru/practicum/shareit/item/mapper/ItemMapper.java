package ru.practicum.shareit.item.mapper;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
<<<<<<< HEAD
import ru.practicum.shareit.user.model.User;
=======
>>>>>>> 61d3a36fb68671b2bc56a32d663def57fc07f660

@Component
public class ItemMapper {
    public static ItemDto toItemDto(Item item) {
<<<<<<< HEAD
        return new ItemDto(item.getId(),
=======
        return new ItemDto(item.getItemId(),
>>>>>>> 61d3a36fb68671b2bc56a32d663def57fc07f660
                item.getName(),
                item.getDescription(),
                item.getAvailable(),
                item.getOwner(),
                item.getRequest()
        );
    }

<<<<<<< HEAD
    public static Item toItem(ItemDto itemDto, User user) {
        return Item.builder()
                .id(itemDto.getId())
                .name(itemDto.getName())
                .description(itemDto.getDescription())
                .available(itemDto.getAvailable())
                .owner(user)
                .build();
=======
    public static Item toItem(ItemDto itemDto) {
        return new Item(itemDto.getId(),
                itemDto.getName(),
                itemDto.getDescription(),
                itemDto.getAvailable(),
                itemDto.getOwner(),
                itemDto.getRequest()
        );
>>>>>>> 61d3a36fb68671b2bc56a32d663def57fc07f660
    }
}
