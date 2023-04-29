package ru.yandex.practicum.filmorate.storage.review;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.LikeType;
import ru.yandex.practicum.filmorate.model.ReviewLike;

import java.sql.ResultSet;
import java.sql.SQLException;

@Component
@RequiredArgsConstructor
public class ReviewLikeDbStorage implements ReviewLikeStorage {
    private final JdbcTemplate jdbcTemplate;

    public void addLike(long reviewId, long userId, LikeType type) {
        String sql = String.format("INSERT INTO review_like VALUES(?,?,'%s')", type);
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

    public void deleteLike(long reviewId, long userId, LikeType type) {
        String sql = String.format("DELETE FROM review_like WHERE review_id = ? AND user_id = ? AND type = '%s'", type);
        jdbcTemplate.update(sql, reviewId, userId);
    }

    private ReviewLike makeReviewLike(ResultSet rs) throws SQLException {
        return new ReviewLike(rs.getInt("review_id"),
                rs.getInt("user_id"), LikeType.valueOf(rs.getString("type")));
    }
}
