package ru.yandex.practicum.filmorate.storage.film;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.Set;

public interface FilmStorage {

    // FILMS - CRUD
    Film addFilm(Film film);
    Film updateFilm(Film film);
    Film getFilm(Long id);
    Set<Film> getFilms();

    // FILMS - Checking
    boolean hasFilm(Film film);


    // LIKES - CRUD
    void addLike(Long filmId, Long userId);
    boolean deleteLike(Long filmId, Long userId);
    Set<Film> getMostPopularFilms(Integer count);

    // LIKES - Checking
    boolean hasLike(Long filmId, Long userId);

    // RESET STORAGE
    void deleteAllData();

}
