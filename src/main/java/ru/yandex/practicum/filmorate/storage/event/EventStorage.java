package ru.yandex.practicum.filmorate.storage.event;

import ru.yandex.practicum.filmorate.model.event.Event;
import ru.yandex.practicum.filmorate.model.event.EventType;
import ru.yandex.practicum.filmorate.model.event.Operation;

import java.util.List;

public interface EventStorage {
    void addEvent(EventType eventType, Operation operation, Long userId, Long entityId);

    List<Event> getFeed(Long userId);
}
