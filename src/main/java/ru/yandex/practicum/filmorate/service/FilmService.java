package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.FilmHasATwinException;
import ru.yandex.practicum.filmorate.exception.ResourceNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.*;

@Service
public class FilmService {

    @Autowired
    FilmStorage filmStorage;
    @Autowired
    UserStorage userStorage;

    // FILMS
    public Film addFilm(Film film) {

        // checking
        boolean filmIsTwin = filmStorage.hasFilm(film);
        if (filmIsTwin) {
            throw new FilmHasATwinException("Film has a twin");
        }

        // add film
        Film result = filmStorage.addFilm(film);
        assert result != null;
        return result;

    }

    public Film updateFilm(Film film) {

        // checking
        Long id = film.getId();
        boolean filmIdNotFound = (filmStorage.getFilm(id) == null);
        if (filmIdNotFound) {
            throw new ResourceNotFoundException("Film", id);
        }

        boolean filmIsTwin = filmStorage.hasFilm(film);
        if (filmIsTwin) {
            throw new FilmHasATwinException("Film has a twin");
        }

        // update film
        Film result = filmStorage.updateFilm(film);
        assert result != null;
        return result;

    }

    public Film getFilm(Long id) {

        // checking
        Film film = filmStorage.getFilm(id);
        if (film == null) {
            throw new ResourceNotFoundException("Film", id);
        }

        // get film
        return film;

    }

    public Set<Film> getFilms() {

        return filmStorage.getFilms();

    }


    // LIKES
    public void addLike(Long filmId, Long userId) {

        // checking
        Film film = filmStorage.getFilm(filmId);
        User user = userStorage.getUser(userId);

        Map<String, Long> notFoundResources = new HashMap<>();
        if (film == null) {
            notFoundResources.put("Film", filmId);
        }
        if (user == null) {
            notFoundResources.put("User", userId);
        }
        if (notFoundResources.size() > 0) {
            throw new ResourceNotFoundException(notFoundResources);
        }

        // add like
        filmStorage.addLike(filmId, userId);

    }

    public void deleteLike(Long filmId, Long userId) {

        // checking
        Film film = filmStorage.getFilm(filmId);
        User user = userStorage.getUser(userId);

        Map<String, Long> notFoundResources = new HashMap<>();
        if (film == null) {
            notFoundResources.put("Film", filmId);
        }
        if (user == null) {
            notFoundResources.put("User", userId);
        }
        if (notFoundResources.size() > 0) {
            throw new ResourceNotFoundException(notFoundResources);
        }

        boolean hasLike = filmStorage.hasLike(filmId, userId);
        if (!hasLike) {
            throw new ResourceNotFoundException(String.format("Like of user %d to film %d not found", filmId ,userId));
        }

        // delete like
        boolean likeDeleted = filmStorage.deleteLike(filmId, userId);
        assert likeDeleted;

    }

    public Set<Film> getMostPopularFilms(Integer count) {

        return filmStorage.getMostPopularFilms(count);

    }

}
