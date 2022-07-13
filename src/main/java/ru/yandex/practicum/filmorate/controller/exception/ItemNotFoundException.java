package ru.yandex.practicum.filmorate.controller.exception;

public class ItemNotFoundException extends RuntimeException {
    public ItemNotFoundException(long itemId, String itemName) {
        super(String.format("%s с id = %d не существует", itemName, itemId));
    }
}
