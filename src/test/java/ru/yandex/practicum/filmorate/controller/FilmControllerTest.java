package ru.yandex.practicum.filmorate.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.validation.ReleaseDateValidator;
import ru.yandex.practicum.filmorate.service.exception.ItemAlreadyExistsException;
import ru.yandex.practicum.filmorate.service.exception.ItemNotFoundException;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static ru.yandex.practicum.filmorate.controller.FilmController.BASE_PATH;

@SpringBootTest
@AutoConfigureMockMvc
class FilmControllerTest extends ItemControllerTest {
    protected final Film testFilm;
    protected final TypeReference<List<Film>> listType;

    @Autowired
    public FilmControllerTest(MockMvc mockMvc, ObjectMapper objectMapper) {
        super(mockMvc, BASE_PATH, objectMapper);

        this.testFilm = Film
                .builder()
                .id(666)
                .name("The Matrix")
                .description("Wake up, Neo...")
                .releaseDate(LocalDate.of(1999, 3, 24))
                .duration(136)
                .build();

        this.listType = new TypeReference<>() {
        };
    }

    @Test
    void add_shouldReturn200AndSameItem() throws Exception {
        String responseText = performPost(path, objectMapper.writeValueAsString(testFilm), status().isOk())
                .getResponse()
                .getContentAsString();
        Film createdFilm = objectMapper.readValue(responseText, Film.class);
        assertEquals(testFilm, createdFilm);
    }

    @Test
    void add_descriptionAndReleaseDateBoundaryValues_shouldReturn200() throws Exception {
        Film filmToAdd = testFilm
                .toBuilder()
                .description(
                        "Wake up, Neo... "
                                + "The Matrix has you... "
                                + "Follow the white rabbit. "
                                + "Knock, knock, Neo. "
                                + "When a beautiful stranger leads computer hacker Neo to a forbidding underworld, "
                                + "he discovers the shocking truth - 200!"
                )
                .releaseDate(ReleaseDateValidator.FIRST_FILM_SHOW)
                .build();

        String responseText = performPost(path, objectMapper.writeValueAsString(filmToAdd), status().isOk())
                .getResponse()
                .getContentAsString();
        Film createdFilm = objectMapper.readValue(responseText, Film.class);
        assertEquals(filmToAdd, createdFilm);
    }

    @Test
    void add_idAlreadyExists_shouldReturn409() throws Exception {
        performPost(path, objectMapper.writeValueAsString(testFilm), status().isOk());
        assertEquals(
                ItemAlreadyExistsException.class,
                performPost(path, objectMapper.writeValueAsString(testFilm), status().isConflict())
                        .getResolvedException()
                        .getClass()
        );
    }

    @Test
    void add_idIsMissing_shouldReturn200WithNonZeroId() throws Exception {
        Film filmToAdd = testFilm
                .toBuilder()
                .id(0)
                .build();

        String responseText = performPost(path, objectMapper.writeValueAsString(filmToAdd), status().isOk())
                .getResponse()
                .getContentAsString();
        Film createdFilm = objectMapper.readValue(responseText, Film.class);
        assertNotEquals(0, createdFilm.getId());
    }

    @Test
    void add_nameIsMissing_shouldReturn400() throws Exception {
        Film filmToAdd = testFilm
                .toBuilder()
                .name("")
                .build();

        performPost(path, objectMapper.writeValueAsString(filmToAdd), status().isBadRequest());
    }

    @Test
    void add_descriptionIsLongerThan200Chars_shouldReturn400() throws Exception {
        Film filmToAdd = testFilm
                .toBuilder()
                .description(
                        "Wake up, Neo... "
                                + "The Matrix has you... "
                                + "Follow the white rabbit. "
                                + "Knock, knock, Neo. "
                                + "When a beautiful stranger leads computer hacker Neo to a forbidding underworld, "
                                + "he discovers the shocking truth - the life he knows is the elaborate deception "
                                + "of an evil cyber-intelligence."
                )
                .build();

        performPost(path, objectMapper.writeValueAsString(filmToAdd), status().isBadRequest());
    }

    @Test
    void add_releaseDateIsBeforeTheFirstFilmShow_shouldReturn400() throws Exception {
        Film filmToAdd = testFilm
                .toBuilder()
                .releaseDate(LocalDate.of(1895, 12, 27))
                .build();

        performPost(path, objectMapper.writeValueAsString(filmToAdd), status().isBadRequest());
    }

    @Test
    void add_durationIsNegative_shouldReturn400() throws Exception {
        Film filmToAdd = testFilm
                .toBuilder()
                .duration(-136)
                .build();

        performPost(path, objectMapper.writeValueAsString(filmToAdd), status().isBadRequest());
    }

    @Test
    void add_durationIsZero_shouldReturn400() throws Exception {
        Film filmToAdd = testFilm
                .toBuilder()
                .duration(0)
                .build();

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
        Film updatedFilm = objectMapper.readValue(responseText, Film.class);
        assertEquals(filmToUpdate, updatedFilm);
    }

    @Test
    void update_idIsMissing_shouldReturn500() throws Exception {
        Film filmToUpdate = testFilm
                .toBuilder()
                .id(0)
                .build();

        assertEquals(
                ItemNotFoundException.class,
                performPut(path, objectMapper.writeValueAsString(filmToUpdate), status().isInternalServerError())
                        .getResolvedException()
                        .getClass()
        );
    }

    @Test
    void update_idNotFound_shouldReturn500() throws Exception {
        Film filmToUpdate = testFilm
                .toBuilder()
                .id(-1)
                .build();

        assertEquals(
                ItemNotFoundException.class,
                performPut(path, objectMapper.writeValueAsString(filmToUpdate), status().isInternalServerError())
                        .getResolvedException()
                        .getClass()
        );
    }

    @Test
    void add_get_shouldReturn200AndSameItem() throws Exception {
        performPost(path, objectMapper.writeValueAsString(testFilm), status().isOk());
        String responseText = performGet(path + "/" + testFilm.getId(), status().isOk())
                .getResponse()
                .getContentAsString();
        Film createdFilm = objectMapper.readValue(responseText, Film.class);
        assertEquals(testFilm, createdFilm);
    }

    @Test
    void get_idIsMissing_shouldReturn500() throws Exception {
        assertEquals(
                ItemNotFoundException.class,
                performGet(path + "/0", status().isInternalServerError())
                        .getResolvedException()
                        .getClass()
        );
    }

    @Test
    void get_idNotFound_shouldReturn500() throws Exception {
        assertEquals(
                ItemNotFoundException.class,
                performGet(path + "/-1", status().isInternalServerError())
                        .getResolvedException()
                        .getClass()
        );
    }

    @Test
    void add_getAll_shouldReturn200AndListOfAllItems() throws Exception {
        Film filmToAdd = testFilm
                .toBuilder()
                .id(testFilm.getId() + 1)
                .build();

        List<Film> filmsToAdd = Arrays.asList(testFilm, filmToAdd);

        performPost(path, objectMapper.writeValueAsString(testFilm), status().isOk());
        performPost(path, objectMapper.writeValueAsString(filmToAdd), status().isOk());

        String responseText = performGet(path, status().isOk())
                .getResponse()
                .getContentAsString();
        List<Film> createdFilms = objectMapper.readValue(responseText, listType);
        createdFilms.sort(Comparator.comparingLong(Film::getId));
        assertEquals(filmsToAdd, createdFilms);
    }

    @Test
    void add_deleteAll_getAll_shouldReturn200AndEmptyList() throws Exception {
        Film filmToAdd = testFilm
                .toBuilder()
                .id(testFilm.getId() + 1)
                .build();

        performPost(path, objectMapper.writeValueAsString(testFilm), status().isOk());
        performPost(path, objectMapper.writeValueAsString(filmToAdd), status().isOk());
        performDelete(path, status().isOk());

        String responseText = performGet(path, status().isOk())
                .getResponse()
                .getContentAsString();
        List<Film> createdFilms = objectMapper.readValue(responseText, listType);
        assertTrue(createdFilms.isEmpty());
    }
}