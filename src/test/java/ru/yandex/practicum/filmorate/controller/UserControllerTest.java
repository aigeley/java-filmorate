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
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
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
        this.typeOfList = new TypeReference<>() {
        };
    }

    @Test
    void add_nameIsMissing_shouldReturn200WithNameEqualsLogin() throws Exception {
        User userToAdd = testUser.withName("");
        String responseText = TestUtils.performPost(mockMvc, path, objectMapper.writeValueAsString(userToAdd), status().isOk())
                .getResponse()
                .getContentAsString();
        User createdUser = objectMapper.readValue(responseText, testItemClass);
        assertEquals(userToAdd.getLogin(), createdUser.getName());
    }

    @Test
    void add_birthdayIsCurrentDate_shouldReturn200() throws Exception {
        User userToAdd = testUser.withBirthday(LocalDate.now());
        String responseText = TestUtils.performPost(mockMvc, path, objectMapper.writeValueAsString(userToAdd), status().isOk())
                .getResponse()
                .getContentAsString();
        User createdUser = objectMapper.readValue(responseText, testItemClass);
        assertEquals(userToAdd, createdUser);
    }

    @Test
    void add_emailWithoutAtSign_shouldReturn400() throws Exception {
        User userToAdd = testUser.withEmail("TAndersonmetacortex.com");
        TestUtils.performPost(mockMvc, path, objectMapper.writeValueAsString(userToAdd), status().isBadRequest());
    }

    @Test
    void add_emailIsMissing_shouldReturn400() throws Exception {
        User userToAdd = testUser.withEmail("");
        TestUtils.performPost(mockMvc, path, objectMapper.writeValueAsString(userToAdd), status().isBadRequest());
    }

    @Test
    void add_loginWithSpace_shouldReturn400() throws Exception {
        User userToAdd = testUser.withLogin("N e o");
        TestUtils.performPost(mockMvc, path, objectMapper.writeValueAsString(userToAdd), status().isBadRequest());
    }

    @Test
    void add_loginIsMissing_shouldReturn400() throws Exception {
        User userToAdd = testUser.withLogin("");
        TestUtils.performPost(mockMvc, path, objectMapper.writeValueAsString(userToAdd), status().isBadRequest());
    }

    @Test
    void add_birthdayInFuture_shouldReturn400() throws Exception {
        User userToAdd = testUser.withBirthday(LocalDate.of(2062, 3, 11));
        TestUtils.performPost(mockMvc, path, objectMapper.writeValueAsString(userToAdd), status().isBadRequest());
    }

    @Test
    void add_update_shouldReturn200AndUpdatedItem() throws Exception {
        TestUtils.performPost(mockMvc, path, objectMapper.writeValueAsString(testUser), status().isOk());

        User userToUpdate = testUser
                .toBuilder()
                .email("392@yandex.ru")
                .login("Trinity")
                .name("Tiffany")
                .birthday(LocalDate.of(1960, 3, 11))
                .build();

        String responseText = TestUtils.performPut(mockMvc, path, objectMapper.writeValueAsString(userToUpdate), status().isOk())
                .getResponse()
                .getContentAsString();
        User updatedUser = objectMapper.readValue(responseText, testItemClass);
        assertEquals(userToUpdate, updatedUser);
    }

    @Test
    void add_addFriend_getFriends_shouldReturn200AndListOfAllFriends() throws Exception {
        long testUserId = testUser.getId();
        long friendId1 = testUserId + 1;
        long friendId2 = testUserId + 2;
        User friend1 = testUser.withId(friendId1);
        User friend2 = testUser.withId(friendId2);
        List<User> expectedUserFriends = Arrays.asList(friend1, friend2);
        TestUtils.performPost(mockMvc, path, objectMapper.writeValueAsString(testUser), status().isOk());
        TestUtils.performPost(mockMvc, path, objectMapper.writeValueAsString(friend1), status().isOk());
        TestUtils.performPost(mockMvc, path, objectMapper.writeValueAsString(friend2), status().isOk());
        TestUtils.performPut(mockMvc, path + "/" + testUserId + "/friends/" + friendId1, "", status().isOk());
        TestUtils.performPut(mockMvc, path + "/" + testUserId + "/friends/" + friendId2, "", status().isOk());
        String responseText = TestUtils.performGet(mockMvc, path + "/" + testUserId + "/friends", status().isOk())
                .getResponse()
                .getContentAsString();
        List<User> actualUserFriends = objectMapper.readValue(responseText, typeOfList);
        assertEquals(2, actualUserFriends.size());
        assertEquals(expectedUserFriends, actualUserFriends);
    }

    @Test
    void add_addFriend_deleteFriend_getFriends_shouldReturn200AndActualFriendsList() throws Exception {
        long testUserId = testUser.getId();
        long friendId1 = testUserId + 1;
        long friendId2 = testUserId + 2;
        User friend1 = testUser.withId(friendId1);
        User friend2 = testUser.withId(friendId2);
        List<User> expectedUserFriends = Arrays.asList(friend2);
        TestUtils.performPost(mockMvc, path, objectMapper.writeValueAsString(testUser), status().isOk());
        TestUtils.performPost(mockMvc, path, objectMapper.writeValueAsString(friend1), status().isOk());
        TestUtils.performPost(mockMvc, path, objectMapper.writeValueAsString(friend2), status().isOk());
        TestUtils.performPut(mockMvc, path + "/" + testUserId + "/friends/" + friendId1, "", status().isOk());
        TestUtils.performPut(mockMvc, path + "/" + testUserId + "/friends/" + friendId2, "", status().isOk());
        TestUtils.performDelete(mockMvc, path + "/" + testUserId + "/friends/" + friendId1, status().isOk());
        String responseText = TestUtils.performGet(mockMvc, path + "/" + testUserId + "/friends", status().isOk())
                .getResponse()
                .getContentAsString();
        List<User> actualUserFriends = objectMapper.readValue(responseText, typeOfList);
        assertEquals(1, actualUserFriends.size());
        assertEquals(expectedUserFriends, actualUserFriends);
    }

    @Test
    void add_addFriend_getCommonFriends_shouldReturn200AndListOfCommonFriends() throws Exception {
        long testUserId = testUser.getId();
        long friendId1 = testUserId + 1;
        long friendId2 = testUserId + 2;
        User friend1 = testUser.withId(friendId1);
        User friend2 = testUser.withId(friendId2);
        List<User> expectedCommonFriends = Arrays.asList(friend2);
        TestUtils.performPost(mockMvc, path, objectMapper.writeValueAsString(testUser), status().isOk());
        TestUtils.performPost(mockMvc, path, objectMapper.writeValueAsString(friend1), status().isOk());
        TestUtils.performPost(mockMvc, path, objectMapper.writeValueAsString(friend2), status().isOk());
        TestUtils.performPut(mockMvc, path + "/" + testUserId + "/friends/" + friendId1, "", status().isOk());
        TestUtils.performPut(mockMvc, path + "/" + testUserId + "/friends/" + friendId2, "", status().isOk());
        TestUtils.performPut(mockMvc, path + "/" + friendId1 + "/friends/" + friendId2, "", status().isOk());
        String responseText = TestUtils.performGet(mockMvc, path + "/" + testUserId + "/friends/common/" + friendId1, status().isOk())
                .getResponse()
                .getContentAsString();
        List<User> actualCommonFriends = objectMapper.readValue(responseText, typeOfList);
        assertEquals(expectedCommonFriends, actualCommonFriends);
    }
}