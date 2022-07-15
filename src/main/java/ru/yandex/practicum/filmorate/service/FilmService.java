package ru.yandex.practicum.filmorate.service;

import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.ItemStorage;

@Service
public class FilmService extends ItemService<Film> {
    private static final String ITEM_NAME = "фильм";

    protected FilmService(ItemStorage<Film> itemStorage) {
        super(ITEM_NAME, itemStorage);
    }
}
