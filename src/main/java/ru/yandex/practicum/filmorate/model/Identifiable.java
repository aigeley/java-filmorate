package ru.yandex.practicum.filmorate.model;

public interface Identifiable<T> {
    long getId();

    T withId(long itemId);
}
