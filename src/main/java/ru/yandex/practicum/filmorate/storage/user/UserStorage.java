package ru.yandex.practicum.filmorate.storage.user;

import ru.yandex.practicum.filmorate.model.User;

import java.util.TreeSet;

public interface UserStorage {

    // USERS - CRUD
    User addUser(User user);

    User updateUser(User user);

    User getUser(Long id);

    TreeSet<User> getUsers();

    // USERS - Checking
    boolean emailAlreadyUsed(String email);

    boolean emailAlreadyUsed(String email, Long excludedUserId);

    boolean loginAlreadyUsed(String login);

    boolean loginAlreadyUsed(String login, Long excludedUserId);

    // FRIENDS - CRUD
    void addFriend(Long userId, Long friendId);

    TreeSet<User> getFriends(Long id);

    boolean deleteFriend(Long userId, Long friendId);

    // FRIENDS - Checking
    boolean hasConnection(Long userId, Long friendId);

    // RESET STORAGE
    void deleteAllData();


}
