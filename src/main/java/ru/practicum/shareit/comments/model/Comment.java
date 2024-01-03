package ru.practicum.shareit.comments.model;

import lombok.*;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Id;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Column;
import javax.persistence.ManyToOne;
import javax.persistence.JoinColumn;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "comments")
@Builder
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "text")
    private String text;

    @ManyToOne
    @ToString.Exclude
    @JoinColumn(name = "item_id", nullable = false)
    private Item item;

    @ManyToOne
    @ToString.Exclude
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "created_date")
    private LocalDateTime created;
}
