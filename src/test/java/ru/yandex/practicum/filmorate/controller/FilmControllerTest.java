package ru.yandex.practicum.filmorate.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.model.validation.ReleaseDateValidator;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
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
                        .mpa(new Mpa(4, "R"))
                        .genres(new LinkedHashSet<>(Arrays.asList(new Genre(6, "Боевик"), new Genre(1, "Комедия"))))
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

        this.typeOfList = new TypeReference<>() {
        };
    }

    @Override
    @AfterEach
    void setUp() throws Exception {
        TestUtils.performDelete(mockMvc, UserController.BASE_PATH, status().isOk());
        super.setUp();
    }

    @Test
    void add_nameIsMissing_shouldReturn400() throws Exception {
        Film filmToAdd = testFilm.withName("");
        TestUtils.performPost(mockMvc, path, objectMapper.writeValueAsString(filmToAdd), status().isBadRequest());
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

        String responseText = TestUtils.performPost(mockMvc, path, objectMapper.writeValueAsString(filmToAdd), status().isOk())
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

        TestUtils.performPost(mockMvc, path, objectMapper.writeValueAsString(filmToAdd), status().isBadRequest());
    }

    @Test
    void add_releaseDateIsTheFirstFilmShow_shouldReturn200() throws Exception {
        Film filmToAdd = testFilm.withReleaseDate(ReleaseDateValidator.FIRST_FILM_SHOW);
        String responseText = TestUtils.performPost(mockMvc, path, objectMapper.writeValueAsString(filmToAdd), status().isOk())
                .getResponse()
                .getContentAsString();
        Film createdFilm = objectMapper.readValue(responseText, testItemClass);
        assertEquals(filmToAdd, createdFilm);
    }

    @Test
    void add_releaseDateIsBeforeTheFirstFilmShow_shouldReturn400() throws Exception {
        Film filmToAdd = testFilm.withReleaseDate(LocalDate.of(1895, 12, 27));
        TestUtils.performPost(mockMvc, path, objectMapper.writeValueAsString(filmToAdd), status().isBadRequest());
    }

    @Test
    void add_durationIsNegative_shouldReturn400() throws Exception {
        Film filmToAdd = testFilm.withDuration(-136);
        TestUtils.performPost(mockMvc, path, objectMapper.writeValueAsString(filmToAdd), status().isBadRequest());
    }

    @Test
    void add_durationIsZero_shouldReturn400() throws Exception {
        Film filmToAdd = testFilm.withDuration(0);
        TestUtils.performPost(mockMvc, path, objectMapper.writeValueAsString(filmToAdd), status().isBadRequest());
    }

    @Test
    void add_update_shouldReturn200AndUpdatedItem() throws Exception {
        TestUtils.performPost(mockMvc, path, objectMapper.writeValueAsString(testFilm), status().isOk());

        Film filmToUpdate = testFilm
                .toBuilder()
                .name("The Animatrix")
                .description("Animatorikkusu")
                .releaseDate(LocalDate.of(2003, 6, 3))
                .duration(102)
                .build();

        String responseText = TestUtils.performPut(mockMvc, path, objectMapper.writeValueAsString(filmToUpdate), status().isOk())
                .getResponse()
                .getContentAsString();
        Film updatedFilm = objectMapper.readValue(responseText, testItemClass);
        assertEquals(filmToUpdate, updatedFilm);
    }

    @Test
    void add_addLike_get_shouldReturn200AndSameItem() throws Exception {
        long testFilmId = testFilm.getId();
        long testUserId = testUser.getId();
        long userId1 = testUserId + 1;
        User user1 = testUser.withId(userId1);
        TestUtils.performPost(mockMvc, path, objectMapper.writeValueAsString(testFilm), status().isOk());
        TestUtils.performPost(mockMvc, UserController.BASE_PATH, objectMapper.writeValueAsString(testUser), status().isOk());
        TestUtils.performPost(mockMvc, UserController.BASE_PATH, objectMapper.writeValueAsString(user1), status().isOk());
        TestUtils.performPut(mockMvc, path + "/" + testFilmId + "/like/" + testUserId, "", status().isOk());
        TestUtils.performPut(mockMvc, path + "/" + testFilmId + "/like/" + userId1, "", status().isOk());
        String responseText = TestUtils.performGet(mockMvc, path + "/" + testFilmId, status().isOk())
                .getResponse()
                .getContentAsString();
        Film filmWithLikes = objectMapper.readValue(responseText, testItemClass);
        assertEquals(testFilm, filmWithLikes);
    }

    @Test
    void add_addLike_deleteLike_get_shouldReturn200AndSameItem() throws Exception {
        long testFilmId = testFilm.getId();
        long testUserId = testUser.getId();
        long userId1 = testUserId + 1;
        User user1 = testUser.withId(userId1);
        TestUtils.performPost(mockMvc, path, objectMapper.writeValueAsString(testFilm), status().isOk());
        TestUtils.performPost(mockMvc, UserController.BASE_PATH, objectMapper.writeValueAsString(testUser), status().isOk());
        TestUtils.performPost(mockMvc, UserController.BASE_PATH, objectMapper.writeValueAsString(user1), status().isOk());
        TestUtils.performPut(mockMvc, path + "/" + testFilmId + "/like/" + testUserId, "", status().isOk());
        TestUtils.performPut(mockMvc, path + "/" + testFilmId + "/like/" + userId1, "", status().isOk());
        TestUtils.performDelete(mockMvc, path + "/" + testFilmId + "/like/" + userId1, status().isOk());
        String responseText = TestUtils.performGet(mockMvc, path + "/" + testFilmId, status().isOk())
                .getResponse()
                .getContentAsString();
        Film filmWithLikes = objectMapper.readValue(responseText, testItemClass);
        assertEquals(testFilm, filmWithLikes);
    }

    @Test
    void add_addLike_getPopularFilms_shouldReturn200AndLimitedFilmsList() throws Exception {
        int count = 3;
        long userId0 = testUser.getId();
        long userId1 = userId0 + 1;
        User user1 = testUser.withId(userId1);
        TestUtils.performPost(mockMvc, UserController.BASE_PATH, objectMapper.writeValueAsString(testUser), status().isOk());
        TestUtils.performPost(mockMvc, UserController.BASE_PATH, objectMapper.writeValueAsString(user1), status().isOk());
        long filmId0 = testFilm.getId();
        long filmId1 = filmId0 + 1;
        long filmId2 = filmId0 + 2;
        long filmId3 = filmId0 + 3;
        Film film1 = testFilm.withId(filmId1);
        Film film2 = testFilm.withId(filmId2);
        Film film3 = testFilm.withId(filmId3);
        List<Film> expectedPopularFilms = Arrays.asList(testFilm, film1, film2);
        TestUtils.performPost(mockMvc, path, objectMapper.writeValueAsString(testFilm), status().isOk());
        TestUtils.performPost(mockMvc, path, objectMapper.writeValueAsString(film1), status().isOk());
        TestUtils.performPost(mockMvc, path, objectMapper.writeValueAsString(film2), status().isOk());
        TestUtils.performPost(mockMvc, path, objectMapper.writeValueAsString(film3), status().isOk());
        TestUtils.performPut(mockMvc, path + "/" + filmId0 + "/like/" + userId0, "", status().isOk());
        TestUtils.performPut(mockMvc, path + "/" + filmId0 + "/like/" + userId1, "", status().isOk());
        TestUtils.performPut(mockMvc, path + "/" + filmId1 + "/like/" + userId0, "", status().isOk());
        String responseText = TestUtils.performGet(mockMvc, path + "/popular?count=" + count, status().isOk())
                .getResponse()
                .getContentAsString();
        List<Film> actualPopularFilms = objectMapper.readValue(responseText, typeOfList);
        assertEquals(count, actualPopularFilms.size());
        assertEquals(expectedPopularFilms, actualPopularFilms);
    }

    @Test
    void add_getPopularFilms_countIsMissing_shouldReturn200AndDefaultLengthFilmsList() throws Exception {
        int count = Integer.parseInt(FilmController.DEFAULT_FILMS_TO_DISPLAY);
        long testFilmId = testFilm.getId();
        for (int i = 0; i <= count; i++) { //добавляем на 1 фильм больше, чем длина списка по умолчанию
            TestUtils.performPost(mockMvc, path, objectMapper.writeValueAsString(testFilm.withId(testFilmId + i)), status().isOk());
        }
        String responseText = TestUtils.performGet(mockMvc, path + "/popular", status().isOk())
                .getResponse()
                .getContentAsString();
        List<Film> actualPopularFilms = objectMapper.readValue(responseText, typeOfList);
        assertEquals(count, actualPopularFilms.size());
    }

    @Test
    void getPopularFilms_countIsZeroOrNegative_shouldReturn400() throws Exception {
        TestUtils.performGet(mockMvc, path + "/popular?count=0", status().isBadRequest());
        TestUtils.performGet(mockMvc, path + "/popular?count=-1", status().isBadRequest());
    }
}