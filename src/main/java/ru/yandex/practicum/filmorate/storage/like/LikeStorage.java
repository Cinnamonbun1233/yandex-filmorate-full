package ru.yandex.practicum.filmorate.storage.like;

public interface LikeStorage {

    // CRUD
    void addLike(Long filmId, Long userId);

    boolean deleteLike(Long filmId, Long userId);


    // Checking
    boolean hasLike(Long filmId, Long userId);

}
