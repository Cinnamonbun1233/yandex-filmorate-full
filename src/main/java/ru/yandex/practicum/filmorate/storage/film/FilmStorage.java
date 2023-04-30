package ru.yandex.practicum.filmorate.storage.film;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;

public interface FilmStorage {

    // FILMS - CRUD
    Film addFilm(Film film);

    Film updateFilm(Film film);

    Film getFilm(Long id);

    List<Film> getFilms();

    // FILMS - Checking
    boolean hasTwin(Film film);

    List<Film> getMostPopularFilms(Integer count);

    List<Film> getFilmsByDirector(Long directorId, String sortBy);

    // RESET STORAGE
    void deleteAllData();

    List<Film> search(String query, String[] by);
}
