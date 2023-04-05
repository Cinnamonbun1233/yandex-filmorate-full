package ru.yandex.practicum.filmorate.storage.user;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.User;

import java.util.*;
import java.util.stream.Collectors;

@Component
public class InMemoryUserStorage implements UserStorage {

    private Long userId = 0L;
    private final Map<Long, User> idUser = new HashMap<>();
    private final Map<String, User> emailUser = new HashMap<>();
    private final Map<String, User> loginUser = new HashMap<>();
    private final Map<Long, TreeSet<Long>> friends = new HashMap<>();


    // USERS - CRUD
    @Override
    public User addUser(User user) {

        Long currentId = getUserId();
        user.setId(currentId);

        idUser.put(currentId, user);
        emailUser.put(user.getEmail(), user);
        loginUser.put(user.getLogin(), user);

        return user;

    }

    @Override
    public User updateUser(User user) {

        Long userId = user.getId();
        User oldUser = idUser.get(userId);
        if (oldUser == null) {
            return null;
        }
        String oldEmail = oldUser.getEmail();
        String oldLogin = oldUser.getLogin();

        idUser.put(user.getId(), user);

        if (!oldEmail.equals(user.getEmail())) {
            emailUser.remove(oldEmail);
            emailUser.put(user.getEmail(), user);
        }

        if (!oldLogin.equals(user.getLogin())) {
            emailUser.remove(oldLogin);
            emailUser.put(user.getLogin(), user);
        }

        return user;

    }

    @Override
    public User getUser(Long id) {

        return idUser.get(id);

    }

    @Override
    public TreeSet<User> getUsers() {

        return new TreeSet<>(idUser.values());

    }


    // USERS - Checking
    @Override
    public boolean emailAlreadyUsed(String email) {
        return emailUser.containsKey(email);
    }

    @Override
    public boolean emailAlreadyUsed(String email, Long excludedUserId) {
        User user = emailUser.get(email);
        return user != null && !excludedUserId.equals(user.getId());
    }

    @Override
    public boolean loginAlreadyUsed(String login) {
        return loginUser.containsKey(login);
    }

    @Override
    public boolean loginAlreadyUsed(String login, Long excludedUserId) {
        User user = loginUser.get(login);
        return user != null && !excludedUserId.equals(user.getId());
    }


    // FRIENDS - CRUD
    @Override
    public void addFriend(Long userId, Long friendId) {

        friends.computeIfAbsent(userId, v -> new TreeSet<>())
                .add(friendId);

    }

    @Override
    public boolean deleteFriend(Long userId, Long friendId) {

        Set<Long> usersFriend = friends.get(userId);
        boolean result = usersFriend.remove(friendId);
        if (usersFriend.size() == 0) {
            friends.remove(userId);
        }
        return result;

    }

    @Override
    public TreeSet<User> getFriends(Long id) {

        Set<Long> usersFriends = friends.get(id);
        if (usersFriends == null) {
            return new TreeSet<>();
        }

        return usersFriends.stream().map(this::getUser).collect(Collectors.toCollection(TreeSet::new));

    }


    // FRIENDS - Checking
    @Override
    public boolean hasConnection(Long userId, Long friendId) {

        return friends.getOrDefault(userId, new TreeSet<>()).contains(friendId);

    }


    // RESTORE
    @Override
    public void deleteAllData() {
        idUser.clear();
        emailUser.clear();
        loginUser.clear();
        friends.clear();
        userId = 1L;
    }


    // PRIVATE
    private Long getUserId() {
        return ++userId;
    }

}
