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
import java.util.List;

@Data
@Builder
public class Film {

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
    @NotNull
    @EqualsAndHashCode.Exclude
    private Integer duration;
    @Positive
    @EqualsAndHashCode.Exclude
    private Byte rate;
    @EqualsAndHashCode.Exclude
    private List<Genre> genres;
    @NotNull
    @EqualsAndHashCode.Exclude
    private Mpa mpa;
    private List<Director> directors;

    public void setUpGenres(List<Genre> genresToSetUp) {

        genres.clear();
        genres.addAll(genresToSetUp);

    }

}
