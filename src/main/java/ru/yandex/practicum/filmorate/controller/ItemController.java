package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import ru.yandex.practicum.filmorate.controller.exception.ItemAlreadyExistsException;
import ru.yandex.practicum.filmorate.controller.exception.ItemNotFoundException;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
public abstract class ItemController<T> {
    protected final String path;
    protected final String itemName;
    private final Map<Integer, T> items;
    private AtomicInteger lastId;

    protected ItemController(String path, String itemName) {
        this.path = path;
        this.itemName = itemName;
        this.items = new HashMap<>();
        this.lastId = new AtomicInteger(0);
    }

    protected int getNextId() {
        return lastId.incrementAndGet();
    }

    protected Collection<T> getAll() {
        return items.values();
    }

    protected T add(int itemId, T item) {
        if (items.containsKey(itemId)) {
            throw new ItemAlreadyExistsException(itemId, itemName);
        }

        items.put(itemId, item);
        log.info("POST: " + item);
        return item;
    }

    protected T update(int itemId, T item) {
        if (itemId == 0 || !items.containsKey(itemId)) {
            throw new ItemNotFoundException(itemId, itemName);
        }

        items.put(itemId, item);
        log.info("PUT: " + item);
        return item;
    }

    protected void deleteAll() {
        items.clear();
        log.info("DELETE: " + path);
    }
}
