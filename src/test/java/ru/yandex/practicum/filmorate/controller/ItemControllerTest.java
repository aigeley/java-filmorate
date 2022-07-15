package ru.yandex.practicum.filmorate.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultMatcher;
import ru.yandex.practicum.filmorate.model.Identifiable;
import ru.yandex.practicum.filmorate.service.exception.ItemAlreadyExistsException;
import ru.yandex.practicum.filmorate.service.exception.ItemNotFoundException;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public abstract class ItemControllerTest<T extends Identifiable> {
    private final MockMvc mockMvc;
    protected final String path;
    protected final ObjectMapper objectMapper;
    protected final T testItem;
    protected final Class<T> testItemClass;
    protected TypeReference<List<T>> listType;

    public ItemControllerTest(MockMvc mockMvc, String path, ObjectMapper objectMapper, T testItem,
                              Class<T> testItemClass) {
        this.mockMvc = mockMvc;
        this.path = path;
        this.objectMapper = objectMapper;
        this.testItem = testItem;
        this.testItemClass = testItemClass;
    }

    protected MvcResult performPost(String Path, String jsonToSend, ResultMatcher expectedStatus) throws Exception {
        return mockMvc
                .perform(
                        post(Path)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(jsonToSend)
                )
                .andExpect(expectedStatus)
                .andReturn();
    }

    protected MvcResult performPut(String Path, String jsonToSend, ResultMatcher expectedStatus) throws Exception {
        return mockMvc
                .perform(
                        put(Path)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(jsonToSend)
                )
                .andExpect(expectedStatus)
                .andReturn();
    }

    protected MvcResult performGet(String Path, ResultMatcher expectedStatus) throws Exception {
        return mockMvc
                .perform(get(Path))
                .andExpect(expectedStatus)
                .andReturn();
    }

    protected MvcResult performDelete(String Path, ResultMatcher expectedStatus) throws Exception {
        return mockMvc
                .perform(delete(Path))
                .andExpect(expectedStatus)
                .andReturn();
    }

    @BeforeEach
    void setUp() throws Exception {
        performDelete(path, status().isOk());
    }

    @Test
    void add_shouldReturn200AndSameItem() throws Exception {
        String responseText = performPost(path, objectMapper.writeValueAsString(testItem), status().isOk())
                .getResponse()
                .getContentAsString();
        T createdItem = objectMapper.readValue(responseText, testItemClass);
        assertEquals(testItem, createdItem);
    }

    @Test
    void add_idAlreadyExists_shouldReturn409() throws Exception {
        performPost(path, objectMapper.writeValueAsString(testItem), status().isOk());
        assertEquals(
                ItemAlreadyExistsException.class,
                performPost(path, objectMapper.writeValueAsString(testItem), status().isConflict())
                        .getResolvedException()
                        .getClass()
        );
    }

    @Test
    void add_get_shouldReturn200AndSameItem() throws Exception {
        performPost(path, objectMapper.writeValueAsString(testItem), status().isOk());
        String responseText = performGet(path + "/" + testItem.getId(), status().isOk())
                .getResponse()
                .getContentAsString();
        T createdItem = objectMapper.readValue(responseText, testItemClass);
        assertEquals(testItem, createdItem);
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
}
