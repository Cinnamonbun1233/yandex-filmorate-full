package ru.yandex.practicum.filmorate.storage.film;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.*;
import java.util.stream.Collectors;

@Component
public class InMemoryFilmStorage implements FilmStorage {

    private Long filmId = 0L;
    private Long genreId = 0L;
    private final Map<Long, Film> idFilmMap = new HashMap<>();              // k - filmId, v - film
    private final Map<Long, Set<Long>> likesMap = new HashMap<>();          // k - filmId, v - set of userId
    private final Comparator<Long> filmComparator = ((filmId1, filmId2) ->
            likesMap.getOrDefault(filmId2, Collections.EMPTY_SET).size()
                    - likesMap.getOrDefault(filmId1, Collections.EMPTY_SET).size());
    private final TreeSet<Long> filmsSet = new TreeSet<>(filmComparator);    // films ids sorted by popularity
    private final Map<Long, Genre> idGenreMap = new HashMap<>();             // f - genreId, v - genre

    // FILMS - CRUD
    @Override
    public Film addFilm(Film film) {

        changeGenresToValid(film);

        Long currentId = getFilmId();
        film.setId(currentId);

        idFilmMap.put(currentId, film);
        filmsSet.add(currentId);

        return film;

    }

    @Override
    public Film updateFilm(Film film) {

        changeGenresToValid(film);

        idFilmMap.put(film.getId(), film);
        return film;

    }

    @Override
    public Film getFilm(Long id) {

        return idFilmMap.get(id);

    }

    @Override
    public TreeSet<Film> getFilms() {

        return new TreeSet<>(idFilmMap.values());

    }


    // FILMS - Checking
    @Override
    public boolean hasTwin(Film film) {

        if (getFilms().contains(film)) {
            return idFilmMap.get(film.getId()) == null;
        }
        return false;

    }


    // LIKES - CRUD
    @Override
    public void addLike(Long filmId, Long userId) {

        likesMap.computeIfAbsent(filmId, v -> new HashSet<>()).add(userId);
        filmsSet.add(filmId);

    }

    @Override
    public boolean deleteLike(Long filmId, Long userId) {

        Set<Long> filmsLikes = likesMap.get(filmId);
        if (!filmsLikes.remove(userId)) {
            return false;
        }
        if (filmsLikes.size() == 0) {
            likesMap.remove(filmId);
        }

        filmsSet.add(filmId);
        return true;

    }

    @Override
    public List<Film> getMostPopularFilms(Integer count) {

        return filmsSet.stream().limit(count).map(this::getFilm).collect(Collectors.toList());

    }


    // LIKES - Checking
    @Override
    public boolean hasLike(Long filmId, Long userId) {

        return likesMap.getOrDefault(filmId, Collections.EMPTY_SET).contains(userId);

    }


    // GENRES - CRUD
    @Override
    public Genre addGenre(Genre genre) {

        Long currentId = getGenreId();
        genre.setId(currentId);

        idGenreMap.put(currentId, genre);

        return genre;
    }

    @Override
    public Genre updateGenre(Genre genre) {

        idGenreMap.put(genre.getId(), genre);
        return genre;

    }

    @Override
    public Genre getGenre(Long id) {

        return idGenreMap.get(id);

    }

    @Override
    public Set<Genre> getGenres() {

        TreeSet<Genre> res = new TreeSet<>(Comparator.comparingLong(Genre::getId));
        res.addAll(idGenreMap.values());
        return res;

    }


    // GENRES - Checking
    @Override
    public boolean hasGenre(Genre genre) {

        return getGenres().contains(genre);

    }

    @Override
    public Set<Long> getUnknownGenreIds(Set<Long> genreIds) {

        genreIds.removeAll(idGenreMap.keySet());
        return genreIds;

    }


    // RESET STORAGE
    @Override
    public void deleteAllData() {

        filmId = 0L;
        idFilmMap.clear();
        likesMap.clear();
        filmsSet.clear();

    }


    // PRIVATE
    private Long getFilmId() {

        return ++filmId;

    }

    private Long getGenreId() {

        return ++genreId;

    }

    private void changeGenresToValid(Film film) {

        Set<Genre> genres = film.getGenres()
                .stream()
                .map(e -> idGenreMap.get(e.getId()))
                .collect(Collectors.toSet());

        film.setUpGenres(genres);
    }

}