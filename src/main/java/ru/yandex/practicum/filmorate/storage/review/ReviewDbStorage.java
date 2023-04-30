package ru.yandex.practicum.filmorate.storage.review;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Review;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
@Component
@Slf4j
public class ReviewDbStorage implements ReviewStorage {
    private final JdbcTemplate jdbcTemplate;

    public void changeUseful(long reviewId, long userId, int operation) {
        String sql = "UPDATE REVIEWS SET rating = rating + ? WHERE id = ?";
        jdbcTemplate.update(sql, operation, reviewId);
    }

    @SneakyThrows
    public Review create(Review review) {
        SimpleJdbcInsert jdbcInsert = new SimpleJdbcInsert(jdbcTemplate).withTableName("reviews")
                .usingGeneratedKeyColumns("id");
        long id = jdbcInsert.executeAndReturnKey(Map.of("content", review.getContent(),
                "type", review.getIsPositive(),
                "user_id", review.getUserId(),
                "film_id", review.getFilmId(),
                "rating", review.getUseful())).longValue();
        review.setReviewId(id);
        return review;
    }

    public List<Review> getReviews(Long filmId, int count) {
        String sql;
        if (filmId == null) {
            sql = "SELECT * FROM REVIEWS ORDER BY rating DESC, ID LIMIT ?";
            return jdbcTemplate.query(sql, (rs, rowNum) -> makeReview(rs), count);
        } else {
            sql = "SELECT * FROM REVIEWS WHERE film_id=? ORDER BY rating DESC, ID LIMIT ?";
            return jdbcTemplate.query(sql, (rs, rowNum) -> makeReview(rs), filmId, count);
        }
    }

    public Review getReviewById(long id) {
        String sql = "SELECT * FROM REVIEWS WHERE id=?";
        try {
            return jdbcTemplate.queryForObject(sql, (rs, rowNum) -> makeReview(rs), id);
        } catch (EmptyResultDataAccessException ex) {
            return null;
        }
    }

    @SneakyThrows
    public Review update(Review review) {
        String sql = "UPDATE REVIEWS SET content=?,type=? WHERE id=?";
        jdbcTemplate.update(sql,
                review.getContent(),
                review.getIsPositive(),
                review.getReviewId());
        return getReviewById(review.getReviewId());
    }

    public void delete(long id) {
        String sql = "DELETE FROM REVIEWS WHERE id = ?";
        jdbcTemplate.update(sql, id);
    }

    private Review makeReview(ResultSet rs) throws SQLException {
        long id = rs.getInt("id");
        return Review.builder()
                .reviewId(id)
                .content(rs.getString("content"))
                .isPositive(rs.getBoolean("type"))
                .userId(rs.getLong("user_id"))
                .filmId(rs.getLong("film_id"))
                .useful(rs.getInt("rating"))
                .build();
    }
}
