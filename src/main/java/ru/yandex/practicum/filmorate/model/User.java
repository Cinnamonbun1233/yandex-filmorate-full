package ru.yandex.practicum.filmorate.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.*;
import java.time.LocalDate;

@Data
public class User {

    @Positive
    @EqualsAndHashCode.Exclude
    private Integer id;
    @Email(message = "Invalid e-mail")
    private String email;
    @Pattern(regexp = "[^' ']*", message = "Invalid login")
    private String login;
    private String name;
    @PastOrPresent
    private LocalDate birthday;

}