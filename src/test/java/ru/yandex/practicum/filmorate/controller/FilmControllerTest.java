package ru.yandex.practicum.filmorate.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.model.validation.ReleaseDateValidator;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class FilmControllerTest extends ItemControllerTest<Film> {
    private final Film testFilm;
    private final User testUser;

    @Autowired
    public FilmControllerTest(MockMvc mockMvc, ObjectMapper objectMapper) {
        super(mockMvc,
                FilmController.BASE_PATH,
                objectMapper,
                Film
                        .builder()
                        .id(666)
                        .name("The Matrix")
                        .description("Wake up, Neo...")
                        .releaseDate(LocalDate.of(1999, 3, 24))
                        .duration(136)
                        .build(),
                Film.class);

        this.testFilm = this.testItem;
        this.testUser = User
                .builder()
                .id(777)
                .email("TAnderson@metacortex.com")
                .login("Neo")
                .name("Thomas Anderson")
                .birthday(LocalDate.of(1962, 3, 11))
                .build();

        this.listType = new TypeReference<>() {
        };
    }

    @Override
    @BeforeEach
    void setUp() throws Exception {
        performDelete(UserController.BASE_PATH, status().isOk());
        super.setUp();
    }

    @Test
    void add_nameIsMissing_shouldReturn400() throws Exception {
        Film filmToAdd = testFilm.withName("");
        performPost(path, objectMapper.writeValueAsString(filmToAdd), status().isBadRequest());
    }

    @Test
    void add_descriptionIs200Chars_shouldReturn200() throws Exception {
        Film filmToAdd = testFilm.withDescription(
                "Wake up, Neo... "
                        + "The Matrix has you... "
                        + "Follow the white rabbit. "
                        + "Knock, knock, Neo. "
                        + "When a beautiful stranger leads computer hacker Neo to a forbidding underworld, "
                        + "he discovers the shocking truth - 200!"
        );

        String responseText = performPost(path, objectMapper.writeValueAsString(filmToAdd), status().isOk())
                .getResponse()
                .getContentAsString();
        Film createdFilm = objectMapper.readValue(responseText, testItemClass);
        assertEquals(200, createdFilm.getDescription().length());
        assertEquals(filmToAdd, createdFilm);
    }

    @Test
    void add_descriptionIsLongerThan200Chars_shouldReturn400() throws Exception {
        Film filmToAdd = testFilm.withDescription(
                "Wake up, Neo... "
                        + "The Matrix has you... "
                        + "Follow the white rabbit. "
                        + "Knock, knock, Neo. "
                        + "When a beautiful stranger leads computer hacker Neo to a forbidding underworld, "
                        + "he discovers the shocking truth - the life he knows is the elaborate deception "
                        + "of an evil cyber-intelligence."
        );

        performPost(path, objectMapper.writeValueAsString(filmToAdd), status().isBadRequest());
    }

    @Test
    void add_releaseDateIsTheFirstFilmShow_shouldReturn200() throws Exception {
        Film filmToAdd = testFilm.withReleaseDate(ReleaseDateValidator.FIRST_FILM_SHOW);
        String responseText = performPost(path, objectMapper.writeValueAsString(filmToAdd), status().isOk())
                .getResponse()
                .getContentAsString();
        Film createdFilm = objectMapper.readValue(responseText, testItemClass);
        assertEquals(filmToAdd, createdFilm);
    }

    @Test
    void add_releaseDateIsBeforeTheFirstFilmShow_shouldReturn400() throws Exception {
        Film filmToAdd = testFilm.withReleaseDate(LocalDate.of(1895, 12, 27));
        performPost(path, objectMapper.writeValueAsString(filmToAdd), status().isBadRequest());
    }

    @Test
    void add_durationIsNegative_shouldReturn400() throws Exception {
        Film filmToAdd = testFilm.withDuration(-136);
        performPost(path, objectMapper.writeValueAsString(filmToAdd), status().isBadRequest());
    }

    @Test
    void add_durationIsZero_shouldReturn400() throws Exception {
        Film filmToAdd = testFilm.withDuration(0);
        performPost(path, objectMapper.writeValueAsString(filmToAdd), status().isBadRequest());
    }

    @Test
    void add_update_shouldReturn200AndUpdatedItem() throws Exception {
        performPost(path, objectMapper.writeValueAsString(testFilm), status().isOk());

        Film filmToUpdate = testFilm
                .toBuilder()
                .name("The Animatrix")
                .description("Animatorikkusu")
                .releaseDate(LocalDate.of(2003, 6, 3))
                .duration(102)
                .build();

        String responseText = performPut(path, objectMapper.writeValueAsString(filmToUpdate), status().isOk())
                .getResponse()
                .getContentAsString();
        Film updatedFilm = objectMapper.readValue(responseText, testItemClass);
        assertEquals(filmToUpdate, updatedFilm);
    }

    @Test
    void add_addLike_get_shouldReturn200AndLikesList() throws Exception {
        long testFilmId = testFilm.getId();
        long testUserId = testUser.getId();
        long userId1 = testUserId + 1;
        User user1 = testUser.withId(userId1);
        performPost(path, objectMapper.writeValueAsString(testFilm), status().isOk());
        performPost(UserController.BASE_PATH, objectMapper.writeValueAsString(testUser), status().isOk());
        performPost(UserController.BASE_PATH, objectMapper.writeValueAsString(user1), status().isOk());
        performPut(path + "/" + testFilmId + "/like/" + testUserId, "", status().isOk());
        performPut(path + "/" + testFilmId + "/like/" + userId1, "", status().isOk());
        String responseText = performGet(path + "/" + testFilmId, status().isOk())
                .getResponse()
                .getContentAsString();
        Film filmWithLikes = objectMapper.readValue(responseText, testItemClass);
        assertEquals(2, filmWithLikes.getLikes().size());
        assertTrue(filmWithLikes.getLikes().contains(testUserId));
        assertTrue(filmWithLikes.getLikes().contains(userId1));
    }

    @Test
    void add_addLike_deleteLike_get_shouldReturn200AndActualLikesList() throws Exception {
        long testFilmId = testFilm.getId();
        long testUserId = testUser.getId();
        long userId1 = testUserId + 1;
        User user1 = testUser.withId(userId1);
        performPost(path, objectMapper.writeValueAsString(testFilm), status().isOk());
        performPost(UserController.BASE_PATH, objectMapper.writeValueAsString(testUser), status().isOk());
        performPost(UserController.BASE_PATH, objectMapper.writeValueAsString(user1), status().isOk());
        performPut(path + "/" + testFilmId + "/like/" + testUserId, "", status().isOk());
        performPut(path + "/" + testFilmId + "/like/" + userId1, "", status().isOk());
        performDelete(path + "/" + testFilmId + "/like/" + userId1, status().isOk());
        String responseText = performGet(path + "/" + testFilmId, status().isOk())
                .getResponse()
                .getContentAsString();
        Film filmWithLikes = objectMapper.readValue(responseText, testItemClass);
        assertEquals(1, filmWithLikes.getLikes().size());
        assertTrue(filmWithLikes.getLikes().contains(testUserId));
        assertFalse(filmWithLikes.getLikes().contains(userId1));
    }

    @Test
    void add_addLike_getPopularFilms_shouldReturn200AndLimitedFilmsList() throws Exception {
        int count = 3;
        long userId0 = testUser.getId();
        long userId1 = userId0 + 1;
        User user1 = testUser.withId(userId1);
        performPost(UserController.BASE_PATH, objectMapper.writeValueAsString(testUser), status().isOk());
        performPost(UserController.BASE_PATH, objectMapper.writeValueAsString(user1), status().isOk());
        long filmId0 = testFilm.getId();
        long filmId1 = filmId0 + 1;
        long filmId2 = filmId0 + 2;
        long filmId3 = filmId0 + 3;
        Film film1 = testFilm.withId(filmId1);
        Film film2 = testFilm.withId(filmId2);
        Film film3 = testFilm.withId(filmId3);
        performPost(path, objectMapper.writeValueAsString(testFilm), status().isOk());
        performPost(path, objectMapper.writeValueAsString(film1), status().isOk());
        performPost(path, objectMapper.writeValueAsString(film2), status().isOk());
        performPost(path, objectMapper.writeValueAsString(film3), status().isOk());
        performPut(path + "/" + filmId0 + "/like/" + userId0, "", status().isOk());
        performPut(path + "/" + filmId0 + "/like/" + userId1, "", status().isOk());
        performPut(path + "/" + filmId1 + "/like/" + userId0, "", status().isOk());
        Film filmWithLikes0 = testFilm.withLikes(new HashSet<>(Arrays.asList(userId0, userId1)));
        Film filmWithLikes1 = film1.withLikes(new HashSet<>(Arrays.asList(userId0)));
        List<Film> expectedPopularFilms = Arrays.asList(filmWithLikes0, filmWithLikes1, film2);
        String responseText = performGet(path + "/popular?count=" + count, status().isOk())
                .getResponse()
                .getContentAsString();
        List<Film> actualPopularFilms = objectMapper.readValue(responseText, listType);
        assertEquals(count, actualPopularFilms.size());
        assertEquals(expectedPopularFilms, actualPopularFilms);
    }

    @Test
    void add_getPopularFilms_countIsMissing_shouldReturn200AndDefaultLengthFilmsList() throws Exception {
        int count = Integer.parseInt(FilmController.DEFAULT_FILMS_TO_DISPLAY);
        long testFilmId = testFilm.getId();
        for (int i = 0; i <= count; i++) { //добавляем на 1 фильм больше, чем длина списка по умолчанию
            performPost(path, objectMapper.writeValueAsString(testFilm.withId(testFilmId + i)), status().isOk());
        }
        String responseText = performGet(path + "/popular", status().isOk())
                .getResponse()
                .getContentAsString();
        List<Film> actualPopularFilms = objectMapper.readValue(responseText, listType);
        assertEquals(count, actualPopularFilms.size());
    }
}