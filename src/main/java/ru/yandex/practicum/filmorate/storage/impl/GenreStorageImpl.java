package ru.yandex.practicum.filmorate.storage.impl;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Genre;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;

@Component
public class GenreStorageImpl implements RowMapper<Genre> {
    private final JdbcTemplate jdbcTemplate;

    public GenreStorageImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Genre get(int genreId) {
        String sql = "SELECT genre_id, genre_name FROM genres WHERE genre_id = ?";
        return jdbcTemplate.queryForObject(sql, this, genreId);
    }

    public Collection<Genre> getAll() {
        String sql = "SELECT genre_id, genre_name FROM genres ORDER BY genre_id";
        return jdbcTemplate.query(sql, this);
    }

    public boolean isExists(int genreId) {
        String sql = "SELECT COUNT(*) cnt FROM genres WHERE genre_id = ?";

        Integer cnt = jdbcTemplate.queryForObject(
                sql,
                (rs, rowNum) -> rs.getInt("cnt"),
                genreId
        );

        return (cnt == null ? 0 : cnt) > 0;
    }

    @Override
    public Genre mapRow(ResultSet rs, int rowNum) throws SQLException {
        return new Genre(
                rs.getInt("genre_id"),
                rs.getString("genre_name")
        );
    }
}
