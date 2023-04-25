package ru.yandex.practicum.filmorate.exception;

public class AlreadyLikedException extends RuntimeException {
    public AlreadyLikedException() {
    }

    public AlreadyLikedException(String message) {
        super(message);
    }
}
