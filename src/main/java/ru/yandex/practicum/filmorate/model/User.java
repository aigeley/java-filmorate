package ru.yandex.practicum.filmorate.model;

import lombok.Builder;
import lombok.Value;
import lombok.With;
import org.springframework.util.StringUtils;
import ru.yandex.practicum.filmorate.model.validation.Login;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.PastOrPresent;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@With
@Value
@Builder(toBuilder = true)
public class User implements Identifiable<User> {
    long id;
    @Email
    @NotBlank
    String email;
    @Login
    String login;
    String name;
    @PastOrPresent
    LocalDate birthday;
    Set<Long> friends;

    public User(long id, String email, String login, String name, LocalDate birthday, Set<Long> friends) {
        this.id = id;
        this.email = email;
        this.login = login;
        this.name = name;
        this.birthday = birthday;
        this.friends = friends == null ? new HashSet<>() : friends;
    }

    public String getName() {
        return StringUtils.hasText(name) ? name : login; //если name не указано, то используем login
    }

    public Set<Long> getFriends() {
        return new HashSet<>(friends);
    }
}
