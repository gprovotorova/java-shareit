package ru.practicum.shareit.common;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import ru.practicum.shareit.exception.InvalidPathVariableException;

public class PageMaker {
    public static Pageable makePageableWithSort(Integer from, Integer size) {
        if (from == null || size == null) {
            return Pageable.unpaged();
        } else if (from < 0 || size <= 0) {
            throw new InvalidPathVariableException("Incorrect page parameters");
        }
        return PageRequest.of(from / size, size, Sort.Direction.ASC, "id");
    }
}
