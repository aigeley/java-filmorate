package ru.yandex.practicum.filmorate.controller;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.model.Film;

import javax.validation.Valid;
import java.util.Collection;

@RestController
public class FilmController extends ItemController<Film> {
    public static final String BASE_PATH = "/films";
    public static final String ITEM_NAME = "фильм";

    protected FilmController() {
        super(BASE_PATH, ITEM_NAME);
    }

    @Override
    @GetMapping(BASE_PATH)
    public Collection<Film> getAll() {
        return super.getAll();
    }

    @PostMapping(BASE_PATH)
    public Film add(@Valid @RequestBody Film film) {
        int filmId = film.getId();
        boolean isIdMissing = filmId == 0;
        int filmIdToAdd = isIdMissing ? getNextId() : filmId; //если id не присвоен извне, то присваиваем сами
        Film filmToAdd;

        if (isIdMissing) {
            filmToAdd = film.toBuilder().id(filmIdToAdd).build();
        } else {
            filmToAdd = film;
        }

        return super.add(filmIdToAdd, filmToAdd);
    }

    @PutMapping(BASE_PATH)
    public Film update(@Valid @RequestBody Film film) {
        return super.update(film.getId(), film);
    }

    @Override
    @DeleteMapping(BASE_PATH)
    public void deleteAll() {
        super.deleteAll();
    }
}
