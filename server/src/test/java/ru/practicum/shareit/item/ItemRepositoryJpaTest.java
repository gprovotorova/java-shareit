package ru.practicum.shareit.item;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;

import java.util.Collection;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
public class ItemRepositoryJpaTest {
    @Autowired
    private TestEntityManager em;
    @Autowired
    private ItemRepository itemRepository;
    private Item item = new Item();
    private User galina = new User();
    private PageRequest page = PageRequest.of(0, 10);

    @BeforeEach
    void saveInfo() {
        galina.setName("Galina");
        galina.setEmail("galina@mail.ru");
        em.persist(galina);

        item.setName("book");
        item.setDescription("very interesting romantic book description");
        item.setAvailable(true);
        item.setOwner(galina);
        item.setRequestId(null);
        em.persist(item);
        em.flush();
    }

    @Test
    void searchByName_shouldReturnItem() {
        Page<Item> resultItems = itemRepository.searchByQuery("book", page);

        assertEquals(resultItems.getTotalElements(), 1);
        Item resultItem = resultItems.getContent().get(0);
        assertEquals(resultItem.getName(), item.getName());
        assertEquals(resultItem.getDescription(), item.getDescription());
        assertEquals(resultItem.getAvailable(), item.getAvailable());
        assertEquals(resultItem.getOwner(), item.getOwner());
    }

    @Test
    void searchByDescription_shouldReturnItem() {
        Page<Item> resultItems = itemRepository.searchByQuery("very", page);

        assertEquals(resultItems.getTotalElements(), 1);
        Item resultItem = resultItems.getContent().get(0);
        assertEquals(resultItem.getName(), item.getName());
        assertEquals(resultItem.getDescription(), item.getDescription());
        assertEquals(resultItem.getAvailable(), item.getAvailable());
        assertEquals(resultItem.getOwner(), item.getOwner());
    }

    @Test
    void searchByQuery_shouldNotReturnItem() {
        Page<Item> resultItems = itemRepository.searchByQuery("text", page);

        assertEquals(resultItems.getTotalElements(), 0);
    }

    @Test
    void findByOwnerIdOrderByIdAsc() {
        Collection<Item> resultItems = itemRepository.findByOwnerIdOrderByIdAsc(galina.getId());

        assertEquals(1, resultItems.size());
    }

    @Test
    void findByOwnerIdOrderByIdAscWithPage() {
        Page<Item> resultItems = itemRepository.findByOwnerIdOrderByIdAsc(galina.getId(), page);

        assertEquals(1, resultItems.getNumberOfElements());
    }

    @AfterEach
    void deleteData() {
        itemRepository.deleteAll();
    }
}
