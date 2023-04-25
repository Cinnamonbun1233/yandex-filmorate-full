package ru.yandex.practicum.filmorate.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Review {
    private long reviewId;
    private String content;
    private boolean isPositive;
    private long userId;
    private long filmId;
    private int useful;
}
