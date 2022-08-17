package ru.yandex.practicum.filmorate.storage;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.User;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class DbUserStorage implements UserStorage, RowMapper<User> {
    private final JdbcTemplate jdbcTemplate;

    public DbUserStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private Set<Long> getFriendsIds(long userId) {
        String sql = "SELECT friend_id FROM user_friends WHERE user_id = ?";

        return new LinkedHashSet<>(
                jdbcTemplate.query(
                        sql,
                        (rs, rowNum) -> rs.getLong("friend_id"),
                        userId
                )
        );
    }

    private void rewriteFriendsIds(User user) {
        deleteFriendsIds(user);
        insertFriendsIds(user);
    }

    private void deleteFriendsIds(User user) {
        long userId = user.getId();
        String sql = "DELETE FROM user_friends WHERE user_id = ?";
        jdbcTemplate.update(sql, userId);
    }

    private void insertFriendsIds(User user) {
        long userId = user.getId();
        int friendsCount = user.getFriends().size();

        if (friendsCount == 0) {
            return; //у пользователя нет друзей
        }

        String sql = "INSERT INTO user_friends (user_id, friend_id) VALUES "
                + DbUtils.getPlaceHolders(friendsCount);
        List<Long> userFriends = new ArrayList<>();

        for (long friendId : user.getFriends()) {
            userFriends.add(userId);
            userFriends.add(friendId);
        }

        jdbcTemplate.update(
                sql,
                userFriends.toArray()
        );
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

        rewriteFriendsIds(user);
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

        rewriteFriendsIds(user);
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
        return get(userId)
                .getFriends()
                .stream()
                .map(this::get)
                .collect(Collectors.toList());
    }

    @Override
    public List<User> getCommonFriends(long userId, long otherUserId) {
        return get(userId)
                .getFriends()
                .stream()
                .filter(friendId -> get(otherUserId).getFriends().contains(friendId))
                .map(this::get)
                .collect(Collectors.toList());
    }

    @Override
    public User mapRow(ResultSet rs, int rowNum) throws SQLException {
        long userId = rs.getLong("user_id");
        Set<Long> friends = getFriendsIds(userId);

        return new User(
                userId,
                rs.getString("email"),
                rs.getString("login"),
                rs.getString("user_name"),
                rs.getDate("birthday").toLocalDate(),
                friends
        );
    }
}
