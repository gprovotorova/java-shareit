package ru.practicum.shareit.item.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.model.Item;

import java.util.Optional;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class ItemRepository {
    private final Map<Long, Item> items = new HashMap<>();
    private Long id = 0L;

    public void save(Item item) {
        item.setItemId(++id);
        items.put(item.getItemId(), item);
    }

    public void update(Item item) {
        items.put(item.getItemId(), item);
    }

    public List<Item> getAllItemsByUser(long userId) {
        return items.values().stream()
                .filter(item -> item
                        .getOwner().getUserId().equals(userId))
                .collect(Collectors.toList());
    }

    public Optional<Item> findById(Long itemId) {
        return Optional.ofNullable(items.get(itemId));
    }

    public List<Item> search(String text) {
        return items.values().stream()
                .filter(Item::getAvailable)
                .filter(item -> (item.getName().toLowerCase().contains(text.toLowerCase())
                        || item.getDescription().toLowerCase().contains(text.toLowerCase())))
                .collect(Collectors.toList());
    }

    public List<Item> getAll() {
        return new ArrayList<>(items.values());
    }
}
