package ru.yandex.practicum.filmorate.service;

import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.FriendStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.List;

@Service
public class UserService extends ItemService<User> {
    public static final String ITEM_NAME = "пользователь";
    private final UserStorage userStorage;
    private final FriendStorage friendStorage;

    protected UserService(UserStorage userStorage, FriendStorage friendStorage) {
        super(ITEM_NAME, userStorage);
        this.userStorage = userStorage;
        this.friendStorage = friendStorage;
    }

    public void addFriend(long userId, long friendId) {
        checkIfItemNotFound(userId);
        checkIfItemNotFound(friendId);
        friendStorage.add(userId, friendId);
    }

    public void deleteFriend(long userId, long friendId) {
        checkIfItemNotFound(userId);
        checkIfItemNotFound(friendId);
        friendStorage.delete(userId, friendId);
    }

    public List<User> getFriends(long userId) {
        return userStorage.getFriends(userId);
    }

    public List<User> getCommonFriends(long userId, long otherUserId) {
        return userStorage.getCommonFriends(userId, otherUserId);
    }
}
