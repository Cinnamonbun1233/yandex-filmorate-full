package ru.yandex.practicum.filmorate.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.service.ReviewService;

import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@RestController
@RequestMapping("/reviews")
@RequiredArgsConstructor
public class ReviewController {
    private final ReviewService reviewService;

    @PostMapping
    public Review createReview(@RequestBody Review review) {
        return reviewService.create(review);
    }
    @GetMapping
    public List<Review> getReviews(@RequestParam(defaultValue = "0") int filmId, @RequestParam(defaultValue = "10") @PositiveOrZero int count) {
        return reviewService.getReviews(filmId, count);
    }

    @PutMapping("/{id}/like/{userId}")
    public void addLike(@PathVariable("id") int reviewId, @PathVariable int userId) {
        reviewService.addLike(reviewId, userId);
    }

    @PutMapping("/{id}/dislike/{userId}")
    public void addDislike(@PathVariable("id") int reviewId, @PathVariable int userId) {
        reviewService.addDisLike(reviewId, userId);
    }

    @DeleteMapping("/{id}/like/{userId}")
    public void deleteLike(@PathVariable("id") int reviewId, @PathVariable int userId) {
        reviewService.deleteLike(reviewId,userId);
    }
    @DeleteMapping("/{id}/dislike/{userId}")
    public void deleteDisLike(@PathVariable("id") int reviewId, @PathVariable int userId) {
        reviewService.deleteDisLike(reviewId,userId);
    }

    @PutMapping
    public Review updateReview(@RequestBody Review review) {
        return reviewService.update(review);
    }

    @DeleteMapping("/{id}")
    public void deleteReview(@PathVariable int id) {
        reviewService.delete(id);
    }

    @GetMapping("/{id}")
    public Review getReviewById(@PathVariable int id) {
        return reviewService.getReviewById(id);
    }
}
