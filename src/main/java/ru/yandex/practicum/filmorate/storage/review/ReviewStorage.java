package ru.yandex.practicum.filmorate.storage.review;

import ru.yandex.practicum.filmorate.model.Review;

import java.util.List;

public interface ReviewStorage {
 void changeUseful(long reviewId, long userId, int operation);

 List<Review> getReviews(Long filmId, int count);

 Review create(Review review);

 Review update(Review review);

 void delete(long id);

 Review getReviewById(long id);
}
