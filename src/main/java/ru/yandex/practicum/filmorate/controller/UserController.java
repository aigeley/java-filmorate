package ru.yandex.practicum.filmorate.controller;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;

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
}
