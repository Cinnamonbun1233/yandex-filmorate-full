package ru.yandex.practicum.filmorate.controller;

import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;

public class ErrorResponse {

    private final String status;
    private final LocalDateTime timestamp;
    private final String error;
    private final String description;

    public ErrorResponse(HttpStatus status, String error, String description) {
        this.status = Integer.toString(status.value());
        this.timestamp = LocalDateTime.now();
        this.error = error;
        this.description = description;
    }

    @SuppressWarnings("unused")
    public String getStatus() {
        return status;
    }

    @SuppressWarnings("unused")
    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    @SuppressWarnings("unused")
    public String getError() {
        return error;
    }

    @SuppressWarnings("unused")
    public String getDescription() {
        return description;
    }

}