package ru.yandex.practicum.filmorate.storage;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
public class DbFriendStorage implements FriendStorage {
    private final JdbcTemplate jdbcTemplate;

    public DbFriendStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public void add(long userId, long friendId) {
        String sql = "INSERT INTO user_friends (user_id, friend_id) VALUES (?, ?)";

        jdbcTemplate.update(
                sql,
                userId,
                friendId
        );
    }

    public void delete(long userId, long friendId) {
        String sql = "DELETE FROM user_friends WHERE user_id = ? AND friend_id = ?";

        jdbcTemplate.update(
                sql,
                userId,
                friendId
        );
    }
}
