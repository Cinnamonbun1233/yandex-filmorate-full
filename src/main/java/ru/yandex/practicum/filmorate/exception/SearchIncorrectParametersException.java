package ru.yandex.practicum.filmorate.exception;

public class SearchIncorrectParametersException extends RuntimeException {
    public SearchIncorrectParametersException(String message) {
        super(message);
    }
}