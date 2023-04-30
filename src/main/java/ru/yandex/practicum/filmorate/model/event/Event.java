package ru.yandex.practicum.filmorate.model.event;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

@Data
@Builder
public class Event {
   @Positive
   private long timestamp;
   @Positive
   private Long userId;
   @NotNull
   private EventType eventType;
   @NotNull
   private Operation operation;
   @Positive
   private Long eventId;
   @Positive
   private Long entityId;
}
