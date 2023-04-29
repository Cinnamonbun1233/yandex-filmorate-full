package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.EmailLoginAlreadyUsed;
import ru.yandex.practicum.filmorate.exception.FriendToYourselfException;
import ru.yandex.practicum.filmorate.exception.IncorrectIdException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import java.util.List;

@RestController
@RequestMapping("/users")
@Validated
@RequiredArgsConstructor
@SuppressWarnings("unused")
public class UserController {

    private final UserService userService;

    // USERS
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public User postUser(@Valid @RequestBody User user) {

        // validation
        if (user.getId() != null) {
            throw new IncorrectIdException("User ID must be empty");
        }

        // add user
        return userService.addUser(user);

    }

    @PutMapping
    public User putUser(@Valid @RequestBody User user) {

        // validation
        if (user.getId() == null) {
            throw new IncorrectIdException("User ID must be not empty");
        }

        // update user
        return userService.updateUser(user);

    }

    @GetMapping("/{id}")
    public User getUser(@PathVariable @Positive Long id) {

        return userService.getUser(id);

    }

    @GetMapping
    public List<User> getUsers() {

        return userService.getUsers();

    }

    @DeleteMapping("/{userId}")
    public void deleteUserById(@PathVariable Long userId) {
        userService.deleteUserById(userId);
    }

    // FRIENDS
    @PutMapping("{id}/friends/{friendId}")
    public void putConnection(@PathVariable @Positive Long id, @PathVariable /*@Positive*/ Long friendId) {

        if (id.equals(friendId)) {
            throw new FriendToYourselfException("One can't add himself/herself as a friend");
        }

        // add connection
        userService.addConnection(id, friendId);

    }

    @GetMapping("{id}/friends")
    public List<User> getUsersFriends(@PathVariable @Positive Long id) {

        return userService.getUsersFriends(id);

    }

    @GetMapping("{id}/friends/common/{otherId}")
    public List<User> getCommonFriends(@PathVariable @Positive Long id, @PathVariable @Positive Long otherId) {

        if (id.equals(otherId)) {
            throw new FriendToYourselfException("First user id equals second user id");
        }

        // get common friends
        return userService.getCommonFriends(id, otherId);

    }

    @DeleteMapping("/{id}/friends/{friendId}")
    public void deleteConnection(@PathVariable @Positive Long id, @PathVariable @Positive Long friendId) {

        if (id.equals(friendId)) {
            throw new FriendToYourselfException("First user id equals second user id");
        }

        // delete connection
        userService.deleteConnection(id, friendId);

    }


    // ERRORS HANDLING
    @ExceptionHandler({EmailLoginAlreadyUsed.class, FriendToYourselfException.class})
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorResponse handleEmailLoginAlreadyUsed(final RuntimeException e) {
        return new ErrorResponse(HttpStatus.CONFLICT, "Conflict operation", e.getMessage());
    }

}