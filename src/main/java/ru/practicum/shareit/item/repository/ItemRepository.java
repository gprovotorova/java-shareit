package ru.practicum.shareit.item.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.model.Item;

import java.util.Collection;
import java.util.List;

@Repository
public interface ItemRepository extends JpaRepository<Item, Long> {

    Collection<Item> findByOwnerIdOrderByIdAsc(Long userId);

    @Query("select i from Item as i " +
            "where i.available = true " +
            "and (upper(i.name) like upper(concat('%', :query,'%')) " +
            "or upper(i.description) like upper(concat('%',:query,'%')))")

    List<Item> searchByQuery(String query);
}
