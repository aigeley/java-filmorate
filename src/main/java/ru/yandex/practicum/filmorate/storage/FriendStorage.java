package ru.yandex.practicum.filmorate.storage;

public interface FriendStorage {
    void add(long userId, long friendId);

    void delete(long userId, long friendId);
}
