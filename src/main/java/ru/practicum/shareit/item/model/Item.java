package ru.practicum.shareit.item.model;

<<<<<<< HEAD
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.booking.model.Booking;
=======

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
>>>>>>> 61d3a36fb68671b2bc56a32d663def57fc07f660
import ru.practicum.shareit.common.Create;
import ru.practicum.shareit.request.ItemRequest;
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
import javax.persistence.OneToOne;
import javax.persistence.OneToMany;
import javax.persistence.CascadeType;
import javax.persistence.FetchType;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.HashSet;
import java.util.Set;
=======
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * TODO Sprint add-controllers.
 */
>>>>>>> 61d3a36fb68671b2bc56a32d663def57fc07f660

@Data
@NoArgsConstructor
@AllArgsConstructor
<<<<<<< HEAD
@Entity
@Table(name = "items")
@Builder
public class Item {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @NotNull(groups = {Create.class})
    @NotBlank(groups = {Create.class})
    @Column(name = "name")
=======
public class Item {

    private Long itemId;

    @NotNull(groups = {Create.class})
    @NotBlank(groups = {Create.class})
>>>>>>> 61d3a36fb68671b2bc56a32d663def57fc07f660
    private String name;

    @NotNull(groups = {Create.class})
    @NotBlank(groups = {Create.class})
<<<<<<< HEAD
    @Column(name = "description")
=======
>>>>>>> 61d3a36fb68671b2bc56a32d663def57fc07f660
    private String description;

    @NotNull(groups = {Create.class})
    @NotBlank(groups = {Create.class})
<<<<<<< HEAD
    @Column(name = "available")
    private Boolean available;

    @ManyToOne
    @JoinColumn(name = "owner_id", nullable = false)
    private User owner;

    @OneToOne
    @JoinColumn(name = "request_id")
    private ItemRequest request;

    @OneToMany(mappedBy = "item",
            orphanRemoval = true,
            cascade = CascadeType.REMOVE,
            fetch = FetchType.LAZY)
    @JsonIgnore
    private Set<Booking> booking = new HashSet<>();
=======
    private Boolean available;

    private User owner;

    private ItemRequest request;
>>>>>>> 61d3a36fb68671b2bc56a32d663def57fc07f660
}
