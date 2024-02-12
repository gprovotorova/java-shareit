package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import ru.practicum.shareit.common.Create;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Getter
@Setter
@AllArgsConstructor
public class ItemShortDto {
    private Long id;

    @NotNull(groups = {Create.class})
    @NotBlank(groups = {Create.class})
    private String name;

    @NotNull(groups = {Create.class})
    @NotBlank(groups = {Create.class})
    private String description;

    @NotNull(groups = {Create.class})
    private Boolean available;

    private Long ownerId;

    private Long requestId;
}
