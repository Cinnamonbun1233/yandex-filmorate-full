package ru.yandex.practicum.filmorate.storage.like;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.FilmService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    @Override
    public Map<Long, HashMap<Long, Double>> getLikesMatrix() {

        String sqlQuery = "SELECT FILM_ID, USER_ID FROM filmorate_like";
        SqlRowSet sqlRowSet = jdbcTemplate.queryForRowSet(sqlQuery);

        Map<Long, HashMap<Long, Double>> res = new HashMap<>();

        while (sqlRowSet.next()) {
            Long filmId = sqlRowSet.getLong("film_id");
            Long userId = sqlRowSet.getLong("user_id");
            res.computeIfAbsent(userId, v -> new HashMap<>()).put(filmId, 1D);
        }

        return res;
    }


    // LIKES - Checking
    @Override
    public boolean hasLike(Long filmId, Long userId) {

        String sql = "SELECT count(*) FROM filmorate_like WHERE film_id = ? AND user_id = ?";
        int count = jdbcTemplate.queryForObject(sql, Integer.class, filmId, userId);
        return (count > 0);

    }

}
