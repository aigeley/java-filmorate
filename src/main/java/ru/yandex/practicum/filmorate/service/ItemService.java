package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import ru.yandex.practicum.filmorate.controller.exception.ItemAlreadyExistsException;
import ru.yandex.practicum.filmorate.controller.exception.ItemNotFoundException;
import ru.yandex.practicum.filmorate.model.Identifiable;
import ru.yandex.practicum.filmorate.storage.ItemStorage;

import java.util.Collection;

@Slf4j
public abstract class ItemService<T extends Identifiable> {
    protected final String itemName;
    protected final ItemStorage<T> itemStorage;

    protected ItemService(String itemName, ItemStorage<T> itemStorage) {
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

        log.info("add: " + item);
        return itemStorage.add(item);
    }

    public T update(T item) {
        long itemId = item.getId();

        if (itemId == 0 || !itemStorage.isIdExists(itemId)) {
            throw new ItemNotFoundException(itemId, itemName);
        }

        log.info("update: " + item);
        return itemStorage.update(item);
    }

    public void deleteAll() {
        log.info("deleteAll: " + itemName);
        itemStorage.deleteAll();
    }
}
