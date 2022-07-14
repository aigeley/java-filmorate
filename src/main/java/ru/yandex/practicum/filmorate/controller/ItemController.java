package ru.yandex.practicum.filmorate.controller;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import ru.yandex.practicum.filmorate.model.Identifiable;
import ru.yandex.practicum.filmorate.service.ItemService;

import javax.validation.Valid;
import java.util.Collection;

public abstract class ItemController<T extends Identifiable> {
    ItemService<T> itemService;

    protected ItemController(ItemService<T> itemService) {
        this.itemService = itemService;
    }

    @GetMapping("/{id}")
    public T get(@PathVariable("id") long itemId) {
        return itemService.get(itemId);
    }

    @GetMapping
    public Collection<T> getAll() {
        return itemService.getAll();
    }

    @PostMapping
    public T add(@Valid @RequestBody T item) {
        return itemService.add(item);
    }

    @PutMapping
    public T update(@Valid @RequestBody T item) {
        return itemService.update(item);
    }

    @DeleteMapping
    public void deleteAll() {
        itemService.deleteAll();
    }
}
