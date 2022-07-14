package ru.yandex.practicum.filmorate.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.ItemService;

import static ru.yandex.practicum.filmorate.controller.FilmController.BASE_PATH;

@RestController
@RequestMapping(BASE_PATH)
public class FilmController extends ItemController<Film> {
    public static final String BASE_PATH = "/films";

    protected FilmController(ItemService<Film> itemService) {
        super(itemService);
    }
}
