package ru.practicum.shareit.request;

import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

/**
 * TODO Sprint add-item-requests.
 */

@Data
@NoArgsConstructor
public class ItemRequest {
    private Long requestId;
    private String description;
    private User requestor;
    private LocalDateTime created;
}
