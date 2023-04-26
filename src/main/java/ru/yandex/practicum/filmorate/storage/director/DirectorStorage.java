package ru.yandex.practicum.filmorate.storage.director;

import ru.yandex.practicum.filmorate.model.Director;

import java.util.List;
import java.util.Set;

public interface DirectorStorage {

    Director addDirector(Director director);

    boolean hasTwin(Director director);

    Director getDirector(Long id);
    
    Director updateDirector(Director director);

    List<Director> getDirectors();

    Set<Long> getUnknownDirectorIds(Set<Long> directorsId);

}