package ru.yandex.practicum.filmorate.controller;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.model.User;

import javax.validation.Valid;
import java.util.Collection;

@RestController
public class UserController extends ItemController<User> {
    public static final String BASE_PATH = "/users";
    public static final String ITEM_NAME = "пользователь";

    protected UserController() {
        super(BASE_PATH, ITEM_NAME);
    }

    @Override
    @GetMapping(BASE_PATH)
    public Collection<User> getAll() {
        return super.getAll();
    }

    @PostMapping(BASE_PATH)
    public User put(@Valid @RequestBody User user) {
        int userId = user.getId();
        boolean isIdMissing = userId == 0;
        int userIdToAdd = isIdMissing ? getNextId() : userId; //если id не присвоен извне, то присваиваем сами
        String userName = user.getName();
        boolean isNameMissing = userName == null || userName.isBlank();
        String userNameToAdd = isNameMissing ? user.getLogin() : userName; //если имя пустое, то берём логин
        boolean isUserRebuildNeeded = isIdMissing || isNameMissing;
        User userToAdd;

        if (isUserRebuildNeeded) {
            userToAdd = user.toBuilder().id(userIdToAdd).name(userNameToAdd).build();
        } else {
            userToAdd = user;
        }

        return super.add(userIdToAdd, userToAdd);
    }

    @PutMapping(BASE_PATH)
    public User update(@Valid @RequestBody User user) {
        String userName = user.getName();
        boolean isNameMissing = userName == null || userName.isBlank();
        String userNameToUpdate = isNameMissing ? user.getLogin() : userName; //если имя пустое, то берём логин
        User userToUpdate;

        if (isNameMissing) {
            userToUpdate = user.toBuilder().name(userNameToUpdate).build();
        } else {
            userToUpdate = user;
        }

        return super.update(user.getId(), userToUpdate);
    }

    @Override
    @DeleteMapping(BASE_PATH)
    public void deleteAll() {
        super.deleteAll();
    }
}
