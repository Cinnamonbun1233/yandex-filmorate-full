package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ResourceHasATwinException;
import ru.yandex.practicum.filmorate.exception.ResourceNotFoundException;
import ru.yandex.practicum.filmorate.model.LikeType;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.model.ReviewLike;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.review.ReviewLikeStorage;
import ru.yandex.practicum.filmorate.storage.review.ReviewStorage;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReviewService {
    private final ReviewStorage reviewDbStorage;
    private final UserService userService;
    private final FilmService filmService;
    private final ReviewLikeStorage reviewLikeDbStorage;

    public void addLike(long reviewId, long userId, LikeType type) {
        User user = userService.getUser(userId);
        Review review = getReviewById(reviewId);
        ReviewLike like = reviewLikeDbStorage.getLike(review.getReviewId(), user.getId());
        if (like != null && type.equals(like.getType())) {
            throw new ResourceHasATwinException(String.format("Пользователь с id - %s уже ставил %s отзыву с id - %s",
                    userId, type.name(), reviewId));
        } else if (like != null && !type.equals(like.getType())) {
            reviewLikeDbStorage.deleteLike(reviewId, userId, like.getType());
        }
        reviewLikeDbStorage.addLike(reviewId, userId, type);

        int operation;
        if (type.equals(LikeType.LIKE)) {
            operation = 1;
        } else {
            operation = -1;
        }
        reviewDbStorage.changeUseful(reviewId, userId, operation);
    }

    public Review create(Review review) {
        userService.getUser(review.getUserId());
        filmService.getFilm(review.getFilmId());
        return reviewDbStorage.create(review);
    }

    public Review getReviewById(long id) {
        Review review = reviewDbStorage.getReviewById(id);
        if (review != null) {
            return review;
        } else {
            throw new ResourceNotFoundException("review", id);
        }
    }

    public List<Review> getReviews(Long filmId, int count) {
        return reviewDbStorage.getReviews(filmId, count);
    }

    public void deleteLike(long reviewId, long userId, LikeType type) {
        User user = userService.getUser(userId);
        Review review = getReviewById(reviewId);
        reviewLikeDbStorage.deleteLike(review.getReviewId(), user.getId(), type);
    }

    public Review update(Review review) {
        return reviewDbStorage.update(review);
    }

    public void delete(long id) {
        reviewDbStorage.delete(id);
    }
}
