package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.service.FilmService;

import java.util.Set;

@RestController
@RequestMapping("/mpa")
@RequiredArgsConstructor
@SuppressWarnings("unused")
public class MpaController {

    private final FilmService filmService;

    @GetMapping
    public Set<Mpa> getMpaRatings() {

        return filmService.getMpas();

    }

    @GetMapping("/{id}")
    public Mpa getMpaRatings(@PathVariable Long id) {

        return filmService.getMpa(id);

    }


}
