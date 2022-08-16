package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public interface FilmStorage extends ItemStorage<Film> {
    default List<Film> getPopularFilms(int count) {
        return getAll()
                .stream()
                .map(Film.class::cast)
                .sorted(Comparator.comparingInt((Film film) -> film.getLikes().size()).reversed())
                .limit(count)
                .collect(Collectors.toList());
    }
}
