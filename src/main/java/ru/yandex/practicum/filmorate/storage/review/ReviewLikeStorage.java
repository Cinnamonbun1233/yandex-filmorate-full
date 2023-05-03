package ru.yandex.practicum.filmorate.storage.review;

import ru.yandex.practicum.filmorate.model.LikeType;
import ru.yandex.practicum.filmorate.model.ReviewLike;

public interface ReviewLikeStorage {
    void addLike(long reviewId, long userId, LikeType type);

    ReviewLike getLike(long reviewId, long userId);

    void deleteLike(long reviewId, long userId, LikeType type);
}
