package ru.yandex.practicum.filmorate.service;

import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.ItemStorage;

import java.util.Set;

@Service
public class FilmService extends ItemService<Film> {
    private static final String ITEM_NAME = "фильм";

    protected FilmService(ItemStorage<Film> itemStorage) {
        super(ITEM_NAME, itemStorage);
    }

    public void addLike(long filmId, long userId) {
        Film film = get(filmId);
        Set<Long> likes = film.getLikes();
        likes.add(userId);
        Film filmToUpdate = film.withLikes(likes);
        update(filmToUpdate);
    }
}
