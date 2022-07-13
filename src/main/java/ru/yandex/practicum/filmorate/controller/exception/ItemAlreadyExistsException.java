package ru.yandex.practicum.filmorate.controller.exception;

public class ItemAlreadyExistsException extends RuntimeException {
    public ItemAlreadyExistsException(long itemId, String itemName) {
        super(String.format("%s с id = %d уже существует", itemName, itemId));
    }
}
