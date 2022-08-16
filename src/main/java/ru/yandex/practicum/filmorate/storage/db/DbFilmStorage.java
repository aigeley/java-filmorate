package ru.yandex.practicum.filmorate.storage.db;

import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

@Primary
@Component
public class DbFilmStorage implements FilmStorage, RowMapper<Film> {
    private final JdbcTemplate jdbcTemplate;

    public DbFilmStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public long getNextId() {
        String sql = "SELECT NEXT VALUE FOR films_seq nextval";
        Long nextId = jdbcTemplate.queryForObject(sql, (rs, rowNum) -> rs.getLong("nextval"));
        return nextId == null ? 0 : nextId;
    }

    @Override
    public Film get(long filmId) {
        String sql = "SELECT film_id, film_name, description, release_date, duration, mpa_id FROM films " +
                "WHERE film_id = ?";
        return jdbcTemplate.queryForObject(sql, this, filmId);
    }

    @Override
    public Collection<Film> getAll() {
        String sql = "SELECT film_id, film_name, description, release_date, duration, mpa_id FROM films";
        return jdbcTemplate.query(sql, this);
    }

    @Override
    public Film add(Film film) {
        String sqlInsertFilms = "INSERT INTO films (film_id, film_name, description, release_date, duration, mpa_id) " +
                "VALUES (?, ?, ?, ?, ?, ?)";

        jdbcTemplate.update(
                sqlInsertFilms,
                film.getId(),
                film.getName(),
                film.getDescription(),
                film.getReleaseDate(),
                film.getDuration(),
                film.getMpa().getId()
        );

        rewriteLikes(film);
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

        rewriteLikes(film);
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
        Integer cnt = jdbcTemplate.queryForObject(sql, (rs, rowNum) -> rs.getInt("cnt"), filmId);
        return (cnt == null ? 0 : cnt) > 0;
    }

    @Override
    public Film mapRow(ResultSet rs, int rowNum) throws SQLException {
        long filmId = rs.getLong("film_id");
        Set<Genre> genres = getGenres(filmId);
        Set<Long> likes = getLikes(filmId);
        Mpa mpa = getMpa(rs.getInt("mpa_id"));

        return new Film(
                filmId,
                rs.getString("film_name"),
                rs.getString("description"),
                rs.getDate("release_date").toLocalDate(),
                rs.getInt("duration"),
                likes,
                mpa,
                genres
        );
    }

    private Mpa getMpa(int mpaId) {
        String sqlSelectMpa = "SELECT mpa_name FROM mpa WHERE mpa_id = ?";

        return jdbcTemplate.queryForObject(
                sqlSelectMpa,
                (rsMpa, rowNumMpa) -> new Mpa(mpaId, rsMpa.getString("mpa_name")),
                mpaId
        );
    }

    private Set<Genre> getGenres(long filmId) {
        String sqlSelectGenres = "SELECT g.genre_id, g.genre_name FROM genres g " +
                "JOIN film_genres fg on g.genre_id = fg.genre_id " +
                "JOIN films f on f.film_id = fg.film_id " +
                "WHERE f.film_id = ?";

        return new LinkedHashSet<>(
                jdbcTemplate.query(
                        sqlSelectGenres,
                        (rsGenre, rowNumGenre) -> new Genre(rsGenre.getInt("genre_id"), rsGenre.getString("genre_name")),
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
        String sqlDeleteFilmGenres = "DELETE FROM film_genres WHERE film_id = ?";
        jdbcTemplate.update(sqlDeleteFilmGenres, filmId);
    }

    private void insertGenres(Film film) {
        long filmId = film.getId();
        int genresCount = film.getGenres().size();

        if (genresCount == 0) {
            return; //фильму не присвоены жанры
        }

        String sqlInsertFilmGenres = "INSERT INTO film_genres (film_id, genre_id) VALUES "
                + getPlaceHolders(genresCount);
        List<Long> filmGenres = new ArrayList<>();

        for (Genre genre : film.getGenres()) {
            filmGenres.add(filmId);
            filmGenres.add((long) genre.getId());
        }

        jdbcTemplate.update(
                sqlInsertFilmGenres,
                filmGenres.toArray()
        );
    }

    private Set<Long> getLikes(long filmId) {
        String sqlSelectLikes = "SELECT user_id FROM likes WHERE film_id = ?";

        return new LinkedHashSet<>(
                jdbcTemplate.query(
                        sqlSelectLikes,
                        (rsLike, rowNumLike) -> rsLike.getLong("user_id"),
                        filmId
                )
        );
    }

    private void rewriteLikes(Film film) {
        deleteLikes(film);
        insertLikes(film);
    }

    private void deleteLikes(Film film) {
        long filmId = film.getId();
        String sqlDeleteLikes = "DELETE FROM likes WHERE film_id = ?";
        jdbcTemplate.update(sqlDeleteLikes, filmId);
    }

    private void insertLikes(Film film) {
        long filmId = film.getId();
        int likesCount = film.getLikes().size();

        if (likesCount == 0) {
            return; //у фильма нет лайков
        }

        String sqlInsertFilmLikes = "INSERT INTO likes (film_id, user_id) VALUES "
                + getPlaceHolders(likesCount);
        List<Long> filmLikes = new ArrayList<>();

        for (long userId : film.getLikes()) {
            filmLikes.add(filmId);
            filmLikes.add(userId);
        }

        jdbcTemplate.update(
                sqlInsertFilmLikes,
                filmLikes.toArray()
        );
    }
}
