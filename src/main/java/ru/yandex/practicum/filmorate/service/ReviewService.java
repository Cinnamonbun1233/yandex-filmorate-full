package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.review.ReviewDaoImpl;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ReviewService {
    private final ReviewDaoImpl reviewDao;
    private final UserService userService;

    public void addLike(long reviewId, long userId) {
        User user = userService.getUser(userId);
        Review review = reviewDao.getReviewById(reviewId);
        reviewDao.addLike(reviewId, userId);
    }
    public void addDisLike(long reviewId, long userId) {
        User user = userService.getUser(userId);
        Review review = reviewDao.getReviewById(reviewId);
        reviewDao.addDisLike(reviewId, userId);
    }
    public void deleteDisLike(long reviewId, long userId) {
        User user = userService.getUser(userId);
        Review review = reviewDao.getReviewById(reviewId);
        reviewDao.deleteDislike(reviewId, userId);
    }
    public void deleteLike(long reviewId, long userId) {
        User user = userService.getUser(userId);
        Review review = reviewDao.getReviewById(reviewId);
        reviewDao.deleteLike(reviewId, userId);
    }

    public Review create(Review review) {
        return reviewDao.create(review);
    }

    public Review update(Review review) {
        return reviewDao.update(review);
    }

    public void delete(long id) {
        reviewDao.delete(id);
    }

    public Review getReviewById(long id) {
        return reviewDao.getReviewById(id);
    }

    public List<Review> getReviews(long filmId, int count) {
        return reviewDao.getReviews(filmId, count);
    }
}
