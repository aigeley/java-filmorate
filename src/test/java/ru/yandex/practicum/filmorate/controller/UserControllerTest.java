package ru.yandex.practicum.filmorate.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import ru.yandex.practicum.filmorate.model.User;
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
import static ru.yandex.practicum.filmorate.controller.UserController.BASE_PATH;

@SpringBootTest
@AutoConfigureMockMvc
class UserControllerTest extends ItemControllerTest {
    protected final User testUser;
    protected final TypeReference<List<User>> listType;

    @Autowired
    public UserControllerTest(MockMvc mockMvc, ObjectMapper objectMapper) {
        super(mockMvc, BASE_PATH, objectMapper);

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

    @Test
    void add_shouldReturn200AndSameItem() throws Exception {
        String responseText = performPost(path, objectMapper.writeValueAsString(testUser), status().isOk())
                .getResponse()
                .getContentAsString();
        User createdUser = objectMapper.readValue(responseText, User.class);
        assertEquals(testUser, createdUser);
    }

    @Test
    void add_idAndNameAreMissing_birthdayIsCurrentDate_shouldReturn200WithNonZeroIdAndNameEqualsLogin()
            throws Exception {
        User userToAdd = testUser
                .toBuilder()
                .id(0)
                .name("")
                .birthday(LocalDate.now())
                .build();

        String responseText = performPost(path, objectMapper.writeValueAsString(userToAdd), status().isOk())
                .getResponse()
                .getContentAsString();
        User createdUser = objectMapper.readValue(responseText, User.class);
        assertNotEquals(0, createdUser.getId());
        assertEquals(userToAdd.getLogin(), createdUser.getName());
    }

    @Test
    void add_idAlreadyExists_shouldReturn409() throws Exception {
        performPost(path, objectMapper.writeValueAsString(testUser), status().isOk());
        assertEquals(
                ItemAlreadyExistsException.class,
                performPost(path, objectMapper.writeValueAsString(testUser), status().isConflict())
                        .getResolvedException()
                        .getClass()
        );
    }

    @Test
    void add_emailWithoutAtSign_shouldReturn400() throws Exception {
        User userToAdd = testUser
                .toBuilder()
                .email("TAndersonmetacortex.com")
                .build();

        performPost(path, objectMapper.writeValueAsString(userToAdd), status().isBadRequest());
    }

    @Test
    void add_emailIsMissing_shouldReturn400() throws Exception {
        User userToAdd = testUser
                .toBuilder()
                .email("")
                .build();

        performPost(path, objectMapper.writeValueAsString(userToAdd), status().isBadRequest());
    }

    @Test
    void add_loginWithSpace_shouldReturn400() throws Exception {
        User userToAdd = testUser
                .toBuilder()
                .login("N e o")
                .build();

        performPost(path, objectMapper.writeValueAsString(userToAdd), status().isBadRequest());
    }

    @Test
    void add_loginIsMissing_shouldReturn400() throws Exception {
        User userToAdd = testUser
                .toBuilder()
                .login("")
                .build();

        performPost(path, objectMapper.writeValueAsString(userToAdd), status().isBadRequest());
    }

    @Test
    void add_birthdayInFuture_shouldReturn400() throws Exception {
        User userToAdd = testUser
                .toBuilder()
                .birthday(LocalDate.of(2062, 3, 11))
                .build();

        performPost(path, objectMapper.writeValueAsString(userToAdd), status().isBadRequest());
    }

    @Test
    void add_get_shouldReturn200AndListOfAllItems() throws Exception {
        User userToAdd = testUser
                .toBuilder()
                .id(testUser.getId() + 1)
                .build();

        List<User> usersToAdd = Arrays.asList(testUser, userToAdd);

        performPost(path, objectMapper.writeValueAsString(testUser), status().isOk());
        performPost(path, objectMapper.writeValueAsString(userToAdd), status().isOk());

        String responseText = performGet(path, status().isOk())
                .getResponse()
                .getContentAsString();
        List<User> createdUsers = objectMapper.readValue(responseText, listType);
        createdUsers.sort(Comparator.comparingLong(User::getId));
        assertEquals(usersToAdd, createdUsers);
    }

    @Test
    void add_delete_get_shouldReturn200AndEmptyList() throws Exception {
        User userToAdd = testUser
                .toBuilder()
                .id(testUser.getId() + 1)
                .build();

        performPost(path, objectMapper.writeValueAsString(testUser), status().isOk());
        performPost(path, objectMapper.writeValueAsString(userToAdd), status().isOk());
        performDelete(path, status().isOk());

        String responseText = performGet(path, status().isOk())
                .getResponse()
                .getContentAsString();
        List<User> createdUsers = objectMapper.readValue(responseText, listType);
        assertTrue(createdUsers.isEmpty());
    }

    @Test
    void add_update_shouldReturn200AndUpdatedItem() throws Exception {
        performPost(path, objectMapper.writeValueAsString(testUser), status().isOk());

        User userToUpdate = testUser
                .toBuilder()
                .email("392@yandex.ru")
                .login("Trinity")
                .name("Tiffany")
                .birthday(LocalDate.of(1960, 3, 11))
                .build();

        String responseText = performPut(path, objectMapper.writeValueAsString(userToUpdate), status().isOk())
                .getResponse()
                .getContentAsString();
        User updatedUser = objectMapper.readValue(responseText, User.class);
        assertEquals(userToUpdate, updatedUser);
    }

    @Test
    void update_idIsMissing_shouldReturn500() throws Exception {
        User userToUpdate = testUser
                .toBuilder()
                .id(0)
                .build();

        assertEquals(
                ItemNotFoundException.class,
                performPut(path, objectMapper.writeValueAsString(userToUpdate), status().isInternalServerError())
                        .getResolvedException()
                        .getClass()
        );
    }

    @Test
    void update_idNotFound_shouldReturn500() throws Exception {
        User userToUpdate = testUser
                .toBuilder()
                .id(-1)
                .build();

        assertEquals(
                ItemNotFoundException.class,
                performPut(path, objectMapper.writeValueAsString(userToUpdate), status().isInternalServerError())
                        .getResolvedException()
                        .getClass()
        );
    }
}