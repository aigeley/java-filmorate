package ru.yandex.practicum.filmorate.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static ru.yandex.practicum.filmorate.controller.UserController.BASE_PATH;

@SpringBootTest
@AutoConfigureMockMvc
class UserControllerTest extends ItemControllerTest<User> {
    private final User testUser;

    @Autowired
    public UserControllerTest(MockMvc mockMvc, ObjectMapper objectMapper) {
        super(mockMvc,
                BASE_PATH,
                objectMapper,
                User
                        .builder()
                        .id(777)
                        .email("TAnderson@metacortex.com")
                        .login("Neo")
                        .name("Thomas Anderson")
                        .birthday(LocalDate.of(1962, 3, 11))
                        .build(),
                User.class);

        this.testUser = this.testItem;
        this.listType = new TypeReference<>() {
        };
    }

    @Test
    void add_nameIsMissing_shouldReturn200WithNameEqualsLogin() throws Exception {
        User userToAdd = testUser.withName("");
        String responseText = performPost(path, objectMapper.writeValueAsString(userToAdd), status().isOk())
                .getResponse()
                .getContentAsString();
        User createdUser = objectMapper.readValue(responseText, User.class);
        assertEquals(userToAdd.getLogin(), createdUser.getName());
    }

    @Test
    void add_birthdayIsCurrentDate_shouldReturn200() throws Exception {
        User userToAdd = testUser.withBirthday(LocalDate.now());
        String responseText = performPost(path, objectMapper.writeValueAsString(userToAdd), status().isOk())
                .getResponse()
                .getContentAsString();
        User createdUser = objectMapper.readValue(responseText, User.class);
        assertEquals(userToAdd, createdUser);
    }

    @Test
    void add_emailWithoutAtSign_shouldReturn400() throws Exception {
        User userToAdd = testUser.withEmail("TAndersonmetacortex.com");
        performPost(path, objectMapper.writeValueAsString(userToAdd), status().isBadRequest());
    }

    @Test
    void add_emailIsMissing_shouldReturn400() throws Exception {
        User userToAdd = testUser.withEmail("");
        performPost(path, objectMapper.writeValueAsString(userToAdd), status().isBadRequest());
    }

    @Test
    void add_loginWithSpace_shouldReturn400() throws Exception {
        User userToAdd = testUser.withLogin("N e o");
        performPost(path, objectMapper.writeValueAsString(userToAdd), status().isBadRequest());
    }

    @Test
    void add_loginIsMissing_shouldReturn400() throws Exception {
        User userToAdd = testUser.withLogin("");
        performPost(path, objectMapper.writeValueAsString(userToAdd), status().isBadRequest());
    }

    @Test
    void add_birthdayInFuture_shouldReturn400() throws Exception {
        User userToAdd = testUser.withBirthday(LocalDate.of(2062, 3, 11));
        performPost(path, objectMapper.writeValueAsString(userToAdd), status().isBadRequest());
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
    void add_addFriend_get_shouldReturn200AndMutualFriendsList() throws Exception {
        User friend1 = testUser.withId(testUser.getId() + 1);
        User friend2 = testUser.withId(testUser.getId() + 2);
        performPost(path, objectMapper.writeValueAsString(testUser), status().isOk());
        performPost(path, objectMapper.writeValueAsString(friend1), status().isOk());
        performPost(path, objectMapper.writeValueAsString(friend2), status().isOk());
        performPut(path + "/" + testUser.getId() + "/friends/" + friend1.getId(), "", status().isOk());
        performPut(path + "/" + testUser.getId() + "/friends/" + friend2.getId(), "", status().isOk());
        String responseText = performGet(path + "/" + testUser.getId(), status().isOk())
                .getResponse()
                .getContentAsString();
        User userWithFriends = objectMapper.readValue(responseText, User.class);
        assertEquals(2, userWithFriends.getFriends().size());
        assertTrue(userWithFriends.getFriends().contains(friend1.getId()));
        assertTrue(userWithFriends.getFriends().contains(friend2.getId()));
        String responseText1 = performGet(path + "/" + friend1.getId(), status().isOk())
                .getResponse()
                .getContentAsString();
        User usersFriend1 = objectMapper.readValue(responseText1, User.class);
        assertEquals(1, usersFriend1.getFriends().size());
        assertTrue(usersFriend1.getFriends().contains(testUser.getId()));
    }
}