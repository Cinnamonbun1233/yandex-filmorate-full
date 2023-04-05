package ru.yandex.practicum.filmorate.storage.film;

import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.List;
import java.util.Set;
import java.util.TreeSet;

public interface FilmStorage {

    // FILMS - CRUD
    Film addFilm(Film film);

    Film updateFilm(Film film);

    Film getFilm(Long id);

    TreeSet<Film> getFilms();

    // FILMS - Checking
    boolean hasTwin(Film film);

    // LIKES - CRUD
    void addLike(Long filmId, Long userId);

    boolean deleteLike(Long filmId, Long userId);

    List<Film> getMostPopularFilms(Integer count);

    // LIKES - Checking
    boolean hasLike(Long filmId, Long userId);

    // GENRES - CRUD
    Genre addGenre(Genre genre);

    Genre updateGenre(Genre genre);

    Genre getGenre(Long id);

    Set<Genre> getGenres();

    // GENRES - Checking
    boolean hasGenre(Genre genre);

    Set<Long> getUnknownGenreIds(Set<Long> genreIds);


    // RESET STORAGE
    void deleteAllData();

}
