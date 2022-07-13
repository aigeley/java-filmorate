package ru.yandex.practicum.filmorate.model;

import lombok.Builder;
import lombok.Value;
import ru.yandex.practicum.filmorate.controller.validation.Login;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.PastOrPresent;
import java.time.LocalDate;

@Value
@Builder(toBuilder = true)
public class User implements Identifiable {
    long id;
    @Email
    @NotBlank
    String email;
    @Login
    String login;
    String name;
    @PastOrPresent
    LocalDate birthday;
}
