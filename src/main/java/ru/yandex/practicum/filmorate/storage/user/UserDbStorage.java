package ru.yandex.practicum.filmorate.storage.user;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.User;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
@RequiredArgsConstructor
@SuppressWarnings("unused")
public class UserDbStorage implements UserStorage {

    private final JdbcTemplate jdbcTemplate;

    // USERS - CRUD
    @Override
    public User addUser(User user) {

        Map<String, Object> userFields = new HashMap<>();
        userFields.put("email", user.getEmail());
        userFields.put("login", user.getLogin());
        userFields.put("name", user.getName());
        userFields.put("birthday", user.getBirthday());

        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("FILMORATE_USER")
                .usingGeneratedKeyColumns("id");
        Long id =  simpleJdbcInsert.executeAndReturnKey(userFields).longValue();

        user.setId(id);

        return user;

    }

    @Override
    public User updateUser(User user) {

        String sqlQuery = "UPDATE filmorate_user SET email = ?, login = ?, name = ?, birthday = ? WHERE id = ?";

        jdbcTemplate.update(sqlQuery,
                user.getEmail(),
                user.getLogin(),
                user.getName(),
                user.getBirthday(),
                user.getId());

        return user;
    }

    @Override
    public User getUser(Long id) {

        String sqlQuery = "SELECT id, email, login, name, birthday FROM filmorate_user WHERE id = ?";

        try {
            return jdbcTemplate.queryForObject(sqlQuery, (rs, rowNum) -> makeUser(rs), id);
        } catch (IncorrectResultSizeDataAccessException e) {
            return null;
        }

    }

    @Override
    public List<User> getUsers() {

        String sqlQuery = "SELECT id, email, login, name, birthday FROM filmorate_user ORDER BY id";
        return jdbcTemplate.query(sqlQuery, (rs, rowNum) -> makeUser(rs));

    }

    @Override
    public void deleteUserById(Long userId) {
        String userSqlQuery = "DELETE FROM FILMORATE_USER WHERE ID = ?";
        jdbcTemplate.update(userSqlQuery, userId);
    }

    // USERS - Checking
    @Override
    public boolean emailAlreadyUsed(String email) {

        String sql = "SELECT count(*) FROM filmorate_user WHERE email = ?";
        int count = jdbcTemplate.queryForObject(sql, Integer.class, email);
        return (count > 0);

    }

    @Override
    public boolean emailAlreadyUsed(String email, Long excludedUserId) {

        String sql = "SELECT count(*) FROM filmorate_user WHERE email = ? AND id <> ?";
        int count = jdbcTemplate.queryForObject(sql, Integer.class, email, excludedUserId);
        return (count > 0);

    }

    @Override
    public boolean loginAlreadyUsed(String login) {

        String sql = "SELECT count(*) FROM filmorate_user WHERE login = ?";
        int count = jdbcTemplate.queryForObject(sql, Integer.class, login);
        return (count > 0);

    }

    @Override
    public boolean loginAlreadyUsed(String login, Long excludedUserId) {

        String sql = "SELECT count(*) FROM filmorate_user WHERE login = ? AND id <> ?";
        int count = jdbcTemplate.queryForObject(sql, Integer.class, login, excludedUserId);
        return (count > 0);

    }


    // RESTORE
    @Override
    public void deleteAllData() {

        jdbcTemplate.update("DELETE FROM FILMORATE_LIKE");

        jdbcTemplate.update("DELETE FROM FRIENDS");

        jdbcTemplate.update("DELETE FROM FILMORATE_USER");
        jdbcTemplate.update("ALTER TABLE FILMORATE_USER ALTER COLUMN ID RESTART WITH 1");

    }


    // PRIVATE
    private User makeUser(ResultSet resultSet) throws SQLException {

        return User.builder()
                .id(resultSet.getLong("id"))
                .email(resultSet.getString("email"))
                .login(resultSet.getString("login"))
                .name(resultSet.getString("name"))
                .birthday(resultSet.getDate("birthday").toLocalDate())
                .build();

    }

}
