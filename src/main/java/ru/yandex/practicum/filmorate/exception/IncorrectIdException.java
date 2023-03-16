package ru.yandex.practicum.filmorate.exception;

public class IncorrectIdException extends RuntimeException {
    public IncorrectIdException(String msg) {
        super(msg);
    }
}
