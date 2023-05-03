package ru.yandex.practicum.filmorate.storage.event;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.event.Event;
import ru.yandex.practicum.filmorate.model.event.EventType;
import ru.yandex.practicum.filmorate.model.event.Operation;

import java.sql.ResultSet;
import java.time.Instant;
import java.util.List;
import java.util.Map;

@Repository
@RequiredArgsConstructor
public class EventDbStorage implements EventStorage {
    private final JdbcTemplate jdbcTemplate;

    public void addEvent(EventType eventType, Operation operation, Long userId, Long entityId) {
        long timestamp = Instant.now().toEpochMilli();
        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate).withTableName("EVENT")
                .usingGeneratedKeyColumns("event_id");

        simpleJdbcInsert.execute(Map.of("timestamp", timestamp,
                "user_id", userId,
                "eventType", eventType.name(),
                "operation", operation.name(),
                "entity_id", entityId));
    }

    public List<Event> getFeed(Long userId) {
        String sql = "SELECT * FROM EVENT WHERE user_id = ?" +
                "ORDER BY timestamp";
        return jdbcTemplate.query(sql, (rs, rowNum) -> makeEvent(rs), userId);
    }

    @SneakyThrows
    private Event makeEvent(ResultSet rs) {
        return Event.builder()
                .eventId(rs.getLong("event_id"))
                .timestamp(rs.getLong("timestamp"))
                .userId(rs.getLong("user_id"))
                .eventType(EventType.valueOf(rs.getString("eventType")))
                .operation(Operation.valueOf(rs.getString("operation")))
                .entityId(rs.getLong("entity_id"))
                .build();
    }
}
