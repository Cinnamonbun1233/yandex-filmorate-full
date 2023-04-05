package ru.yandex.practicum.filmorate.storage.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.User;

import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;

@Repository
@SuppressWarnings("unused")
public class UserDbStorage implements UserStorage {

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public UserDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

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
    public TreeSet<User> getUsers() {

        String sqlQuery = "SELECT id, email, login, name, birthday FROM filmorate_user";
        List<User> userList =  jdbcTemplate.query(sqlQuery, (rs, rowNum) -> makeUser(rs));
        return new TreeSet<>(userList);

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


    // FRIENDS - CRUD

    @Override
    public void addFriend(Long userId, Long friendId) {

        String sqlQuery = "INSERT INTO friends(user_from, user_to) values (?, ?)";
        jdbcTemplate.update(sqlQuery, userId, friendId);

    }

    @Override
    public TreeSet<User> getFriends(Long id) {

        String sqlQuery = "SELECT id, email, login, name, birthday FROM filmorate_user WHERE id IN " +
                "(SELECT user_to FROM friends WHERE user_from = ?)";
        List<User> userList =  jdbcTemplate.query(sqlQuery, (rs, rowNum) -> makeUser(rs), id);
        return new TreeSet<>(userList);

    }

    @Override
    public boolean deleteFriend(Long userId, Long friendId) {

        String sqlQuery = "DELETE FROM friends WHERE user_from = ? AND user_to = ?";
        return jdbcTemplate.update(sqlQuery, userId, friendId) > 0;

    }


    // FRIENDS - Checking

    @Override
    public boolean hasConnection(Long userId, Long friendId) {

        String sql = "SELECT count(*) FROM friends WHERE user_from = ? AND user_to = ?";
        int count = jdbcTemplate.queryForObject(sql, Integer.class, userId, friendId);
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

        // i don't like this code but dunno how to do it better
        Date birthdayAsDate = resultSet.getDate("birthday");
        LocalDate birthdayAsLocalDate = (birthdayAsDate == null ? null : birthdayAsDate.toLocalDate());

        return User.builder()
                .id(resultSet.getLong("id"))
                .email(resultSet.getString("email"))
                .login(resultSet.getString("login"))
                .name(resultSet.getString("name"))
                .birthday(birthdayAsLocalDate)
                .build();

    }

}
