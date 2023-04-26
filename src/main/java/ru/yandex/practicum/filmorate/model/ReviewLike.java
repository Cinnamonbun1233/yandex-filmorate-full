package ru.yandex.practicum.filmorate.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

@Data
@AllArgsConstructor
public class ReviewLike {
    @Positive
    private long reviewId;
    @Positive
    private long userId;
    @NotNull
    private String type;
}
