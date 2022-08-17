package ru.yandex.practicum.filmorate.service;

import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.service.exception.ItemNotFoundException;
import ru.yandex.practicum.filmorate.storage.DbMpaStorage;

import java.util.Collection;

@Service
public class MpaService {
    protected final String itemName;
    private static final String ITEM_NAME = "рейтинг MPA";
    private final DbMpaStorage mpaStorage;

    protected MpaService(DbMpaStorage mpaStorage) {
        this.itemName = ITEM_NAME;
        this.mpaStorage = mpaStorage;
    }

    protected void checkIfItemNotFound(int mpaId) {
        if (mpaId == 0 || !mpaStorage.isExists(mpaId)) {
            throw new ItemNotFoundException(mpaId, itemName);
        }
    }

    public Mpa get(int mpaId) {
        checkIfItemNotFound(mpaId);
        return mpaStorage.get(mpaId);
    }

    public Collection<Mpa> getAll() {
        return mpaStorage.getAll();
    }
}
