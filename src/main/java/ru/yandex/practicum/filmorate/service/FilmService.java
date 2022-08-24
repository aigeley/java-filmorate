package ru.yandex.practicum.filmorate.service;

import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.LikeStorage;

import java.util.List;

@Service
public class FilmService extends ItemService<Film> {
    private static final String ITEM_NAME = "фильм";
    private final FilmStorage filmStorage;
    private final LikeStorage likeStorage;
    private final UserService userService;

    protected FilmService(FilmStorage filmStorage, LikeStorage likeStorage, UserService userService) {
        super(ITEM_NAME, filmStorage);
        this.filmStorage = filmStorage;
        this.likeStorage = likeStorage;
        this.userService = userService;
    }

    public void addLike(long filmId, long userId) {
        this.checkIfItemNotFound(filmId);
        userService.checkIfItemNotFound(userId);
        likeStorage.add(filmId, userId);
    }

    public void deleteLike(long filmId, long userId) {
        this.checkIfItemNotFound(filmId);
        userService.checkIfItemNotFound(userId);
        likeStorage.delete(filmId, userId);
    }

    public List<Film> getPopularFilms(int count) {
        return filmStorage.getPopularFilms(count);
    }
}
