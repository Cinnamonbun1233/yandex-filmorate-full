package ru.yandex.practicum.filmorate.storage.like;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


public interface LikeStorage {

    // CRUD
    void addLike(Long filmId, Long userId);

    boolean deleteLike(Long filmId, Long userId);

    Map<Long, HashMap<Long, Double>> getLikesMatrix();

    // Checking
    boolean hasLike(Long filmId, Long userId);

    List<Film> sortFilmsByLikes(List<Film> filmList);

}
