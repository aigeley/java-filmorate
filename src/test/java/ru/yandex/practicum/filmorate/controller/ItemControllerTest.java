package ru.yandex.practicum.filmorate.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.MockMvc;
import ru.yandex.practicum.filmorate.model.Identifiable;
import ru.yandex.practicum.filmorate.service.exception.ItemAlreadyExistsException;
import ru.yandex.practicum.filmorate.service.exception.ItemNotFoundException;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public abstract class ItemControllerTest<T extends Identifiable<T>> {
    protected final MockMvc mockMvc;
    protected final String path;
    protected final ObjectMapper objectMapper;
    protected final T testItem;
    protected final Class<T> testItemClass;
    protected TypeReference<List<T>> typeOfList;

    public ItemControllerTest(MockMvc mockMvc, String path, ObjectMapper objectMapper, T testItem,
                              Class<T> testItemClass) {
        this.mockMvc = mockMvc;
        this.path = path;
        this.objectMapper = objectMapper;
        this.testItem = testItem;
        this.testItemClass = testItemClass;
    }

    @BeforeEach
    void setUp() throws Exception {
        TestUtils.performDelete(mockMvc, path, status().isOk());
    }

    @Test
    void add_shouldReturn200AndSameItem() throws Exception {
        String responseText = TestUtils.performPost(mockMvc, path, objectMapper.writeValueAsString(testItem), status().isOk())
                .getResponse()
                .getContentAsString();
        T createdItem = objectMapper.readValue(responseText, testItemClass);
        assertEquals(testItem, createdItem);
    }

    @Test
    void add_idIsMissing_shouldReturn200WithNonZeroId() throws Exception {
        T itemToAdd = testItem.withId(0);
        String responseText = TestUtils.performPost(mockMvc, path, objectMapper.writeValueAsString(itemToAdd), status().isOk())
                .getResponse()
                .getContentAsString();
        T createdItem = objectMapper.readValue(responseText, testItemClass);
        assertNotEquals(0, createdItem.getId());
    }

    @Test
    void add_idAlreadyExists_shouldReturn409() throws Exception {
        TestUtils.performPost(mockMvc, path, objectMapper.writeValueAsString(testItem), status().isOk());
        assertEquals(
                ItemAlreadyExistsException.class,
                TestUtils.performPost(mockMvc, path, objectMapper.writeValueAsString(testItem), status().isConflict())
                        .getResolvedException()
                        .getClass()
        );
    }

    @Test
    void add_get_shouldReturn200AndSameItem() throws Exception {
        TestUtils.performPost(mockMvc, path, objectMapper.writeValueAsString(testItem), status().isOk());
        String responseText = TestUtils.performGet(mockMvc, path + "/" + testItem.getId(), status().isOk())
                .getResponse()
                .getContentAsString();
        T createdItem = objectMapper.readValue(responseText, testItemClass);
        assertEquals(testItem, createdItem);
    }

    @Test
    void add_getAll_shouldReturn200AndListOfAllItems() throws Exception {
        T itemToAdd = testItem.withId(testItem.getId() + 1);
        List<T> itemsToAdd = Arrays.asList(testItem, itemToAdd);
        TestUtils.performPost(mockMvc, path, objectMapper.writeValueAsString(testItem), status().isOk());
        TestUtils.performPost(mockMvc, path, objectMapper.writeValueAsString(itemToAdd), status().isOk());
        String responseText = TestUtils.performGet(mockMvc, path, status().isOk())
                .getResponse()
                .getContentAsString();
        List<T> createdItems = objectMapper.readValue(responseText, typeOfList);
        createdItems.sort(Comparator.comparingLong(T::getId));
        assertEquals(itemsToAdd, createdItems);
    }

    @Test
    void add_deleteAll_getAll_shouldReturn200AndEmptyList() throws Exception {
        T itemToAdd = testItem.withId(testItem.getId() + 1);
        TestUtils.performPost(mockMvc, path, objectMapper.writeValueAsString(testItem), status().isOk());
        TestUtils.performPost(mockMvc, path, objectMapper.writeValueAsString(itemToAdd), status().isOk());
        TestUtils.performDelete(mockMvc, path, status().isOk());
        String responseText = TestUtils.performGet(mockMvc, path, status().isOk())
                .getResponse()
                .getContentAsString();
        List<T> createdItems = objectMapper.readValue(responseText, typeOfList);
        assertTrue(createdItems.isEmpty());
    }

    @Test
    void update_idIsMissing_shouldReturn404() throws Exception {
        T itemToUpdate = testItem.withId(0);
        assertEquals(
                ItemNotFoundException.class,
                TestUtils.performPut(mockMvc, path, objectMapper.writeValueAsString(itemToUpdate), status().isNotFound())
                        .getResolvedException()
                        .getClass()
        );
    }

    @Test
    void update_idNotFound_shouldReturn404() throws Exception {
        T itemToUpdate = testItem.withId(-1);
        assertEquals(
                ItemNotFoundException.class,
                TestUtils.performPut(mockMvc, path, objectMapper.writeValueAsString(itemToUpdate), status().isNotFound())
                        .getResolvedException()
                        .getClass()
        );
    }

    @Test
    void get_idIsMissing_shouldReturn404() throws Exception {
        assertEquals(
                ItemNotFoundException.class,
                TestUtils.performGet(mockMvc, path + "/0", status().isNotFound())
                        .getResolvedException()
                        .getClass()
        );
    }

    @Test
    void get_idNotFound_shouldReturn404() throws Exception {
        assertEquals(
                ItemNotFoundException.class,
                TestUtils.performGet(mockMvc, path + "/-1", status().isNotFound())
                        .getResolvedException()
                        .getClass()
        );
    }
}
