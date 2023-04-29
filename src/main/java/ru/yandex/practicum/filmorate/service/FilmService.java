package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ResourceHasATwinException;
import ru.yandex.practicum.filmorate.exception.ResourceNotFoundException;
import ru.yandex.practicum.filmorate.model.*;
import ru.yandex.practicum.filmorate.storage.director.DirectorStorage;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.genre.GenreStorage;
import ru.yandex.practicum.filmorate.storage.like.LikeStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import javax.annotation.PostConstruct;
import java.util.*;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class FilmService {

    private final FilmStorage filmStorage;
    private final UserStorage userStorage;
    private final GenreStorage genreStorage;
    private final LikeStorage likeStorage;
    private final DirectorStorage directorStorage;

    // FILMS
    public Film addFilm(Film film) {

        // checking
        filmIsATwin(film);
        filmHasUnknownGenres(film);
        filmHasUnknownMpa(film);
        filmHasUnknownDirector(film);

        // fix
        fixMpa(film);
        fixGenres(film);
        fixDirectors(film);

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
        filmHasUnknownDirector(film);

        // fix
        fixMpa(film);
        fixGenres(film);
        fixDirectors(film);

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

    public List<Film> getFilms() {

        return filmStorage.getFilms();

    }

    public List<Film> getFilmsByDirector(Long directorId, String sortBy) {

        // checking
        getDirector(directorId);

        // get films
        return filmStorage.getFilmsByDirector(directorId, sortBy);

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
        likeStorage.addLike(filmId, userId);

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

        boolean hasLike = likeStorage.hasLike(filmId, userId);
        if (!hasLike) {
            throw new ResourceNotFoundException(String.format("Like of user %d to film %d not found", filmId, userId));
        }

        // delete like
        boolean likeDeleted = likeStorage.deleteLike(filmId, userId);
        assert likeDeleted;

    }

    public List<Film> getMostPopularFilms(Integer count, Integer genreId, Integer year) {
        return filmStorage.getMostPopularFilms(count, genreId, year);
    }

    // GENRE
    public Genre addGenre(Genre genre) {

        // checking
        boolean genreIsTwin = genreStorage.hasGenre(genre);
        if (genreIsTwin) {
            throw new ResourceHasATwinException("Genre has a twin");
        }

        // add genre
        Genre result = genreStorage.addGenre(genre);
        assert result != null;
        return result;

    }

    public Genre updateGenre(Genre genre) {

        // checking
        Long id = genre.getId();
        boolean genreIdNotFound = (genreStorage.getGenre(id) == null);
        if (genreIdNotFound) {
            throw new ResourceNotFoundException("Genre", id);
        }

        boolean genreIsTwin = genreStorage.hasGenre(genre);
        if (genreIsTwin) {
            throw new ResourceHasATwinException("Genre has a twin");
        }

        // update genre
        Genre result = genreStorage.updateGenre(genre);
        assert result != null;
        return result;

    }

    public Genre getGenre(Long id) {

        // checking
        Genre genre = genreStorage.getGenre(id);
        if (genre == null) {
            throw new ResourceNotFoundException("Genre", id);
        }

        // get genre
        return genre;

    }

    public List<Genre> getGenres() {

        return genreStorage.getGenres();

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


    // DIRECTOR
    public Director addDirector(Director director) {

        // checking
        directorIsATwin(director);

        // add director
        Director result = directorStorage.addDirector(director);
        assert result != null;
        return result;

    }

    public Director updateDirector(Director director) {

        // checking
        Long id = director.getId();
        boolean directorIdNotFound = (directorStorage.getDirector(id) == null);
        if (directorIdNotFound) {
            throw new ResourceNotFoundException("Director", id);
        }
        directorIsATwin(director);

        // update director
        Director result = directorStorage.updateDirector(director);
        assert result != null;
        return result;


    }

    public Director getDirector(Long id) {

        // checking
        Director director = directorStorage.getDirector(id);
        if (director == null) {
            throw new ResourceNotFoundException("Director", id);
        }

        // get film
        return director;

    }

    public List<Director> getDirectors() {

        return directorStorage.getDirectors();

    }

    public void deleteDirector(Long id) {
        directorStorage.deleteDirector(id);

    }


    // PRIVATE

    private void filmIsATwin(Film film) {

        boolean filmIsTwin = filmStorage.hasTwin(film);
        if (filmIsTwin) {
            throw new ResourceHasATwinException("Film has a twin");
        }

    }

    private void filmHasUnknownGenres(Film film) {

        List<Genre> filmGenres = film.getGenres();

        if (filmGenres != null && filmGenres.size() > 0) {
            Set<Long> genresIds = filmGenres.stream().map(Genre::getId).collect(Collectors.toSet());
            Set<Long> unknownGenreIds = genreStorage.getUnknownGenreIds(genresIds);
            if (unknownGenreIds.size() > 0) {
                String unknownGenres = unknownGenreIds.stream().map(String::valueOf).collect(Collectors.joining(" ,"));
                throw new ResourceNotFoundException("Genre with id " + unknownGenres + " not found");
            }
        }

    }

    private void filmHasUnknownMpa(Film film) {

        Mpa mpa = film.getMpa();

        Long mpaId = mpa.getId();
        if (Mpa.getMpa(mpaId) == null) {
            throw new ResourceNotFoundException("MPA with id " + mpaId + " not found");
        }

    }

    private void filmHasUnknownDirector(Film film) {

        List<Director> filmDirectors = film.getDirectors();

        if (filmDirectors != null && filmDirectors.size() > 0) {
            Set<Long> directorIds = filmDirectors.stream().map(Director::getId).collect(Collectors.toSet());
            Set<Long> unknownDirectorsIds = directorStorage.getUnknownDirectorIds(directorIds);
            if (unknownDirectorsIds.size() > 0) {
                String unknownGenres = unknownDirectorsIds.stream().map(String::valueOf).collect(Collectors.joining(" ,"));
                throw new ResourceNotFoundException("Director with id " + unknownGenres + " not found");
            }
        }

    }

    private void fixMpa(Film film) {
        film.setMpa(Mpa.getMpa(film.getMpa()));
    }

    private void fixGenres(Film film) {

        if (film.getGenres() == null) {
            film.setGenres(Collections.EMPTY_LIST);
        }

        List<Genre> geresWithoutDuplicates = film.getGenres()
                .stream()
                .distinct()
                .collect(Collectors.toList());
        film.setGenres(geresWithoutDuplicates);

    }

    private void fixDirectors(Film film) {

        if (film.getDirectors() == null) {
            film.setDirectors(Collections.EMPTY_LIST);
        }

        List<Director> directorsWithoutDuplicates = film.getDirectors()
                .stream()
                .distinct()
                .collect(Collectors.toList());
        film.setDirectors(directorsWithoutDuplicates);

    }

    private void directorIsATwin(Director director) {

        boolean directorIsTwin = directorStorage.hasTwin(director);
        if (directorIsTwin) {
            throw new ResourceHasATwinException("Director has a twin");
        }

    }

    @PostConstruct
    @SuppressWarnings("unused")
    private void setUpGenres() {

        if (genreStorage.getGenre(1L) == null) {
            addGenre(Genre.builder().name("Комедия").build());
            addGenre(Genre.builder().name("Драма").build());
            addGenre(Genre.builder().name("Мультфильм").build());
            addGenre(Genre.builder().name("Триллер").build());
            addGenre(Genre.builder().name("Документальный").build());
            addGenre(Genre.builder().name("Боевик").build());
        }

    }

}