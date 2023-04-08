package ru.yandex.practicum.filmorate.storage.friend;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.User;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class FriendDbStorage implements FriendStorage {

    private final JdbcTemplate jdbcTemplate;

    // CRUD
    @Override
    public void addFriend(Long userId, Long friendId) {

        String sqlQuery = "INSERT INTO friends(user_from, user_to) values (?, ?)";
        jdbcTemplate.update(sqlQuery, userId, friendId);

    }

    @Override
    public List<User> getFriends(Long id) {

        String sqlQuery = "SELECT id, email, login, name, birthday FROM filmorate_user WHERE id IN " +
                "(SELECT user_to FROM friends WHERE user_from = ? ORDER BY id)";
        return jdbcTemplate.query(sqlQuery, (rs, rowNum) -> makeUser(rs), id);

    }

    @Override
    public boolean deleteFriend(Long userId, Long friendId) {

        String sqlQuery = "DELETE FROM friends WHERE user_from = ? AND user_to = ?";
        return jdbcTemplate.update(sqlQuery, userId, friendId) > 0;

    }

    @Override
    public List<User> getCommonFriends(Long userId, Long otherUserId) {

        String sqlQuery = "SELECT id, email, login, name, birthday FROM filmorate_user WHERE id IN" +
                "(SELECT user_to FROM friends WHERE user_from = ? AND user_to IN " +
                "(SELECT user_to FROM friends WHERE user_from = ?))";

        return jdbcTemplate.query(sqlQuery, (rs, rowNum) -> makeUser(rs), otherUserId, userId);

    }


    // Checking
    @Override
    public boolean hasConnection(Long userId, Long friendId) {

        String sql = "SELECT count(*) FROM friends WHERE user_from = ? AND user_to = ?";
        int count = jdbcTemplate.queryForObject(sql, Integer.class, userId, friendId);
        return (count > 0);

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
