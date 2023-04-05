package ru.yandex.practicum.filmorate.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.jetbrains.annotations.NotNull;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Positive;

@Data
@AllArgsConstructor
@Builder
public class Genre implements Comparable<Genre> {

    @Positive
    private Long id;
    @NotBlank
    private String name;


    @Override
    public int compareTo(@NotNull Genre o) {
        return Long.compare(this.id, o.id);
    }

}

