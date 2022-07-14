package ru.yandex.practicum.filmorate.storage;

import java.util.Collection;

public interface ItemStorage<T> {
    long getNextId();

    T get(long itemId);

    Collection<T> getAll();

    T add(T item);

    T update(T item);

    void deleteAll();

    boolean isIdExists(long itemId);
}
