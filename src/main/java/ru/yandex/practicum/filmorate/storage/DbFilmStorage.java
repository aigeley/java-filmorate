package ru.yandex.practicum.filmorate.storage;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

@Component
public class DbFilmStorage implements FilmStorage, RowMapper<Film> {
    private final JdbcTemplate jdbcTemplate;

    public DbFilmStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private Set<Genre> getGenres(long filmId) {
        String sql = "SELECT g.genre_id, g.genre_name FROM genres g " +
                "JOIN film_genres fg on g.genre_id = fg.genre_id " +
                "JOIN films f on f.film_id = fg.film_id " +
                "WHERE f.film_id = ?";

        return new LinkedHashSet<>(
                jdbcTemplate.query(
                        sql,
                        (rs, rowNum) -> new Genre(rs.getInt("genre_id"), rs.getString("genre_name")),
                        filmId
                )
        );
    }

    private void rewriteGenres(Film film) {
        deleteGenres(film);
        insertGenres(film);
    }

    private void deleteGenres(Film film) {
        long filmId = film.getId();
        String sql = "DELETE FROM film_genres WHERE film_id = ?";
        jdbcTemplate.update(sql, filmId);
    }

    private void insertGenres(Film film) {
        long filmId = film.getId();
        int genresCount = film.getGenres().size();

        if (genresCount == 0) {
            return; //фильму не присвоены жанры
        }

        String sql = "INSERT INTO film_genres (film_id, genre_id) VALUES "
                + DbUtils.getPlaceHolders(genresCount);
        List<Long> filmGenres = new ArrayList<>();

        for (Genre genre : film.getGenres()) {
            filmGenres.add(filmId);
            filmGenres.add((long) genre.getId());
        }

        jdbcTemplate.update(
                sql,
                filmGenres.toArray()
        );
    }

    @Override
    public long getNextId() {
        String sql = "SELECT NEXT VALUE FOR films_seq nextval";

        Long nextId = jdbcTemplate.queryForObject(
                sql,
                (rs, rowNum) -> rs.getLong("nextval")
        );

        return nextId == null ? 0 : nextId;
    }

    @Override
    public Film get(long filmId) {
        String sql = "SELECT f.film_id, f.film_name, f.description, f.release_date, f.duration, f.mpa_id, m.mpa_name " +
                "FROM films f " +
                "JOIN mpa m on m.mpa_id = f.mpa_id " +
                "AND f.film_id = ?";
        return jdbcTemplate.queryForObject(sql, this, filmId);
    }

    @Override
    public Collection<Film> getAll() {
        String sql = "SELECT f.film_id, f.film_name, f.description, f.release_date, f.duration, f.mpa_id, m.mpa_name " +
                "FROM films f " +
                "JOIN mpa m on m.mpa_id = f.mpa_id";
        return jdbcTemplate.query(sql, this);
    }

    @Override
    public Film add(Film film) {
        String sql = "INSERT INTO films (film_id, film_name, description, release_date, duration, mpa_id) " +
                "VALUES (?, ?, ?, ?, ?, ?)";

        jdbcTemplate.update(
                sql,
                film.getId(),
                film.getName(),
                film.getDescription(),
                film.getReleaseDate(),
                film.getDuration(),
                film.getMpa().getId()
        );

        rewriteGenres(film);
        return film;
    }

    @Override
    public Film update(Film film) {
        String sql = "UPDATE films SET film_name = ?, description = ?, release_date = ?, duration = ?, mpa_id = ? " +
                "WHERE film_id = ?";

        jdbcTemplate.update(
                sql,
                film.getName(),
                film.getDescription(),
                film.getReleaseDate(),
                film.getDuration(),
                film.getMpa().getId(),
                film.getId()
        );

        rewriteGenres(film);
        return film;
    }

    @Override
    public void deleteAll() {
        String sql = "DELETE FROM films";
        jdbcTemplate.update(sql);
    }

    @Override
    public boolean isExists(long filmId) {
        String sql = "SELECT COUNT(*) cnt FROM films WHERE film_id = ?";

        Integer cnt = jdbcTemplate.queryForObject(
                sql,
                (rs, rowNum) -> rs.getInt("cnt"),
                filmId
        );

        return (cnt == null ? 0 : cnt) > 0;
    }

    @Override
    public List<Film> getPopularFilms(int count) {
        String sql = "SELECT f.film_id, f.film_name, f.description, f.release_date, f.duration, f.mpa_id, m.mpa_name, " +
                "COUNT(l.user_id) cnt " +
                "FROM films f " +
                "JOIN mpa m on m.mpa_id = f.mpa_id " +
                "LEFT JOIN likes l on f.film_id = l.film_id " +
                "GROUP BY f.film_id, f.film_name, f.description, f.release_date, f.duration, f.mpa_id, m.mpa_name " +
                "ORDER BY cnt DESC, f.film_id " +
                "LIMIT ?";
        return jdbcTemplate.query(sql, this, count);
    }

    @Override
    public Film mapRow(ResultSet rs, int rowNum) throws SQLException {
        long filmId = rs.getLong("film_id");
        Set<Genre> genres = getGenres(filmId);

        return new Film(
                filmId,
                rs.getString("film_name"),
                rs.getString("description"),
                rs.getDate("release_date").toLocalDate(),
                rs.getInt("duration"),
                new Mpa(rs.getInt("mpa_id"), rs.getString("mpa_name")),
                genres
        );
    }
}
