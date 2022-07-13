package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Identifiable;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

public abstract class InMemoryItemStorage<T extends Identifiable> implements ItemStorage<T> {
    private final Map<Long, T> items;
    private final AtomicLong lastId;

    protected InMemoryItemStorage() {
        this.items = new HashMap<>();
        this.lastId = new AtomicLong(0);
    }

    @Override
    public long getNextId() {
        return lastId.incrementAndGet();
    }

    @Override
    public Collection<T> getAll() {
        return items.values();
    }

    @Override
    public T add(T item) {
        items.put(item.getId(), item);
        return item;
    }

    @Override
    public T update(T item) {
        return this.add(item);
    }

    @Override
    public void deleteAll() {
        items.clear();
    }

    @Override
    public boolean isIdExists(long itemId) {
        return items.containsKey(itemId);
    }
}
