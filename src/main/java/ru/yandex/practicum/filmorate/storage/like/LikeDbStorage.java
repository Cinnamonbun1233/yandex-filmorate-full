package ru.yandex.practicum.filmorate.storage.like;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.*;
import java.util.stream.Collectors;

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

    // SEARCH - здесь тот ещё квест был. Т.к. лайки не хранятся в фильмах, пришлось попотеть
    @Override
    public List<Film> sortFilmsByLikes(List<Film> filmList) {
        Map<Film, Integer> map = new HashMap<>();

        // Сперва выясняем у какого фильма сколько лайков было
        for (Film film : filmList) {
            Long filmId = film.getId();
            String sql = "SELECT count(*) FROM filmorate_like WHERE film_id = " + filmId;
            int count = jdbcTemplate.queryForObject(sql, Integer.class);
            map.put(film, count);
        }

        // Затем сортируем мапу по значениям. Сравниваем значения в мапе между собой,
        // получаем упорядоченную по значениям мапу. И всегда получаем правильный ответ
        Map<Film,Integer> result =
                map.entrySet().stream()
                        .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                        .collect(Collectors.toMap(
                                Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));

        return new ArrayList<>(result.keySet());
    }

}
