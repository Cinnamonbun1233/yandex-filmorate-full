package ru.yandex.practicum.filmorate.storage.friend;

import ru.yandex.practicum.filmorate.model.User;

import java.util.List;

public interface FriendStorage {

    // CRUD
    void addFriend(Long userId, Long friendId);

    List<User> getFriends(Long id);

    boolean deleteFriend(Long userId, Long friendId);

    List<User> getCommonFriends(Long userId, Long otherUserId);

    // Checking
    boolean hasConnection(Long userId, Long friendId);

}
