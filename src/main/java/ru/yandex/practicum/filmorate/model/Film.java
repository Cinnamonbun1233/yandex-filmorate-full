package ru.yandex.practicum.filmorate.model;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.jetbrains.annotations.NotNull;
import ru.yandex.practicum.filmorate.annotation.IsAfterCinemaBirthday;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;
import java.time.LocalDate;
import java.util.Set;
import java.util.TreeSet;

@Data
@Builder
public class Film implements Comparable<Film>{

    @Positive
    @EqualsAndHashCode.Exclude
    private Long id;
    @NotBlank
    private String name;
    @Size(max = 200)
    private String description;
    @IsAfterCinemaBirthday
    private LocalDate releaseDate;
    @Positive
    @EqualsAndHashCode.Exclude
    private Integer duration;
    @Positive
    @EqualsAndHashCode.Exclude
    private Byte rate;
    @EqualsAndHashCode.Exclude
    private TreeSet<Genre> genres;
    @EqualsAndHashCode.Exclude
    private Mpa mpa;

    public void setUpGenres(Set<Genre> genresToSetUp) {

        genres.clear();
        genres.addAll(genresToSetUp);

    }

    @Override
    public int compareTo(@NotNull Film other) {
        return Long.compare(this.getId(), other.getId());
    }

}
