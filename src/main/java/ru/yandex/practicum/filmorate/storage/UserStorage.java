package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.User;

import java.util.List;

public interface UserStorage extends ItemStorage<User> {
    List<User> getFriends(long userId);

    List<User> getCommonFriends(long userId, long otherUserId);
}
