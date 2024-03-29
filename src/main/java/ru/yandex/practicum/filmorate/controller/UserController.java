package ru.yandex.practicum.filmorate.controller;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;

import java.util.List;

import static ru.yandex.practicum.filmorate.controller.UserController.BASE_PATH;

@RestController
@RequestMapping(BASE_PATH)
public class UserController extends ItemController<User> {
    public static final String BASE_PATH = "/users";
    private final UserService userService;

    protected UserController(UserService userService) {
        super(userService);
        this.userService = userService;
    }

    @PutMapping("/{id}/friends/{friendId}")
    public void addFriend(@PathVariable("id") long userId, @PathVariable long friendId) {
        userService.addFriend(userId, friendId);
    }

    @DeleteMapping("/{id}/friends/{friendId}")
    public void deleteFriend(@PathVariable("id") long userId, @PathVariable long friendId) {
        userService.deleteFriend(userId, friendId);
    }

    @GetMapping(value = "/{id}/friends", produces = "application/json;charset=UTF-8")
    public List<User> getFriends(@PathVariable("id") long userId) {
        return userService.getFriends(userId);
    }

    @GetMapping(value = "/{id}/friends/common/{otherId}", produces = "application/json;charset=UTF-8")
    public List<User> getCommonFriends(@PathVariable("id") long userId, @PathVariable("otherId") long otherUserId) {
        return userService.getCommonFriends(userId, otherUserId);
    }
}
