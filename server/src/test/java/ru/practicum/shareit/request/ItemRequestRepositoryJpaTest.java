package ru.practicum.shareit.request;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
public class ItemRequestRepositoryJpaTest {
    @Autowired
    private TestEntityManager em;
    @Autowired
    private ItemRequestRepository itemRequestRepository;
    private User galina = new User();
    private ItemRequest itemRequest = new ItemRequest();

    private static final LocalDateTime DATE = LocalDateTime.now();
    private PageRequest page = PageRequest.of(0, 10);


    @BeforeEach
    void saveInfo() {
        galina.setName("Galina");
        galina.setEmail("galina@mail.ru");
        em.persist(galina);

        itemRequest.setDescription("Request 1");
        itemRequest.setRequester(galina);
        itemRequest.setCreated(DATE);
        em.persist(itemRequest);
        em.flush();
    }

    @Test
    void findByRequesterIdNot_shouldNotFound() {
        Page<ItemRequest> resultRequests = itemRequestRepository.findByRequesterIdNot(galina.getId(), page);
        assertEquals(0, resultRequests.getNumberOfElements());
    }

    @Test
    void findByRequesterIdNot_shouldFound() {
        User anna = new User();
        anna.setName("Anna");
        anna.setEmail("anna@mail.ru");
        em.persist(anna);

        ItemRequest itemRequest = new ItemRequest();
        itemRequest.setDescription("Request 1");
        itemRequest.setRequester(anna);
        itemRequest.setCreated(DATE);
        em.persist(itemRequest);
        em.flush();

        Page<ItemRequest> resultRequests = itemRequestRepository.findByRequesterIdNot(anna.getId(), page);
        assertEquals(1, resultRequests.getNumberOfElements());
    }
}