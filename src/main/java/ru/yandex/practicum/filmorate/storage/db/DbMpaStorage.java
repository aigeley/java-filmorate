package ru.yandex.practicum.filmorate.storage.db;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;

@Component
public class DbMpaStorage implements RowMapper<Mpa> {
    private final JdbcTemplate jdbcTemplate;

    public DbMpaStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Mpa get(int mpaId) {
        String sql = "SELECT mpa_id, mpa_name FROM mpa WHERE mpa_id = ?";
        return jdbcTemplate.queryForObject(sql, this, mpaId);
    }

    public Collection<Mpa> getAll() {
        String sql = "SELECT mpa_id, mpa_name FROM mpa ORDER BY mpa_id";
        return jdbcTemplate.query(sql, this);
    }

    public boolean isExists(int mpaId) {
        String sql = "SELECT COUNT(*) cnt FROM mpa WHERE mpa_id = ?";

        Integer cnt = jdbcTemplate.queryForObject(
                sql,
                (rs, rowNum) -> rs.getInt("cnt"),
                mpaId
        );

        return (cnt == null ? 0 : cnt) > 0;
    }

    @Override
    public Mpa mapRow(ResultSet rs, int rowNum) throws SQLException {
        return new Mpa(
                rs.getInt("mpa_id"),
                rs.getString("mpa_name")
        );
    }
}
