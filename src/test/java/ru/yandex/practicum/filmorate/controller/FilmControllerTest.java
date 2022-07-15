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

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static ru.yandex.practicum.filmorate.controller.FilmController.BASE_PATH;

@SpringBootTest
@AutoConfigureMockMvc
class FilmControllerTest extends ItemControllerTest<Film> {
    private final Film testFilm;

    @Autowired
    public FilmControllerTest(MockMvc mockMvc, ObjectMapper objectMapper) {
        super(mockMvc,
                BASE_PATH,
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
        this.listType = new TypeReference<>() {
        };
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
        Film createdFilm = objectMapper.readValue(responseText, Film.class);
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
        Film createdFilm = objectMapper.readValue(responseText, Film.class);
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
        Film updatedFilm = objectMapper.readValue(responseText, Film.class);
        assertEquals(filmToUpdate, updatedFilm);
    }
}