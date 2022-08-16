package ru.yandex.practicum.filmorate.model;

import lombok.Builder;
import lombok.Value;
import lombok.With;

import javax.validation.constraints.NotBlank;

@With
@Value
@Builder(toBuilder = true)
public class Mpa {
    int id;
    @NotBlank
    String name;

    public Mpa(int id, String name) {
        this.id = id;
        this.name = name;
    }
}
