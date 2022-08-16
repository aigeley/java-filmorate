package ru.yandex.practicum.filmorate.storage.inmemory;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class InMemoryFilmStorage extends InMemoryItemStorage<Film> implements FilmStorage {
    @Override
    public List<Film> getPopularFilms(int count) {
        return getAll()
                .stream()
                .map(Film.class::cast)
                .sorted(Comparator.comparingInt((Film film) -> film.getLikes().size()).reversed())
                .limit(count)
                .collect(Collectors.toList());
    }
}
