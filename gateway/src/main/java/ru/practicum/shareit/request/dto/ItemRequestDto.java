package ru.practicum.shareit.request.dto;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ItemRequestDto {
    private Long id;

    @NotNull(message = "Request description cannot be empty.")
    private String description;
    private LocalDateTime created;
}
