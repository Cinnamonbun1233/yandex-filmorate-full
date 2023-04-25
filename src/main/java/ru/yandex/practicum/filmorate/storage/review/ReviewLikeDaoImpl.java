package ru.yandex.practicum.filmorate.storage.review;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.model.ReviewLike;

import java.sql.ResultSet;
import java.sql.SQLException;

@Component
@RequiredArgsConstructor
public class ReviewLikeDaoImpl {
    private final JdbcTemplate jdbcTemplate;

    public void addLike(long reviewId, long userId) {
        String sql = "INSERT INTO review_like VALUES(?,?,'LIKE')";
        jdbcTemplate.update(sql, reviewId, userId);
    }

    public ReviewLike getLike(long reviewId, long userId) {
        String sql = "SELECT * FROM review_like WHERE review_id = ? AND user_id = ?";
        try {
            return jdbcTemplate.queryForObject(sql, (rs, rowNum) -> makeReviewLike(rs), reviewId, userId);
        } catch (EmptyResultDataAccessException ex) {
            return null;
        }
    }

    private ReviewLike makeReviewLike(ResultSet rs) throws SQLException {
        return new ReviewLike(rs.getInt("review_id"),
                rs.getInt("user_id"), rs.getString("type"));
    }

    public void addDislike(long reviewId, long userId) {
        String sql = "INSERT INTO review_like VALUES(?,?,'DISLIKE')";
        jdbcTemplate.update(sql, reviewId, userId);
    }

    public void deleteLike(long reviewId, long userId) {
        String sql = "DELETE FROM review_like WHERE review_id = ? AND user_id = ? AND type = 'LIKE'";
        jdbcTemplate.update(sql, reviewId, userId);
    }

    public void deleteDislike(long reviewId, long userId) {
        String sql = "DELETE FROM review_like WHERE review_id = ? AND user_id = ? AND type = 'DISLIKE'";
        jdbcTemplate.update(sql, reviewId, userId);
    }
}
