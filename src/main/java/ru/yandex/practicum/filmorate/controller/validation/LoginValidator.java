package ru.yandex.practicum.filmorate.controller.validation;

import org.springframework.util.StringUtils;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class LoginValidator implements ConstraintValidator<Login, String> {

    @Override
    public boolean isValid(String login, ConstraintValidatorContext context) {
        return StringUtils.hasText(login) && !login.contains(" ");
    }

}