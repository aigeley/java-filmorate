package ru.yandex.practicum.filmorate.model;

import lombok.Builder;
import lombok.Value;
import lombok.With;
import ru.yandex.practicum.filmorate.model.validation.ReleaseDate;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@With
@Value
@Builder(toBuilder = true)
public class Film implements Identifiable<Film> {
    long id;
    @NotBlank
    String name;
    @Size(max = 200)
    String description;
    @ReleaseDate
    LocalDate releaseDate;
    @Positive
    int duration;
    Set<Long> likes;

    public Film(long id, String name, String description, LocalDate releaseDate, int duration, Set<Long> likes) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.releaseDate = releaseDate;
        this.duration = duration;
        this.likes = likes == null ? new HashSet<>() : likes;
    }

    public Set<Long> getLikes() {
        return new HashSet<>(likes);
    }
}
