package ru.yandex.practicum.filmorate.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.service.GenreService;

import java.util.Collection;

import static ru.yandex.practicum.filmorate.controller.GenreController.BASE_PATH;

@RestController
@RequestMapping(BASE_PATH)
public class GenreController {
    public static final String BASE_PATH = "/genres";
    private final GenreService genreService;

    protected GenreController(GenreService genreService) {
        this.genreService = genreService;
    }

    @GetMapping(value = "/{id}", produces = "application/json;charset=UTF-8")
    public Genre get(@PathVariable("id") int genreId) {
        return genreService.get(genreId);
    }

    @GetMapping(produces = "application/json;charset=UTF-8")
    public Collection<Genre> getAll() {
        return genreService.getAll();
    }
}
