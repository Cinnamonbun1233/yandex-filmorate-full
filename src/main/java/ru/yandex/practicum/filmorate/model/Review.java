package ru.yandex.practicum.filmorate.model;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

@Data
@Builder
public class Review {
    private long reviewId;
    @NotBlank
    private String content;
    @NotNull
    private Boolean isPositive;
    @Positive
    private long userId;
    @Positive
    private long filmId;
    private int useful;
}
