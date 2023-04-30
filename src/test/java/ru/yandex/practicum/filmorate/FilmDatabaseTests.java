package ru.yandex.practicum.filmorate;


import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.genre.GenreStorage;
import ru.yandex.practicum.filmorate.storage.like.LikeStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@SuppressWarnings("unused")
public class FilmDatabaseTests {

    @Autowired
    private FilmStorage filmStorage;
    @Autowired
    private UserStorage userStorage;
    @Autowired
    private GenreStorage genreStorage;
    @Autowired
    private LikeStorage likeStorage;

    @BeforeEach
    public void setUp() {
        filmStorage.deleteAllData();
    }

    @Test
    public void testFilmCrud() {

        Film film = filmStorage.getFilm(1L);
        assertThat(film)
                .isNull();

        List<Film> films = filmStorage.getFilms();
        assertThat(films)
                .size().isZero();

        filmStorage.addFilm(Film.builder()
                .name("The Shawshank Redemption")
                .description("Nominated for 7 Oscars")
                .releaseDate(LocalDate.of(1994, 9, 22))
                .duration(144)
                .mpa(Mpa.G)
                .build());

        Film film1 = filmStorage.getFilm(1L);
        assertThat(film1)
                .isNotNull()
                .hasFieldOrPropertyWithValue("id", 1L)
                .hasFieldOrPropertyWithValue("name", "The Shawshank Redemption")
                .hasFieldOrPropertyWithValue("description", "Nominated for 7 Oscars");

        film1.setRate((byte) 5);

        filmStorage.updateFilm(film1);
        assertThat(film1)
                .hasFieldOrPropertyWithValue("rate", (byte) 5);

        List<Film> allFilms = filmStorage.getFilms();
        assertThat(allFilms)
                .size().isEqualTo(1);

    }

    @Test
    public void testFilmChecking() {

        Film film1 = Film.builder()
                .name("The Shawshank Redemption")
                .description("Nominated for 7 Oscars")
                .releaseDate(LocalDate.of(1994, 9, 22))
                .duration(144)
                .mpa(Mpa.G)
                .build();

        Film film2 = Film.builder()
                .name("The Shawshank Redemption")
                .description("Nominated for 7 Oscars")
                .releaseDate(LocalDate.of(1994, 9, 22))
                .duration(144)
                .mpa(Mpa.G)
                .build();

        filmStorage.addFilm(film1);

        assertThat(filmStorage.hasTwin(film2))
                .isTrue();

    }

    @Test
    public void testLikeCrud() {

        Film film1 = Film.builder()
                .name("The Shawshank Redemption")
                .description("Nominated for 7 Oscars")
                .releaseDate(LocalDate.of(1994, 9, 22))
                .duration(144)
                .mpa(Mpa.G)
                .build();

        Film film2 = Film.builder()
                .name("The Godfather")
                .description("Won 3 Oscars")
                .releaseDate(LocalDate.of(1972, 3, 17))
                .duration(144)
                .mpa(Mpa.G)
                .build();

        User user1 = User.builder()
                .name("John")
                .email("john@beatles.uk")
                .login("john")
                .birthday(LocalDate.of(1940, 10, 9))
                .build();

        filmStorage.addFilm(film1);
        filmStorage.addFilm(film2);
        userStorage.addUser(user1);

        likeStorage.addLike(2L, 1L);

        assertThat(filmStorage.getMostPopularFilms(10, null, null).get(0))
                .hasFieldOrPropertyWithValue("name", "The Godfather");

        assertThat(filmStorage.getMostPopularFilms(10, null, null).get(1))
                .hasFieldOrPropertyWithValue("name", "The Shawshank Redemption");


        likeStorage.deleteLike(2L, 1L);
        likeStorage.addLike(1L, 1L);

        assertThat(filmStorage.getMostPopularFilms(10, null, null).get(0))
                .hasFieldOrPropertyWithValue("name", "The Shawshank Redemption");

        assertThat(filmStorage.getMostPopularFilms(10, null, null).get(1))
                .hasFieldOrPropertyWithValue("name", "The Godfather");

        assertThat(likeStorage.hasLike(1L, 1L))
                .isTrue();

        assertThat(likeStorage.hasLike(2L, 1L))
                .isFalse();


    }

    @Test
    public void testGenreCrud() {

        List<Genre> genres = genreStorage.getGenres();
        assertThat(genres)
                .size().isEqualTo(6);

        genreStorage.addGenre(Genre.builder()
                .name("New genre")
                .build());

        assertThat(genreStorage.getGenres())
                .size().isEqualTo(7);

        assertThat(genreStorage.getGenre(7L))
                .hasFieldOrPropertyWithValue("name", "New genre");

        assertThat(genreStorage.hasGenre(Genre.builder().name("New genre").build()))
                .isTrue();

        assertThat(genreStorage.hasGenre(Genre.builder().name("Some genre").build()))
                .isFalse();

    }

    @Test
    public void deleteFilmTest() {
        filmStorage.addFilm(Film.builder()
                .name("The Shawshank Redemption")
                .description("Nominated for 7 Oscars")
                .releaseDate(LocalDate.of(1994, 9, 22))
                .duration(144)
                .mpa(Mpa.G)
                .build());
        filmStorage.deleteFilmById(1L);
        List<Film> films = filmStorage.getFilms();
        assertTrue(films.isEmpty());
    }
}
