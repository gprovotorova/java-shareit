package ru.practicum.shareit.item.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.model.Item;

import java.util.Collection;
import java.util.List;

@Repository
public interface ItemRepository extends JpaRepository<Item, Long> {

    Collection<Item> findByOwnerIdOrderByIdAsc(Long userId);

    @Query("select i from Item i " +
            "where i.available = true " +
            "and (lower(i.name) like concat('%', lower(:query),'%') " +
            "or lower(i.description) like concat('%', lower(:query),'%'))")
    List<Item> searchByQuery(String query);

    //List<Item> searchByQuery(@Param("query") String query);
}
