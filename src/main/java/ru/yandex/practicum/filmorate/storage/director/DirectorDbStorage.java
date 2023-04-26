package ru.yandex.practicum.filmorate.storage.director;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Director;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Repository
@RequiredArgsConstructor
public class DirectorDbStorage implements DirectorStorage{

    private final JdbcTemplate jdbcTemplate;


    @Override
    public Director addDirector(Director director) {

        Map<String, Object> directorFields = new HashMap<>();
        directorFields.put("name", director.getName());

        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("DIRECTOR")
                .usingGeneratedKeyColumns("id");

        Long genreId =  simpleJdbcInsert.executeAndReturnKey(directorFields).longValue();
        director.setId(genreId);

        return director;

    }

    @Override
    public boolean hasTwin(Director director) {

        boolean newDirector = (director.getId() == null);

        String sql;
        Object[] params;

        if (newDirector) {
            sql = "SELECT count(*) FROM director WHERE name = ?";
            params = new Object[]{director.getName()};
        } else {
            sql = "SELECT count(*) FROM director WHERE name = ? AND id <> ?";
            params = new Object[]{director.getName(), director.getId()};
        }
        int count = jdbcTemplate.queryForObject(sql, Integer.class, params);

        return (count > 0);

    }

    @Override
    public Director getDirector(Long id) {

        String sqlQuery = "SELECT id, name FROM director WHERE id = ?";

        try {
            return jdbcTemplate.queryForObject(sqlQuery, (rs, rowNum) -> makeDirector(rs), id);
        } catch (IncorrectResultSizeDataAccessException e) {
            return null;
        }

    }

    @Override
    public Director updateDirector(Director director) {

        String sqlQuery = "UPDATE director SET name = ? WHERE id = ?";

        jdbcTemplate.update(sqlQuery,
                director.getName(),
                director.getId());

        return director;

    }

    @Override
    public List<Director> getDirectors() {

        String sqlQuery = "SELECT id, name FROM director ORDER BY id";
        return jdbcTemplate.query(sqlQuery, (rs, rowNum) -> makeDirector(rs));

    }

    @Override
    public Set<Long> getUnknownDirectorIds(Set<Long> directorIds) {

        String sqlQuery = "SELECT id FROM director";
        List<Long> directorFoundIds = jdbcTemplate.query(sqlQuery, (rs, rowNum) -> makeDirectorId(rs));

        directorIds.removeAll(directorFoundIds);

        return directorIds;

    }


    // PRIVATE
    private Director makeDirector(ResultSet resultSet) throws SQLException {

        return Director.builder()
                .id(resultSet.getLong("id"))
                .name(resultSet.getString("name"))
                .build();

    }

    private Long makeDirectorId(ResultSet resultSet) throws SQLException {

        return resultSet.getLong("id");

    }

}
