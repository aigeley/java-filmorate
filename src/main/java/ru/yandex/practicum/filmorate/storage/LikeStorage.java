package ru.yandex.practicum.filmorate.storage;

public interface LikeStorage {
    void add(long filmId, long userId);

    void delete(long filmId, long userId);
}
