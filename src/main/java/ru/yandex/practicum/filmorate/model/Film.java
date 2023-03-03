package ru.yandex.practicum.filmorate.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import ru.yandex.practicum.filmorate.annotation.IsAfterCinemaBirthday;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;
import java.time.LocalDate;

@Data
public class Film {

    @Positive
    @EqualsAndHashCode.Exclude
    private Integer id;
    @NotBlank
    private String name;
    @Size(max = 200)
    private String description;
    @IsAfterCinemaBirthday
    private LocalDate releaseDate;
    @Positive
    private int duration;

}
