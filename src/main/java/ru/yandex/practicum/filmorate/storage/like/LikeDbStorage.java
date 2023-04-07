package ru.yandex.practicum.filmorate.storage.like;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class LikeDbStorage implements LikeStorage {

    private final JdbcTemplate jdbcTemplate;

    // LIKES - CRUD
    @Override
    public void addLike(Long filmId, Long userId) {

        String sqlQuery = "INSERT INTO filmorate_like (film_id, user_id) VALUES (?, ?)";
        try {
            jdbcTemplate.update(sqlQuery, filmId, userId);
        } catch (DuplicateKeyException e) {
            // Do nothing. Adding like is idempotent
        }

    }

    @Override
    public boolean deleteLike(Long filmId, Long userId) {

        String sqlQuery = "DELETE FROM filmorate_like WHERE film_id = ? AND user_id = ?";
        return jdbcTemplate.update(sqlQuery, filmId, userId) > 0;

    }


    // LIKES - Checking
    @Override
    public boolean hasLike(Long filmId, Long userId) {

        String sql = "SELECT count(*) FROM filmorate_like WHERE film_id = ? AND user_id = ?";
        int count = jdbcTemplate.queryForObject(sql, Integer.class, filmId, userId);
        return (count > 0);

    }

}
