package ru.yandex.practicum.filmorate.controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.IncorrectIdException;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.service.FilmService;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import java.util.List;

@RestController
@RequestMapping("/directors")
@Validated
public class DirectorController {

    private final FilmService filmService;

    @Autowired
    public DirectorController(FilmService filmService) {
        this.filmService = filmService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Director postDirector(@Valid @RequestBody Director director) {

        // add film
        return filmService.addDirector(director);

    }

    @PutMapping
    @ResponseStatus(HttpStatus.OK)
    public Director putDirector(@Valid @RequestBody Director director) {

        // validation
        boolean directorIdEmpty = (director.getId() == null);
        if (directorIdEmpty) {
            throw new IncorrectIdException("Director ID must be not empty");
        }

        // update director
        return filmService.updateDirector(director);

    }

    @GetMapping("/{id}")
    public Director getDirector(@PathVariable @Positive Long id) {

        return filmService.getDirector(id);

    }

    @GetMapping
    public List<Director> getDirector() {

        return filmService.getDirectors();

    }

    @DeleteMapping("/{id}")
    public Director deleteDirector(@PathVariable @Positive Long id) {

        return filmService.deleteDirector(id);

    }

}