package ru.yandex.practicum.filmorate.storage.film;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;

public interface FilmStorage {

    // FILMS - CRUD
    Film addFilm(Film film);

    Film updateFilm(Film film);

    Film getFilm(Long id);

    List<Film> getFilms();

    List<Film> getFilms(List<Long> filmIds);

    void deleteFilmById(Long filmId);

    // FILMS - Checking
    boolean hasTwin(Film film);

    public List<Film> getCommonFilmsWithFriend(Long userId, Long friendId);

    List<Film> getFilmsByDirector(Long directorId, String sortBy);

    List<Film> getMostPopularFilms(Integer count, Integer genre, Integer year);

    // RESET STORAGE
    void deleteAllData();

}
