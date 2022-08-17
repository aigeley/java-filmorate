package ru.yandex.practicum.filmorate.controller;

import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultMatcher;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;

public class TestUtils {
    public static MvcResult performPost(MockMvc mockMvc, String Path, String jsonToSend, ResultMatcher expectedStatus) throws Exception {
        return mockMvc
                .perform(
                        post(Path)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(jsonToSend)
                )
                .andExpect(expectedStatus)
                .andReturn();
    }

    public static MvcResult performGet(MockMvc mockMvc, String Path, ResultMatcher expectedStatus) throws Exception {
        return mockMvc
                .perform(get(Path))
                .andExpect(expectedStatus)
                .andReturn();
    }

    public static MvcResult performPut(MockMvc mockMvc, String Path, String jsonToSend, ResultMatcher expectedStatus) throws Exception {
        return mockMvc
                .perform(
                        put(Path)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(jsonToSend)
                )
                .andExpect(expectedStatus)
                .andReturn();
    }

    public static MvcResult performDelete(MockMvc mockMvc, String Path, ResultMatcher expectedStatus) throws Exception {
        return mockMvc
                .perform(delete(Path))
                .andExpect(expectedStatus)
                .andReturn();
    }
}
