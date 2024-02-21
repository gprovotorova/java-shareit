package ru.practicum.shareit.comments;

import java.time.LocalDateTime;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;

@Getter
@Setter
public class CommentDto {
    private Long id;
    @NotBlank
    private String text;
    private String authorName;
    private LocalDateTime created;

    public CommentDto(Long id, String text, String authorName, LocalDateTime created) {
        this.id = id;
        this.text = text;
        this.authorName = authorName;
        this.created = created;
        if (created == null) {
            this.created = LocalDateTime.now();
        } else {
            this.created = created;
        }
    }
}
