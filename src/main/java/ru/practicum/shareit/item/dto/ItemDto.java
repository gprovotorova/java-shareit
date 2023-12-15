package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.common.Create;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.user.model.User;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ItemDto {
    private Long id;

    @NotNull(groups = {Create.class})
    @NotBlank(groups = {Create.class})
    private String name;

    @NotNull(groups = {Create.class})
    @NotBlank(groups = {Create.class})
    private String description;

    @NotNull(groups = {Create.class})
    @NotBlank(groups = {Create.class})
    private Boolean available;

    private User owner;
    private ItemRequest request;
}
