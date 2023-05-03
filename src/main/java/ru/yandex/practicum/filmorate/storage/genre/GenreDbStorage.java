package ru.yandex.practicum.filmorate.storage.genre;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Genre;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Repository
@RequiredArgsConstructor
@SuppressWarnings("unused")
public class GenreDbStorage implements GenreStorage {

    private final JdbcTemplate jdbcTemplate;

    // CRUD
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

        jdbcTemplate.update(sqlQuery,
                genre.getName(),
                genre.getId());

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
    public List<Genre> getGenres() {

        String sqlQuery = "SELECT id, name FROM genre ORDER BY id";
        return jdbcTemplate.query(sqlQuery, (rs, rowNum) -> makeGenre(rs));

    }

    // Checking
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


    // PRIVATE
    private Genre makeGenre(ResultSet resultSet) throws SQLException {

        return Genre.builder()
                .id(resultSet.getLong("id"))
                .name(resultSet.getString("name"))
                .build();

    }

    private Long makeGenreId(ResultSet resultSet) throws SQLException {
        return resultSet.getLong("id");
    }

}
