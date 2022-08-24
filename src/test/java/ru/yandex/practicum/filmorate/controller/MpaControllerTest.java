package ru.yandex.practicum.filmorate.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class MpaControllerTest {
    protected final MockMvc mockMvc;
    protected final String path;
    protected final ObjectMapper objectMapper;
    protected final Class<Mpa> testItemClass;
    protected final TypeReference<Set<Mpa>> typeOfSet;

    @Autowired
    MpaControllerTest(MockMvc mockMvc, ObjectMapper objectMapper) {
        this.mockMvc = mockMvc;
        this.objectMapper = objectMapper;
        this.path = MpaController.BASE_PATH;
        this.testItemClass = Mpa.class;
        this.typeOfSet = new TypeReference<>() {
        };
    }

    @Test
    void get_shouldReturn200() throws Exception {
        String responseText = TestUtils.performGet(mockMvc, path + "/" + 1, status().isOk())
                .getResponse()
                .getContentAsString();
        Mpa mpa = objectMapper.readValue(responseText, testItemClass);
        assertEquals(1, mpa.getId());
        assertEquals("G", mpa.getName());
    }

    @Test
    void add_getAll_shouldReturn200AndListOfAllItems() throws Exception {
        Set<Mpa> mpaSet = new LinkedHashSet<>(
                Arrays.asList(
                        new Mpa(1, "G"),
                        new Mpa(2, "PG"),
                        new Mpa(3, "PG-13"),
                        new Mpa(4, "R"),
                        new Mpa(5, "NC-17")
                )
        );
        String responseText = TestUtils.performGet(mockMvc, path, status().isOk())
                .getResponse()
                .getContentAsString();
        Set<Mpa> retrievedMpa = objectMapper.readValue(responseText, typeOfSet);
        assertEquals(mpaSet, retrievedMpa);
    }
}