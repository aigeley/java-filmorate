package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.User;

import java.util.List;
import java.util.stream.Collectors;

public interface UserStorage extends ItemStorage<User> {
    default List<User> getFriends(long userId) {
        return get(userId)
                .getFriends()
                .stream()
                .map(this::get)
                .collect(Collectors.toList());
    }

    default List<User> getCommonFriends(long userId, long otherUserId) {
        return get(userId)
                .getFriends()
                .stream()
                .filter(friendId -> get(otherUserId).getFriends().contains(friendId))
                .map(this::get)
                .collect(Collectors.toList());
    }
}
