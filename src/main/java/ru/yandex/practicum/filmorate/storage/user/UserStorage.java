package ru.yandex.practicum.filmorate.storage.user;

import ru.yandex.practicum.filmorate.model.User;

import java.util.List;

public interface UserStorage {

    // USERS - CRUD
    User addUser(User user);

    User updateUser(User user);

    User getUser(Long id);

    List<User> getUsers();

    void deleteUserById(Long userId);

    // USERS - Checking
    boolean emailAlreadyUsed(String email);

    boolean emailAlreadyUsed(String email, Long excludedUserId);

    boolean loginAlreadyUsed(String login);

    boolean loginAlreadyUsed(String login, Long excludedUserId);

    // RESET STORAGE
    void deleteAllData();


}
