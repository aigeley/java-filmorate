package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import ru.yandex.practicum.filmorate.controller.exception.ItemAlreadyExistsException;
import ru.yandex.practicum.filmorate.controller.exception.ItemNotFoundException;
import ru.yandex.practicum.filmorate.model.Identifiable;
import ru.yandex.practicum.filmorate.storage.ItemStorage;

import java.util.Collection;

@Slf4j
public abstract class ItemController<T extends Identifiable> {
    protected final String path;
    protected final String itemName;
    protected final ItemStorage<T> itemStorage;

    protected ItemController(String path, String itemName, ItemStorage<T> itemStorage) {
        this.path = path;
        this.itemName = itemName;
        this.itemStorage = itemStorage;
    }

    public Collection<T> getAll() {
        return itemStorage.getAll();
    }

    public T add(T item) {
        long itemId = item.getId();

        if (itemStorage.isIdExists(itemId)) {
            throw new ItemAlreadyExistsException(itemId, itemName);
        }

        log.info("POST: " + item);
        return itemStorage.add(item);
    }

    public T update(T item) {
        long itemId = item.getId();

        if (itemId == 0 || !itemStorage.isIdExists(itemId)) {
            throw new ItemNotFoundException(itemId, itemName);
        }

        log.info("PUT: " + item);
        return itemStorage.update(item);
    }

    public void deleteAll() {
        log.info("DELETE: " + path);
        itemStorage.deleteAll();
    }
}
