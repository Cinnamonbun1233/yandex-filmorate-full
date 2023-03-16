package ru.yandex.practicum.filmorate.exception;

public class EmailLoginAlreadyUsed extends RuntimeException {
    public EmailLoginAlreadyUsed(String msg) {
        super(msg);
    }
}
