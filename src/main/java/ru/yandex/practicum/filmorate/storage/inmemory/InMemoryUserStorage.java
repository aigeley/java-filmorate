package ru.yandex.practicum.filmorate.storage.inmemory;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class InMemoryUserStorage extends InMemoryItemStorage<User> implements UserStorage {
    @Override
    public List<User> getFriends(long userId) {
        return get(userId)
                .getFriends()
                .stream()
                .map(this::get)
                .collect(Collectors.toList());
    }

    @Override
    public List<User> getCommonFriends(long userId, long otherUserId) {
        return get(userId)
                .getFriends()
                .stream()
                .filter(friendId -> get(otherUserId).getFriends().contains(friendId))
                .map(this::get)
                .collect(Collectors.toList());
    }
}
