package ru.yandex.practicum.filmorate.service;

import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.List;
import java.util.Set;

@Service
public class UserService extends ItemService<User> {
    public static final String ITEM_NAME = "пользователь";
    private final UserStorage userStorage;

    protected UserService(UserStorage userStorage) {
        super(ITEM_NAME, userStorage);
        this.userStorage = userStorage;
    }

    protected void addFriendToUser(long userId, long friendId) {
        User user = get(userId);
        checkIfItemNotFound(friendId);
        Set<Long> friends = user.getFriends();
        friends.add(friendId);
        User userToUpdate = user.withFriends(friends);
        update(userToUpdate);
    }

    public void addFriend(long userId, long friendId) {
        addFriendToUser(userId, friendId);
    }

    protected void deleteFriendFromUser(long userId, long friendId) {
        User user = get(userId);
        Set<Long> friends = user.getFriends();
        friends.remove(friendId);
        User userToUpdate = user.withFriends(friends);
        update(userToUpdate);
    }

    public void deleteFriend(long userId, long friendId) {
        deleteFriendFromUser(userId, friendId);
    }

    public List<User> getFriends(long userId) {
        return userStorage.getFriends(userId);
    }

    public List<User> getCommonFriends(long userId, long otherUserId) {
        return userStorage.getCommonFriends(userId, otherUserId);
    }
}
