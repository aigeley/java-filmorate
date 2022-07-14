package ru.yandex.practicum.filmorate.service;

import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.ItemStorage;

@Service
public class FilmService extends ItemService<Film> {
    public static final String ITEM_NAME = "фильм";

    protected FilmService(ItemStorage<Film> itemStorage) {
        super(ITEM_NAME, itemStorage);
    }

    @Override
    public Film add(Film film) {
        long filmId = film.getId();
        boolean isIdMissing = filmId == 0;
        long filmIdToAdd = isIdMissing ? itemStorage.getNextId() : filmId; //если id не задан извне, то присваиваем сами
        Film filmToAdd;

        if (isIdMissing) {
            filmToAdd = film
                    .toBuilder()
                    .id(filmIdToAdd)
                    .build();
        } else {
            filmToAdd = film;
        }

        return super.add(filmToAdd);
    }
}
