package ru.yandex.practicum.filmorate.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultMatcher;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public abstract class ItemControllerTest {
    private final MockMvc mockMvc;
    protected final String path;
    protected final ObjectMapper objectMapper;

    public ItemControllerTest(MockMvc mockMvc, String path, ObjectMapper objectMapper) {
        this.mockMvc = mockMvc;
        this.path = path;
        this.objectMapper = objectMapper;
    }

    @BeforeEach
    protected void setUp() throws Exception {
        performDelete(path, status().isOk());
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
}
