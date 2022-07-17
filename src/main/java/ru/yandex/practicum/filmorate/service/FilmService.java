package ru.yandex.practicum.filmorate.service;

import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.ItemStorage;

import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class FilmService extends ItemService<Film> {
    private static final String ITEM_NAME = "фильм";
    private final UserService userService;

    protected FilmService(ItemStorage<Film> itemStorage, UserService userService) {
        super(ITEM_NAME, itemStorage);
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
        Set<Long> friends = film.getLikes();
        friends.remove(userId);
        Film filmToUpdate = film.withLikes(friends);
        update(filmToUpdate);
    }

    public List<Film> getPopularFilms(int count) {
        return itemStorage
                .getAll()
                .stream()
                .map(Film.class::cast)
                .sorted(Comparator.comparingInt((Film film) -> film.getLikes().size()).reversed())
                .limit(count)
                .collect(Collectors.toList());
    }
}
