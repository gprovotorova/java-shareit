package ru.practicum.shareit.item.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.model.Item;

import java.util.Collection;

@Repository
public interface ItemRepository extends JpaRepository<Item, Long> {

    Collection<Item> findByOwnerIdOrderByIdAsc(Long userId);

    @Query("select i from Item i " +
            "where lower(i.name) like lower (concat('%', :query, '%')) " +
            "or lower(i.description) like lower (concat('%', :query, '%')) " +
            "and i.available = true")
    Collection<Item> searchByQuery(@Param("query") String query);
}
