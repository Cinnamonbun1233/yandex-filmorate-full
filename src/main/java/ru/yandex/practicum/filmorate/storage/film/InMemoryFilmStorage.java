package ru.yandex.practicum.filmorate.storage.film;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.*;
import java.util.stream.Collectors;

@Component
public class InMemoryFilmStorage implements FilmStorage {

    private Long filmId = 0L;
    private final Map<Long, Film> idFilmMap = new HashMap<>();              // k - filmId, v - film
    private final Map<Long, Set<Long>> likesMap = new HashMap<>();          // k - filmId, v - set of userId
    private final Comparator<Long> filmComparator = ((filmId1, filmId2) ->
            likesMap.getOrDefault(filmId2, Collections.EMPTY_SET).size()
                    - likesMap.getOrDefault(filmId1, Collections.EMPTY_SET).size());
    private final TreeSet<Long> filmsSet = new TreeSet<>(filmComparator);    // films ids sorted by popularity


    // FILMS - CRUD
    @Override
    public Film addFilm(Film film) {

        Long currentId = getFilmId();
        film.setId(currentId);

        idFilmMap.put(currentId, film);
        filmsSet.add(currentId);

        return film;

    }

    @Override
    public Film updateFilm(Film film) {

        idFilmMap.put(film.getId(), film);
        return film;

    }

    @Override
    public Film getFilm(Long id) {

        return idFilmMap.get(id);

    }

    @Override
    public Set<Film> getFilms() {

        return new HashSet<>(idFilmMap.values());

    }


    // FILMS - Checking
    @Override
    public boolean hasFilm(Film film){

        return getFilms().contains(film);

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
    public Set<Film> getMostPopularFilms(Integer count) {

        return filmsSet.stream().limit(count).map(this::getFilm).collect(Collectors.toSet());

    }


    // LIKES - Checking
    @Override
    public boolean hasLike(Long filmId, Long userId) {

        return likesMap.getOrDefault(filmId, Collections.EMPTY_SET).contains(userId);

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
    private Long getFilmId(){

        return ++filmId;

    }

}