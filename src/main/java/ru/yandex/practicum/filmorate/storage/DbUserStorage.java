package ru.yandex.practicum.filmorate.storage;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.User;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.List;

@Component
public class DbUserStorage implements UserStorage, RowMapper<User> {
    private final JdbcTemplate jdbcTemplate;

    public DbUserStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public long getNextId() {
        String sql = "SELECT NEXT VALUE FOR users_seq nextval";

        Long nextId = jdbcTemplate.queryForObject(
                sql,
                (rs, rowNum) -> rs.getLong("nextval")
        );

        return nextId == null ? 0 : nextId;
    }

    @Override
    public User get(long userId) {
        String sql = "SELECT user_id, email, login, user_name, birthday FROM users " +
                "WHERE user_id = ?";
        return jdbcTemplate.queryForObject(sql, this, userId);
    }

    @Override
    public Collection<User> getAll() {
        String sql = "SELECT user_id, email, login, user_name, birthday FROM users";
        return jdbcTemplate.query(sql, this);
    }

    @Override
    public User add(User user) {
        String sql = "INSERT INTO users (user_id, email, login, user_name, birthday) " +
                "VALUES (?, ?, ?, ?, ?)";

        jdbcTemplate.update(
                sql,
                user.getId(),
                user.getEmail(),
                user.getLogin(),
                user.getName(),
                user.getBirthday()
        );

        return user;
    }

    @Override
    public User update(User user) {
        String sql = "UPDATE users SET email = ?, login = ?, user_name = ?, birthday = ? " +
                "WHERE user_id = ?";

        jdbcTemplate.update(
                sql,
                user.getEmail(),
                user.getLogin(),
                user.getName(),
                user.getBirthday(),
                user.getId()
        );

        return user;
    }

    @Override
    public void deleteAll() {
        String sql = "DELETE FROM users";
        jdbcTemplate.update(sql);
    }

    @Override
    public boolean isExists(long userId) {
        String sql = "SELECT COUNT(*) cnt FROM users WHERE user_id = ?";

        Integer cnt = jdbcTemplate.queryForObject(
                sql,
                (rs, rowNum) -> rs.getInt("cnt"),
                userId
        );

        return (cnt == null ? 0 : cnt) > 0;
    }

    @Override
    public List<User> getFriends(long userId) {
        String sql = "SELECT u.user_id, u.email, u.login, u.user_name, u.birthday FROM users u " +
                "JOIN user_friends uf ON u.user_id = uf.friend_id AND uf.user_id = ?";
        return jdbcTemplate.query(sql, this, userId);
    }

    @Override
    public List<User> getCommonFriends(long userId, long otherUserId) {
        String sql = "SELECT u.user_id, u.email, u.login, u.user_name, u.birthday FROM users u " +
                "JOIN user_friends uf ON u.user_id = uf.friend_id AND uf.user_id = ? " +
                "INTERSECT SELECT u.user_id, u.email, u.login, u.user_name, u.birthday FROM users u " +
                "JOIN user_friends uf ON u.user_id = uf.friend_id AND uf.user_id = ?";
        return jdbcTemplate.query(sql, this, userId, otherUserId);
    }

    @Override
    public User mapRow(ResultSet rs, int rowNum) throws SQLException {
        return new User(
                rs.getLong("user_id"),
                rs.getString("email"),
                rs.getString("login"),
                rs.getString("user_name"),
                rs.getDate("birthday").toLocalDate()
        );
    }
}
