package ru.yandex.practicum.filmorate.service;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.InMemoryStorage;

import javax.validation.Valid;
import javax.validation.ValidationException;
import java.util.Collection;


@RestController
@RequestMapping("/films")
public class FilmController {

    InMemoryStorage inMemoryStorage = InMemoryStorage.getInstance();

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Film postFilm(@Valid @RequestBody Film film) {

        // no storage checking
        boolean filmIdNotEmpty = film.getId() != null;
        if (filmIdNotEmpty) {
            throw new ResponseStatusException(400, "Film ID must be empty", new ValidationException());
        }

        // storage checking
        boolean filmIsTwin = inMemoryStorage.hasFilm(film);
        if (filmIsTwin) {
            throw new ResponseStatusException(409, "Film has a twin", new ValidationException());
        }

        // add
        Film result = inMemoryStorage.addFilm(film, true);
        assert result != null;
        return result;

    }

    @PutMapping
    @ResponseStatus(HttpStatus.OK)
    public Film putFilm(@RequestBody Film film) {

        // no storage checking
        boolean filmIdEmpty = (film.getId() == null);
        if (filmIdEmpty) {
            throw new ResponseStatusException(400, "Film ID must be not empty", new ValidationException());
        }

        // storage checking
        int id = film.getId();
        boolean filmIdNotFound = (inMemoryStorage.getFilm(id) == null);
        if (filmIdNotFound) {
            throw new ResponseStatusException(404, "ID: " + id + " not found", new ValidationException());
        }

        boolean filmIsTwin= inMemoryStorage.hasFilm(film);
        if (filmIsTwin) {
            throw new ResponseStatusException(409, "Film has a twin", new ValidationException());
        }

        // update
        Film result = inMemoryStorage.updateFilm(film, true);
        assert result != null;
        return film;

    }

    @GetMapping
    public Collection<Film> getFilms() {
        return inMemoryStorage.getFilms();
    }

}
