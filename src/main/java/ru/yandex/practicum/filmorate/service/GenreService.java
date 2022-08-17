package ru.yandex.practicum.filmorate.service;

import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.service.exception.ItemNotFoundException;
import ru.yandex.practicum.filmorate.storage.DbGenreStorage;

import java.util.Collection;

@Service
public class GenreService {
    protected final String itemName;
    private static final String ITEM_NAME = "жанр";
    private final DbGenreStorage genreStorage;

    protected GenreService(DbGenreStorage genreStorage) {
        this.itemName = ITEM_NAME;
        this.genreStorage = genreStorage;
    }

    protected void checkIfItemNotFound(int genreId) {
        if (genreId == 0 || !genreStorage.isExists(genreId)) {
            throw new ItemNotFoundException(genreId, itemName);
        }
    }

    public Genre get(int genreId) {
        checkIfItemNotFound(genreId);
        return genreStorage.get(genreId);
    }

    public Collection<Genre> getAll() {
        return genreStorage.getAll();
    }
}
