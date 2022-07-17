package ru.yandex.practicum.filmorate.service;

import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.ItemStorage;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class UserService extends ItemService<User> {
    public static final String ITEM_NAME = "пользователь";

    protected UserService(ItemStorage<User> itemStorage) {
        super(ITEM_NAME, itemStorage);
    }

    private User getUserWithName(User user) {
        String userName = user.getName();
        boolean isNameMissing = userName == null || userName.isBlank();
        return isNameMissing ? user.withName(user.getLogin()) : user;
    }

    @Override
    public User add(User user) {
        User userToAdd = getUserWithName(user);
        return super.add(userToAdd);
    }

    @Override
    public User update(User user) {
        User userToUpdate = getUserWithName(user);
        return super.update(userToUpdate);
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
        addFriendToUser(friendId, userId);
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
        deleteFriendFromUser(friendId, userId);
    }

    public List<User> getFriends(long userId) {
        return get(userId)
                .getFriends()
                .stream()
                .map(this::get)
                .collect(Collectors.toList());
    }

    public List<User> getCommonFriends(long userId, long otherUserId) {
        return get(userId)
                .getFriends()
                .stream()
                .filter(friendId -> get(otherUserId).getFriends().contains(friendId))
                .map(this::get)
                .collect(Collectors.toList());
    }
}
