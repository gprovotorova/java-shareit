package ru.practicum.shareit.item.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.item.model.Item;

import java.util.Collection;
import java.util.List;

public interface ItemRepository extends JpaRepository<Item, Long> {

    Collection<Item> findByOwnerIdOrderByIdAsc(Long userId);

    Page<Item> findByOwnerIdOrderByIdAsc(Long userId, Pageable page);

    @Query("select i from Item as i " +
            "where i.available = true " +
            "and (upper(i.name) like upper(concat('%', :query,'%')) " +
            "or upper(i.description) like upper(concat('%',:query,'%')))")
    List<Item> searchByQuery(String query);

    @Query("select i from Item as i " +
            "where i.available = true " +
            "and (upper(i.name) like upper(concat('%', :query,'%')) " +
            "or upper(i.description) like upper(concat('%',:query,'%')))")
    Page<Item> searchByQuery(String query, Pageable page);

    List<Item> findByRequestId(Long id);
}
