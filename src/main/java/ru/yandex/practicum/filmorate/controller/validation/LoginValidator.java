package ru.yandex.practicum.filmorate.controller.validation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class LoginValidator implements ConstraintValidator<Login, String> {

    @Override
    public boolean isValid(String login, ConstraintValidatorContext context) {
        return !(login == null || login.isBlank() || login.contains(" "));
    }

}