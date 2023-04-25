package ru.yandex.practicum.filmorate.storage.review;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.AlreadyLikedException;
import ru.yandex.practicum.filmorate.exception.ReviewNotFoundException;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.model.ReviewLike;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
@Component
@Slf4j
public class ReviewDaoImpl {
    private final JdbcTemplate jdbcTemplate;
    private final ReviewLikeDaoImpl reviewLikeDao;
    private final ObjectMapper mapper = new ObjectMapper().findAndRegisterModules();

    public void addLike(long reviewId, long userId) {
        ReviewLike like = reviewLikeDao.getLike(reviewId, userId);
        if (like != null && "LIKE".equalsIgnoreCase(like.getType())) {
            throw new AlreadyLikedException(String.format("Пользователь с id - %s уже ставил лайк отзыву с id - %s",
                    userId, reviewId));
        } else if (like != null && "DISLIKE".equalsIgnoreCase(like.getType())) {
            deleteDislike(reviewId, userId);
        }
        reviewLikeDao.addLike(reviewId, userId);
        changeUseful(reviewId, userId, 1);

        log.info("Пользователь c id {} поставил отзыву с id {} лайк", userId, reviewId);
    }

    private void changeUseful(long reviewId, long userId, int operation) {
        String sql = "UPDATE reviews SET rating = rating + ? WHERE id = ?";
        jdbcTemplate.update(sql, operation, reviewId);
    }

    public void addDisLike(long reviewId, long userId) {
        ReviewLike like = reviewLikeDao.getLike(reviewId, userId);
        if (like != null && "DISLIKE".equalsIgnoreCase(like.getType())) {
            throw new AlreadyLikedException(String.format("Пользователь с id - %s уже ставил дизлайк отзыву с id - %s",
                    userId, reviewId));
        } else if (like != null && "LIKE".equalsIgnoreCase(like.getType())) {
            deleteLike(reviewId, userId);
        }
        reviewLikeDao.addDislike(reviewId, userId);
        changeUseful(reviewId, userId, -1);

        log.info("Пользователь c id {} поставил отзыву с id {} дизлайк", userId, reviewId);
    }

    public void deleteDislike(long reviewId, long userId) {
        reviewLikeDao.deleteDislike(reviewId, userId);
    }

    public void deleteLike(long reviewId, long userId) {
        reviewLikeDao.deleteLike(reviewId, userId);
    }

    public List<Review> getReviews(long filmId, int count) {
        String sql;
        if (filmId == 0) {
            sql = "SELECT * FROM reviews ORDER BY rating LIMIT ?";
            return jdbcTemplate.query(sql, (rs, rowNum) -> makeReview(rs), count);
        } else {
            sql = "SELECT * FROM reviews WHERE film_id=? ORDER BY rating LIMIT ?";
            return jdbcTemplate.query(sql, (rs, rowNum) -> makeReview(rs), filmId, count);
        }
    }

    private Review makeReview(ResultSet rs) throws SQLException {
        long id = rs.getInt("id");
        return Review.builder()
                .reviewId(id)
                .content(rs.getString("content"))
                .isPositive(rs.getBoolean("type"))
                .userId(rs.getInt("user_id"))
                .filmId(rs.getInt("film_id"))
                .useful(rs.getInt("rating"))
                .build();
    }

    @SneakyThrows
    public Review create(Review review) {
        SimpleJdbcInsert jdbcInsert = new SimpleJdbcInsert(jdbcTemplate).withTableName("reviews")
                .usingGeneratedKeyColumns("id");
        long id = jdbcInsert.executeAndReturnKey(Map.of("content", review.getContent(),
                "type", review.isPositive(),
                "user_id", review.getUserId(),
                "film_id", review.getFilmId(),
                "rating", review.getUseful())).longValue();
        review.setReviewId(id);
        log.info("Создан отзыв - {}", mapper.writeValueAsString(review));
        return review;
    }

    @SneakyThrows
    public Review update(Review review) {
        String sql = "UPDATE reviews SET content=?,type=?,film_id=? WHERE id=?";
        jdbcTemplate.update(sql, review.getContent(), review.isPositive(), review.getFilmId(), review.getReviewId());
        log.info("Обновлен отзыв : {}", mapper.writeValueAsString(review));
        return review;
    }

    public void delete(long id) {
        String sql = "DELETE FROM reviews WHERE id = ?";
        jdbcTemplate.update(sql, id);
    }

    public Review getReviewById(long id) {
        String sql = "SELECT * FROM Reviews WHERE id=?";
        try {
            return jdbcTemplate.queryForObject(sql, (rs, rowNum) -> makeReview(rs), id);
        } catch (EmptyResultDataAccessException ex) {
            throw new ReviewNotFoundException(String.format("Отзыв с id - %s не обнаружен", id));
        }
    }
}
