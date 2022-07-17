package ru.yandex.practicum.filmorate.controller;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;

import static ru.yandex.practicum.filmorate.controller.FilmController.BASE_PATH;

@RestController
@RequestMapping(BASE_PATH)
public class FilmController extends ItemController<Film> {
    public static final String BASE_PATH = "/films";
    private final FilmService filmService;

    protected FilmController(FilmService filmService) {
        super(filmService);
        this.filmService = filmService;
    }

    @PutMapping("/{id}/like/{userId}")
    public void addLike(@PathVariable("id") long filmId, @PathVariable long userId) {
        filmService.addLike(filmId, userId);
    }

    @DeleteMapping("/{id}/like/{userId}")
    public void deleteLike(@PathVariable("id") long filmId, @PathVariable long userId) {
        filmService.deleteLike(filmId, userId);
    }
}
