package ru.yandex.practicum.filmorate.service;

import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.ItemStorage;

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
}
