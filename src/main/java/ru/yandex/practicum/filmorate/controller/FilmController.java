package ru.yandex.practicum.filmorate.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import ru.yandex.practicum.filmorate.exception.IncorrectIdException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/films")
@Validated
@SuppressWarnings("unused")
public class FilmController {

    private final FilmService filmService;
    private final String TOP_FILMS_COUNT = "10";

    @Autowired
    public FilmController(FilmService filmService) {
        this.filmService = filmService;
    }

    // FILMS
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Film postFilm(@Valid @RequestBody Film film) {

        // validation
        boolean filmIdNotEmpty = (film.getId() != null);
        if (filmIdNotEmpty) {
            throw new IncorrectIdException("Film ID must be empty");
        }

        // add film
        return filmService.addFilm(film);

    }

    @PutMapping
    @ResponseStatus(HttpStatus.OK)
    public Film putFilm(@RequestBody Film film) {

        // validation
        boolean filmIdEmpty = (film.getId() == null);
        if (filmIdEmpty) {
            throw new IncorrectIdException("Film ID must be not empty");
        }

        // update film
        return filmService.updateFilm(film);

    }

    @GetMapping("/{id}")
    public Film getFilm(@PathVariable @Positive Long id) {

        return filmService.getFilm(id);

    }

    @GetMapping
    public Set<Film> getFilms() {

        return filmService.getFilms();

    }

    @DeleteMapping("/{id}")
    public Film deleteFilm() {
        throw new ResponseStatusException(HttpStatus.NOT_IMPLEMENTED);
    }


    // LIKES
    @PutMapping("{id}/like/{userId}") // idempotent
    public void putLike(@PathVariable @Positive Long id, @PathVariable @Positive Long userId) {

        filmService.addLike(id, userId);

    }

    @DeleteMapping("{id}/like/{userId}") // not idempotent
    public void deleteLike(@PathVariable @Positive Long id, @PathVariable /*@Positive*/ Long userId) {

        filmService.deleteLike(id, userId);

    }

    @GetMapping("/popular")
    public List<Film> getMostPopularFilms(@RequestParam(defaultValue = TOP_FILMS_COUNT) @Positive Integer count) {

        return filmService.getMostPopularFilms(count);

    }

}
