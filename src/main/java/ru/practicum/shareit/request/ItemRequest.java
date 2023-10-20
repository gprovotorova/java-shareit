package ru.practicum.shareit.request;

import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.user.model.User;

<<<<<<< HEAD
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
@Entity
@Table(name = "requests")
public class ItemRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "description", nullable = false)
    private String description;

    @ManyToOne
    @JoinColumn(name = "requestor_id")
    private User requestor;

    @Column(name = "created_date")
=======
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
>>>>>>> 61d3a36fb68671b2bc56a32d663def57fc07f660
    private LocalDateTime created;
}
