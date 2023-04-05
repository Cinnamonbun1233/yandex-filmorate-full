package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ResourceHasATwinException;
import ru.yandex.practicum.filmorate.exception.ResourceNotFoundException;
import ru.yandex.practicum.filmorate.model.*;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import javax.annotation.PostConstruct;
import java.util.*;
import java.util.stream.Collectors;


@Service
public class FilmService {

    private final FilmStorage filmStorage;
    private final UserStorage userStorage;

    @Autowired
    public FilmService(FilmStorage filmStorage, @Qualifier("userDbStorage") UserStorage userStorage) {
        this.filmStorage = filmStorage;
        this.userStorage = userStorage;
    }

    // FILMS
    public Film addFilm(Film film) {

        // checking
        filmIsATwin(film);
        filmHasUnknownGenres(film);
        filmHasUnknownMpa(film);

        // fix
        fixMpa(film);
        fixGenres(film);

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
        filmIsATwin(film);
        filmHasUnknownGenres(film);
        filmHasUnknownMpa(film);

        // fix
        fixMpa(film);
        fixGenres(film);

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
            throw new ResourceNotFoundException(String.format("Like of user %d to film %d not found", filmId, userId));
        }

        // delete like
        boolean likeDeleted = filmStorage.deleteLike(filmId, userId);
        assert likeDeleted;

    }

    public List<Film> getMostPopularFilms(Integer count) {

        return filmStorage.getMostPopularFilms(count);

    }


    // GENRE
    public Genre addGenre(Genre genre) {

        // checking
        boolean genreIsTwin = filmStorage.hasGenre(genre);
        if (genreIsTwin) {
            throw new ResourceHasATwinException("Genre has a twin");
        }

        // add genre
        Genre result = filmStorage.addGenre(genre);
        assert result != null;
        return result;

    }

    public Genre updateGenre(Genre genre) {

        // checking
        Long id = genre.getId();
        boolean genreIdNotFound = (filmStorage.getGenre(id) == null);
        if (genreIdNotFound) {
            throw new ResourceNotFoundException("Genre", id);
        }

        boolean genreIsTwin = filmStorage.hasGenre(genre);
        if (genreIsTwin) {
            throw new ResourceHasATwinException("Genre has a twin");
        }

        // update genre
        Genre result = filmStorage.updateGenre(genre);
        assert result != null;
        return result;

    }

    public Genre getGenre(Long id) {

        // checking
        Genre genre = filmStorage.getGenre(id);
        if (genre == null) {
            throw new ResourceNotFoundException("Genre", id);
        }

        // get genre
        return genre;

    }

    public Set<Genre> getGenres() {

        return filmStorage.getGenres();

    }


    // MPA
    public Set<Mpa> getMpas() {

        TreeSet<Mpa> res = new TreeSet<>(Comparator.comparingLong(Mpa::getId));
        res.addAll(Mpa.getMpas());
        return res;

    }

    public Mpa getMpa(Long id) {

        // checking
        Mpa mpa = Mpa.getMpa(id);
        if (mpa == null) {
            throw new ResourceNotFoundException("Mpa", id);
        }

        // get genre
        return mpa;

    }


    // PRIVATE


    private void filmIsATwin(Film film) {

        boolean filmIsTwin = filmStorage.hasTwin(film);
        if (filmIsTwin) {
            throw new ResourceHasATwinException("Film has a twin");
        }

    }

    private void filmHasUnknownGenres(Film film) {

        Set<Genre> filmGenres = film.getGenres();

        if (filmGenres != null && filmGenres.size() > 0) {
            Set<Long> genresIds = filmGenres.stream().map(Genre::getId).collect(Collectors.toSet());
            Set<Long> unknownGenreIds = filmStorage.getUnknownGenreIds(genresIds);
            if (unknownGenreIds.size() > 0) {
                String unknownGenres = unknownGenreIds.stream().map(String::valueOf).collect(Collectors.joining(" ,"));
                throw new ResourceNotFoundException("Genre with id " + unknownGenres + " not found");
            }
        }

    }

    private void filmHasUnknownMpa(Film film) {

        Mpa mpa = film.getMpa();
        if (mpa == null) {
            return;
        }
        Long mpaId = mpa.getId();
        if (Mpa.getMpa(mpaId) == null) {
            throw new ResourceNotFoundException("MPA with id " + mpaId + " not found");
        }

    }

    private void fixMpa(Film film) {
        film.setMpa(Mpa.getMpa(film.getMpa()));
    }

    private void fixGenres(Film film) {

        if (film.getGenres() == null) {
            film.setGenres(new TreeSet<>());
        }

    }

    @PostConstruct
    @SuppressWarnings("unused")
    private void setUpGenres() {

        if (filmStorage.getGenre(1L) == null) {
            addGenre(Genre.builder().name("Комедия").build());
            addGenre(Genre.builder().name("Драма").build());
            addGenre(Genre.builder().name("Мультфильм").build());
            addGenre(Genre.builder().name("Триллер").build());
            addGenre(Genre.builder().name("Документальный").build());
            addGenre(Genre.builder().name("Боевик").build());
        }

    }

}