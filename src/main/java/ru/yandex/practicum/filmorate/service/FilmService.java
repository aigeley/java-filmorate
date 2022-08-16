package ru.yandex.practicum.filmorate.service;

import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.util.List;
import java.util.Set;

@Service
public class FilmService extends ItemService<Film> {
    private static final String ITEM_NAME = "фильм";
    private final FilmStorage filmStorage;
    private final UserService userService;

    protected FilmService(FilmStorage filmStorage, UserService userService) {
        super(ITEM_NAME, filmStorage);
        this.filmStorage = filmStorage;
        this.userService = userService;
    }

    public void addLike(long filmId, long userId) {
        Film film = get(filmId);
        userService.checkIfItemNotFound(userId);
        Set<Long> likes = film.getLikes();
        likes.add(userId);
        Film filmToUpdate = film.withLikes(likes);
        update(filmToUpdate);
    }

    public void deleteLike(long filmId, long userId) {
        Film film = get(filmId);
        userService.checkIfItemNotFound(userId);
        Set<Long> likes = film.getLikes();
        likes.remove(userId);
        Film filmToUpdate = film.withLikes(likes);
        update(filmToUpdate);
    }

    public List<Film> getPopularFilms(int count) {
        return filmStorage.getPopularFilms(count);
    }
}
