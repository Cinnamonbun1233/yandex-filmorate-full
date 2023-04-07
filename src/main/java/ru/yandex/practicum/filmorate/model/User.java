package ru.yandex.practicum.filmorate.model;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.jetbrains.annotations.NotNull;

import javax.validation.constraints.*;
import java.time.LocalDate;

@Data
@Builder
public class User {

    @Positive
    @EqualsAndHashCode.Exclude
    private Long id;
    @Email(message = "Invalid e-mail")
    @NotEmpty
    private String email;
    @Pattern(regexp = "[^' ']*", message = "Invalid login")
    private String login;
    private String name;
    @PastOrPresent
    @NotNull
    private LocalDate birthday;

}