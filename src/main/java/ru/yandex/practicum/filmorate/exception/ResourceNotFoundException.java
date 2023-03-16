package ru.yandex.practicum.filmorate.exception;

import java.util.Map;
import java.util.stream.Collectors;

public class ResourceNotFoundException extends RuntimeException {

    public ResourceNotFoundException(String msg) {
        super(msg);
    }

    public ResourceNotFoundException(String resourceName, Long id) {
        super(resourceName + " with id " + id + " not found");
    }

    public ResourceNotFoundException(Map<String, Long> resourceNameId) {

        super(resourceNameId.entrySet()
                .stream()
                .map(e -> "" + e.getKey() + " with id " + e.getValue() + " not found")
                .collect(Collectors.joining(", ")));

    }

}
