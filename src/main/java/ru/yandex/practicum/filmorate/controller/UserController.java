package ru.yandex.practicum.filmorate.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.ItemService;

import static ru.yandex.practicum.filmorate.controller.UserController.BASE_PATH;

@RestController
@RequestMapping(BASE_PATH)
public class UserController extends ItemController<User> {
    public static final String BASE_PATH = "/users";

    protected UserController(ItemService<User> itemService) {
        super(itemService);
    }
}
