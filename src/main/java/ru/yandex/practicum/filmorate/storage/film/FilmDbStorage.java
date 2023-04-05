package ru.yandex.practicum.filmorate.storage.film;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.sql.*;
import java.sql.Date;
import java.time.LocalDate;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Repository
@SuppressWarnings("unused")
public class FilmDbStorage implements FilmStorage{

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public FilmDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    // FILMS - CRUD
    @Override
    public Film addFilm(Film film) {

        Mpa mpa = Mpa.getMpa(film.getMpa());
        Long mpaId = (mpa == null ? null : mpa.getId());

        // film
        Map<String, Object> filmFields = new HashMap<>();
        filmFields.put("name", film.getName());
        filmFields.put("description", film.getDescription());
        filmFields.put("release_date", film.getReleaseDate());
        filmFields.put("duration", film.getDuration());
        filmFields.put("rate", film.getRate());
        filmFields.put("mpa", mpaId);

        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("FILM")
                .usingGeneratedKeyColumns("id");

        Long filmId =  simpleJdbcInsert.executeAndReturnKey(filmFields).longValue();
        film.setId(filmId);

        // genres of film
        updateGenres(film);

        return film;

    }

    @Override
    public Film updateFilm(Film film) {

        // film
        String sqlQuery = "UPDATE film SET name = ?, description = ?, release_date = ?, duration = ?" +
                ", rate = ?, mpa = ? WHERE id = ?";

        Mpa mpa = Mpa.getMpa(film.getMpa());
        Long mpaId = (mpa == null ? null : mpa.getId());

        jdbcTemplate.update(sqlQuery
                , film.getName()
                , film.getDescription()
                , film.getReleaseDate()
                , film.getDuration()
                , film.getRate()
                , mpaId
                , film.getId());

        // genres of film
        updateGenres(film);

        return film;

    }

    @Override
    public Film getFilm(Long id) {

        String sqlQuery = "SELECT id, name, description, release_date, duration, rate, mpa FROM film WHERE id = ?";

        try {
            // film
            Film film = jdbcTemplate.queryForObject(sqlQuery, (rs, rowNum) -> makeFilm(rs), id);
            // genres
            linkGenresToFilms(new ArrayList<>(Arrays.asList(film)));
            return film;
        } catch (IncorrectResultSizeDataAccessException e) {
            return null;
        }

    }

    @Override
    public TreeSet<Film> getFilms() {

        // films
        String sqlQuery = "SELECT id, name, description, release_date, duration, rate, mpa FROM film";
        List<Film> filmList =  jdbcTemplate.query(sqlQuery, (rs, rowNum) -> makeFilm(rs));

        // genres
        linkGenresToAllFilms(filmList);

        return new TreeSet<>(filmList);

    }


    // FILMS - Checking
    @Override
    public boolean hasTwin(Film film) {

        boolean newFilm = (film.getId() == null);

        String sql;
        Object[] params;

        if (newFilm) {
            sql = "SELECT count(*) FROM film WHERE name = ? AND description = ? AND release_date = ?";
            params = new Object[]{film.getName(), film.getDescription(), film.getReleaseDate()};
        } else {
            sql = "SELECT count(*) FROM film WHERE name = ? AND description = ? AND release_date = ? AND id <> ?";
            params = new Object[]{film.getName(), film.getDescription(), film.getReleaseDate(), film.getId()};
        }
        int count = jdbcTemplate.queryForObject(sql, Integer.class, params);

        return (count > 0);

    }


    // LIKES - CRUD
    @Override
    public void addLike(Long filmId, Long userId) {

        String sqlQuery = "INSERT INTO filmorate_like (film_id, user_id) VALUES (?, ?)";
        try {
            jdbcTemplate.update(sqlQuery, filmId, userId);
        } catch (DuplicateKeyException e) {}

    }

    @Override
    public boolean deleteLike(Long filmId, Long userId) {

        String sqlQuery = "DELETE FROM filmorate_like WHERE film_id = ? AND user_id = ?";
        return jdbcTemplate.update(sqlQuery, filmId, userId) > 0;

    }

    @Override
    public List<Film> getMostPopularFilms(Integer count) {

        // films
        String sqlQuery = "SELECT f.id, f.name, f.description, f.release_date, f.duration, f.rate, f.mpa, idAndLikes.Likes " +
                "FROM film f JOIN " +
                "(SELECT f.id AS film_id, IFNULL(l.sum,0) AS likes " +
                "FROM film AS f LEFT JOIN " +
                "(SELECT film_id, count(*) AS sum " +
                "FROM FILMORATE_LIKE GROUP BY film_id ORDER BY sum DESC LIMIT (?)) " +
                "AS l ON f.id = l.film_id ORDER BY likes DESC LIMIT (?)) AS idAndLikes ON f.ID = idAndLikes.film_id";

        List<Film> filmList =  jdbcTemplate.query(sqlQuery, (rs, rowNum) -> makeFilm(rs), count, count);

        // genres
        linkGenresToFilms(filmList);

        return filmList;

    }


    // LIKES - Checking
    @Override
    public boolean hasLike(Long filmId, Long userId) {

        String sql = "SELECT count(*) FROM filmorate_like WHERE film_id = ? AND user_id = ?";
        int count = jdbcTemplate.queryForObject(sql, Integer.class, filmId, userId);
        return (count > 0);

    }


    // GENRES - CRUD
    @Override
    public Genre addGenre(Genre genre) {

        Map<String, Object> genreFields = new HashMap<>();
        genreFields.put("name", genre.getName());

        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("GENRE")
                .usingGeneratedKeyColumns("id");

        Long genreId =  simpleJdbcInsert.executeAndReturnKey(genreFields).longValue();
        genre.setId(genreId);

        return genre;

    }

    @Override
    public Genre updateGenre(Genre genre) {

        // genre
        String sqlQuery = "UPDATE genre SET name = ? WHERE id = ?";

        jdbcTemplate.update(sqlQuery
                , genre.getName()
                , genre.getId());

        return genre;

    }

    @Override
    public Genre getGenre(Long id) {

        String sqlQuery = "SELECT id, name FROM genre WHERE id = ?";

        try {
            return jdbcTemplate.queryForObject(sqlQuery, (rs, rowNum) -> makeGenre(rs), id);
        } catch (IncorrectResultSizeDataAccessException e) {
            return null;
        }

    }

    @Override
    public Set<Genre> getGenres() {

        String sqlQuery = "SELECT id, name FROM genre";
        List<Genre> genreList =  jdbcTemplate.query(sqlQuery, (rs, rowNum) -> makeGenre(rs));
        return new TreeSet<>(genreList);

    }


    // GENRES - Checking
    @Override
    public boolean hasGenre(Genre genre) {

        boolean newGenre = (genre.getId() == null);

        String sql;
        Object[] params;

        if (newGenre) {
            sql = "SELECT count(*) FROM genre WHERE name = ?";
            params = new Object[]{genre.getName()};
        } else {
            sql = "SELECT count(*) FROM film WHERE name = ? AND id <> ?";
            params = new Object[]{genre.getName(), genre.getId()};
        }
        int count = jdbcTemplate.queryForObject(sql, Integer.class, params);

        return (count > 0);

    }

    @Override
    public Set<Long> getUnknownGenreIds(Set<Long> genreIds) {

        String sqlQuery = "SELECT id FROM genre";
        List<Long> genreFoundIds = jdbcTemplate.query(sqlQuery, (rs, rowNum) -> makeGenreId(rs));

        genreIds.removeAll(genreFoundIds);

        return genreIds;

    }

    // RESET STORAGE
    @Override
    public void deleteAllData() {

        jdbcTemplate.update("DELETE FROM FILMORATE_LIKE");

        jdbcTemplate.update("DELETE FROM FILM_GENRE");

        jdbcTemplate.update("DELETE FROM FILM");
        jdbcTemplate.update("ALTER TABLE FILM ALTER COLUMN ID RESTART WITH 1");

        jdbcTemplate.update("DELETE FROM GENRE WHERE (id > 6)");
        jdbcTemplate.update("ALTER TABLE GENRE ALTER COLUMN ID RESTART WITH 7");

    }

    // PRIVATE

    // RowMappers
    private Film makeFilm(ResultSet resultSet) throws SQLException {

        // i don't like this code but dunno how to do it better
        Date releaseDateAsDate = resultSet.getDate("release_date");
        LocalDate releaseDateAsLocalDate = (releaseDateAsDate == null ? null : releaseDateAsDate.toLocalDate());

        return Film.builder()
                .id(resultSet.getLong("id"))
                .name(resultSet.getString("name"))
                .description(resultSet.getString("description"))
                .releaseDate(releaseDateAsLocalDate)
                .duration(resultSet.getInt("duration"))
                .rate(resultSet.getByte("rate"))
                .mpa(Mpa.getMpa(resultSet.getLong("mpa")))
                .build();
    }

    private Long makeGenreId(ResultSet resultSet) throws SQLException {
        return resultSet.getLong("id");
    }

    private Genre makeGenre(ResultSet resultSet) throws SQLException {

        return Genre.builder()
                .id(resultSet.getLong("id"))
                .name(resultSet.getString("name"))
                .build();

    }


    // genres
    private void linkGenresToFilms(List<Film> filmList) {

        linkGenresToFilms(filmList, true);

    }

    private void linkGenresToAllFilms(List<Film> filmList) {

        linkGenresToFilms(filmList, false);

    }

    private void linkGenresToFilms(List<Film> filmList, boolean allFilms) {

        // all genres
        String sqlQueryAllGenres = "SELECT id, name FROM genre";
        List<Genre> allGenresList =  jdbcTemplate.query(sqlQueryAllGenres, (rs, rowNum) -> makeGenre(rs));
        Map<Long, Genre> allGenresListMap = allGenresList.stream().collect(Collectors.toMap(Genre::getId, Function.identity()));

        // films' genres
        String sqlFilmsGenres;
        SqlRowSet sqlRowSet;

        if (allFilms) {
            String sqlFilmsGenresTemplate = "SELECT film_id, genre_id FROM film_genre WHERE film_id IN (%s)";
            List<Long> filmIds = filmList.stream().map(Film::getId).collect(Collectors.toList());
            String sqlParam = String.join(",", Collections.nCopies(filmIds.size(), "?"));
            sqlFilmsGenres = String.format(sqlFilmsGenresTemplate, sqlParam);
            sqlRowSet = jdbcTemplate.queryForRowSet(sqlFilmsGenres, filmIds.toArray());
        } else {
            sqlFilmsGenres = "SELECT film_id, genre_id FROM film_genre";
            sqlRowSet = jdbcTemplate.queryForRowSet(sqlFilmsGenres);
        }

        Map<Long, TreeSet<Genre>> filmsGenres = new HashMap<>();
        while (sqlRowSet.next()) {
            Long film_id = sqlRowSet.getLong("film_id");
            Long genre_id = sqlRowSet.getLong("genre_id");
            filmsGenres.computeIfAbsent(film_id, v -> new TreeSet<>()).add(allGenresListMap.get(genre_id));
        }

        // set genres to film
        filmList.forEach(film -> {
            TreeSet<Genre> genres = filmsGenres.get(film.getId());
            film.setGenres((genres != null ? genres : new TreeSet<>()));
        });

    }

    private void updateGenres(Film film) {

        // delete
        String sqlQueryDelete = "DELETE FROM film_genre WHERE film_id = ?";
        jdbcTemplate.update(sqlQueryDelete, film.getId());

        // insert
        if (film.getGenres() == null || film.getGenres().size() == 0) {
            return;
        }
        List<Genre> genres = new ArrayList<>(film.getGenres());
        String sqlQueryInsert = "INSERT INTO film_genre(film_id, genre_id) VALUES (?, ?)";

        jdbcTemplate.batchUpdate(sqlQueryInsert, new BatchPreparedStatementSetter() {

            @Override
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                ps.setLong(1, film.getId());
                ps.setLong(2, genres.get(i).getId());
            }

            @Override
            public int getBatchSize() {
                return genres.size();
            }

        });

    }

}