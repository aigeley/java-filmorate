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

    @Override
    public User add(User user) {
        long userId = user.getId();
        boolean isIdMissing = userId == 0;
        long userIdToAdd = isIdMissing ? itemStorage.getNextId() : userId; //если id не задан извне, то присваиваем сами
        String userName = user.getName();
        boolean isNameMissing = userName == null || userName.isBlank();
        String userNameToAdd = isNameMissing ? user.getLogin() : userName; //если имя пустое, то берём логин
        boolean isUserRebuildNeeded = isIdMissing || isNameMissing;
        User userToAdd;

        if (isUserRebuildNeeded) {
            userToAdd = user
                    .toBuilder()
                    .id(userIdToAdd)
                    .name(userNameToAdd)
                    .build();
        } else {
            userToAdd = user;
        }

        return super.add(userToAdd);
    }

    @Override
    public User update(User user) {
        String userName = user.getName();
        boolean isNameMissing = userName == null || userName.isBlank();
        String userNameToUpdate = isNameMissing ? user.getLogin() : userName; //если имя пустое, то берём логин
        User userToUpdate;

        if (isNameMissing) {
            userToUpdate = user
                    .toBuilder()
                    .name(userNameToUpdate)
                    .build();
        } else {
            userToUpdate = user;
        }

        return super.update(userToUpdate);
    }
}
