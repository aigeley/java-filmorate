package ru.yandex.practicum.filmorate.storage;

import java.util.Collection;

public interface ItemStorage<T> {
    long getNextId();

    T get(long itemId);

    Collection<T> getAll();

    T add(T item);

    T update(T item);

    void deleteAll();

    boolean isExists(long itemId);

    default String getPlaceHolders(int count) {
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < count; i++) {
            sb.append("(?, ?)");

            if (i == count - 1) {
                break; //не ставим запятую вконце
            }

            sb.append(", ");
        }

        return sb.toString();
    }
}
