package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import ru.yandex.practicum.filmorate.model.Identifiable;
import ru.yandex.practicum.filmorate.service.exception.ItemAlreadyExistsException;
import ru.yandex.practicum.filmorate.service.exception.ItemNotFoundException;
import ru.yandex.practicum.filmorate.storage.ItemStorage;

import java.util.Collection;

@Slf4j
public abstract class ItemService<T extends Identifiable<T>> {
    protected final String itemName;
    protected final ItemStorage<T> itemStorage;

    protected ItemService(String itemName, ItemStorage<T> itemStorage) {
        this.itemName = itemName;
        this.itemStorage = itemStorage;
    }

    private T getItemWithId(T item) {
        long itemId = item.getId();
        boolean isIdMissing = itemId == 0;
        return isIdMissing ? item.withId(itemStorage.getNextId()) : item; //если id не задано извне, то задаём сами
    }

    public T get(long itemId) {
        if (itemId == 0 || !itemStorage.isIdExists(itemId)) {
            throw new ItemNotFoundException(itemId, itemName);
        }

        return itemStorage.get(itemId);
    }

    public Collection<T> getAll() {
        return itemStorage.getAll();
    }

    public T add(T item) {
        T itemToAdd = getItemWithId(item);
        long itemToAddId = itemToAdd.getId();

        if (itemStorage.isIdExists(itemToAddId)) {
            throw new ItemAlreadyExistsException(itemToAddId, itemName);
        }

        log.info("add: " + itemToAdd);
        return itemStorage.add(itemToAdd);
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
