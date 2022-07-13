package ru.yandex.practicum.filmorate.controller;

import com.google.gson.reflect.TypeToken;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.util.NestedServletException;
import ru.yandex.practicum.filmorate.model.User;

import java.lang.reflect.Type;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static ru.yandex.practicum.filmorate.controller.UserController.BASE_PATH;

@WebMvcTest(controllers = UserController.class)
class UserControllerTest extends ItemControllerTest {
    protected final User testUser;
    protected final Type listType;

    public UserControllerTest(@Autowired MockMvc mockMvc) {
        super(mockMvc, BASE_PATH);

        this.testUser = User
                .builder()
                .id(777)
                .email("TAnderson@metacortex.com")
                .login("Neo")
                .name("Thomas Anderson")
                .birthday(LocalDate.of(1962, 3, 11))
                .build();

        this.listType = new TypeToken<List<User>>() {
        }.getType();
    }

    @Test
    void add_shouldReturn200AndSameItem() throws Exception {
        String responseText = performPost(path, gson.toJson(testUser), status().isOk());
        User createdUser = gson.fromJson(responseText, User.class);
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

        String responseText = performPost(path, gson.toJson(userToAdd), status().isOk());
        User createdUser = gson.fromJson(responseText, User.class);
        assertNotEquals(0, createdUser.getId());
        assertEquals(userToAdd.getLogin(), createdUser.getName());
    }

    @Test
    void add_emailWithoutAtSign_shouldReturn400() throws Exception {
        User userToAdd = testUser
                .toBuilder()
                .email("TAndersonmetacortex.com")
                .build();

        performPost(path, gson.toJson(userToAdd), status().isBadRequest());
    }

    @Test
    void add_emailIsMissing_shouldReturn400() throws Exception {
        User userToAdd = testUser
                .toBuilder()
                .email("")
                .build();

        performPost(path, gson.toJson(userToAdd), status().isBadRequest());
    }

    @Test
    void add_loginWithSpace_shouldReturn400() throws Exception {
        User userToAdd = testUser
                .toBuilder()
                .login("N e o")
                .build();

        performPost(path, gson.toJson(userToAdd), status().isBadRequest());
    }

    @Test
    void add_loginIsMissing_shouldReturn400() throws Exception {
        User userToAdd = testUser
                .toBuilder()
                .login("")
                .build();

        performPost(path, gson.toJson(userToAdd), status().isBadRequest());
    }

    @Test
    void add_birthdayInFuture_shouldReturn400() throws Exception {
        User userToAdd = testUser
                .toBuilder()
                .birthday(LocalDate.of(2062, 3, 11))
                .build();

        performPost(path, gson.toJson(userToAdd), status().isBadRequest());
    }

    @Test
    void add_update_shouldReturn200AndUpdatedItem() throws Exception {
        performPost(path, gson.toJson(testUser), status().isOk());

        User userToUpdate = testUser
                .toBuilder()
                .email("392@yandex.ru")
                .login("Trinity")
                .name("Tiffany")
                .birthday(LocalDate.of(1960, 3, 11))
                .build();

        String responseText = performPut(path, gson.toJson(userToUpdate), status().isOk());
        User updatedUser = gson.fromJson(responseText, User.class);
        assertEquals(userToUpdate, updatedUser);
    }

    @Test
    void update_idIsMissing_shouldReturn500() throws Exception {
        User userToUpdate = testUser
                .toBuilder()
                .id(0)
                .build();

        assertThrows(
                NestedServletException.class,
                () -> performPut(path, gson.toJson(userToUpdate), status().isInternalServerError())
        );
    }

    @Test
    void update_idNotFound_shouldReturn500() throws Exception {
        User userToUpdate = testUser
                .toBuilder()
                .id(-1)
                .build();

        assertThrows(
                NestedServletException.class,
                () -> performPut(path, gson.toJson(userToUpdate), status().isInternalServerError())
        );
    }

    @Test
    void add_get_shouldReturn200AndListOfAllItems() throws Exception {
        User userToAdd = testUser
                .toBuilder()
                .id(testUser.getId() + 1)
                .build();

        List<User> usersToAdd = Arrays.asList(testUser, userToAdd);

        performPost(path, gson.toJson(testUser), status().isOk());
        performPost(path, gson.toJson(userToAdd), status().isOk());

        String responseText = performGet(path, status().isOk());
        List<User> createdUsers = gson.fromJson(responseText, listType);
        createdUsers.sort(Comparator.comparingLong(User::getId));
        assertEquals(usersToAdd, createdUsers);
    }

    @Test
    void add_delete_get_shouldReturn200AndEmptyList() throws Exception {
        User userToAdd = testUser
                .toBuilder()
                .id(testUser.getId() + 1)
                .build();

        performPost(path, gson.toJson(testUser), status().isOk());
        performPost(path, gson.toJson(userToAdd), status().isOk());
        performDelete(path, status().isOk());

        String responseText = performGet(path, status().isOk());
        List<User> createdUsers = gson.fromJson(responseText, listType);
        assertTrue(createdUsers.isEmpty());
    }
}