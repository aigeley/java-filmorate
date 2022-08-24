package ru.yandex.practicum.filmorate.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class GenreControllerTest {
    protected final MockMvc mockMvc;
    protected final String path;
    protected final ObjectMapper objectMapper;
    protected final Class<Genre> testItemClass;
    protected final TypeReference<Set<Genre>> typeOfSet;

    @Autowired
    GenreControllerTest(MockMvc mockMvc, ObjectMapper objectMapper) {
        this.mockMvc = mockMvc;
        this.objectMapper = objectMapper;
        this.path = GenreController.BASE_PATH;
        this.testItemClass = Genre.class;
        this.typeOfSet = new TypeReference<>() {
        };
    }

    @Test
    void get_shouldReturn200() throws Exception {
        String responseText = TestUtils.performGet(mockMvc, path + "/" + 1, status().isOk())
                .getResponse()
                .getContentAsString();
        Genre genre = objectMapper.readValue(responseText, testItemClass);
        assertEquals(1, genre.getId());
        assertEquals("Комедия", genre.getName());
    }

    @Test
    void add_getAll_shouldReturn200AndListOfAllItems() throws Exception {
        Set<Genre> genres = new LinkedHashSet<>(
                Arrays.asList(
                        new Genre(1, "Комедия"),
                        new Genre(2, "Драма"),
                        new Genre(3, "Мультфильм"),
                        new Genre(4, "Триллер"),
                        new Genre(5, "Документальный"),
                        new Genre(6, "Боевик")
                )
        );
        String responseText = TestUtils.performGet(mockMvc, path, status().isOk())
                .getResponse()
                .getContentAsString();
        Set<Genre> retrievedGenres = objectMapper.readValue(responseText, typeOfSet);
        assertEquals(genres, retrievedGenres);
    }
}