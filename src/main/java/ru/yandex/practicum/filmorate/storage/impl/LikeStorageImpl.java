package ru.yandex.practicum.filmorate.storage.impl;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.storage.LikeStorage;

@Component
public class LikeStorageImpl implements LikeStorage {
    private final JdbcTemplate jdbcTemplate;

    public LikeStorageImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public void add(long filmId, long userId) {
        String sql = "INSERT INTO likes (film_id, user_id) VALUES (?, ?)";

        jdbcTemplate.update(
                sql,
                filmId,
                userId
        );
    }

    public void delete(long filmId, long userId) {
        String sql = "DELETE FROM likes WHERE film_id = ? AND user_id = ?";

        jdbcTemplate.update(
                sql,
                filmId,
                userId
        );
    }
}
