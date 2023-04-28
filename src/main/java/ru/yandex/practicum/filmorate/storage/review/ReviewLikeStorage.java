package ru.yandex.practicum.filmorate.storage.review;

import ru.yandex.practicum.filmorate.model.ReviewLike;

public interface ReviewLikeStorage {
    void addLike(long reviewId, long userId, String type);

    ReviewLike getLike(long reviewId, long userId);

    void deleteLike(long reviewId, long userId, String type);
}
