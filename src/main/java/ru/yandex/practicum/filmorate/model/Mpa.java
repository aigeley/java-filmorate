package ru.yandex.practicum.filmorate.model;

import lombok.Builder;
import lombok.Value;
import lombok.With;

import javax.validation.constraints.NotBlank;
import java.util.Objects;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Mpa mpa = (Mpa) o;
        return id == mpa.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
