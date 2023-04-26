package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.AlreadyLikedException;
import ru.yandex.practicum.filmorate.exception.ResourceNotFoundException;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.model.ReviewLike;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.review.ReviewDbStorage;
import ru.yandex.practicum.filmorate.storage.review.ReviewLikeDbStorage;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReviewService {
    private final ReviewDbStorage reviewDbStorage;
    private final UserService userService;
    private final ReviewLikeDbStorage reviewLikeDbStorage;

    public void addLike(long reviewId, long userId) {
        User user = userService.getUser(userId);
        Review review = getReviewById(reviewId);

        ReviewLike like = reviewLikeDbStorage.getLike(review.getReviewId(), user.getId());
        if (like != null && "LIKE".equalsIgnoreCase(like.getType())) {
            throw new AlreadyLikedException(String.format("Пользователь с id - %s уже ставил лайк отзыву с id - %s",
                    userId, reviewId));
        } else if (like != null && "DISLIKE".equalsIgnoreCase(like.getType())) {
            reviewLikeDbStorage.deleteDislike(reviewId, userId);
        }
        reviewLikeDbStorage.addLike(reviewId, userId);
        reviewDbStorage.changeUseful(reviewId, userId, 1);

        log.info("Пользователь c id {} поставил отзыву с id {} лайк", userId, reviewId);
    }

    public void addDisLike(long reviewId, long userId) {
        User user = userService.getUser(userId);
        Review review = getReviewById(reviewId);
        ReviewLike like = reviewLikeDbStorage.getLike(reviewId, userId);
        if (like != null && "DISLIKE".equalsIgnoreCase(like.getType())) {
            throw new AlreadyLikedException(String.format("Пользователь с id - %s уже ставил дизлайк отзыву с id - %s",
                    userId, reviewId));
        } else if (like != null && "LIKE".equalsIgnoreCase(like.getType())) {
            deleteLike(reviewId, userId);
        }
        reviewLikeDbStorage.addDislike(reviewId, userId);
        reviewDbStorage.changeUseful(reviewId, userId, -1);

        log.info("Пользователь c id {} поставил отзыву с id {} дизлайк", userId, reviewId);
    }

    public void deleteDisLike(long reviewId, long userId) {
        User user = userService.getUser(userId);
        Review review = getReviewById(reviewId);
        reviewLikeDbStorage.deleteDislike(reviewId, userId);
    }

    public void deleteLike(long reviewId, long userId) {
        User user = userService.getUser(userId);
        Review review = getReviewById(reviewId);
        reviewLikeDbStorage.deleteLike(review.getReviewId(), user.getId());
    }

    public Review create(Review review) {
        return reviewDbStorage.create(review);
    }

    public Review update(Review review) {
        return reviewDbStorage.update(review);
    }

    public void delete(long id) {
        reviewDbStorage.delete(id);
    }

    public Review getReviewById(long id) {
        Review review = reviewDbStorage.getReviewById(id);
        if (review != null) {
            return review;
        } else {
            throw new ResourceNotFoundException("review", id);
        }
    }

    public List<Review> getReviews(long filmId, int count) {
        return reviewDbStorage.getReviews(filmId, count);
    }
}
