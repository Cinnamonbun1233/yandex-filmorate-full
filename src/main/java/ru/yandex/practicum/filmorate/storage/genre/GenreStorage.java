package ru.yandex.practicum.filmorate.storage.genre;

import ru.yandex.practicum.filmorate.model.Genre;

import java.util.List;
import java.util.Set;

public interface GenreStorage {

    Genre addGenre(Genre genre);

    Genre updateGenre(Genre genre);

    Genre getGenre(Long id);

    List<Genre> getGenres();

    // GENRES - Checking
    boolean hasGenre(Genre genre);

    Set<Long> getUnknownGenreIds(Set<Long> genreIds);


}
