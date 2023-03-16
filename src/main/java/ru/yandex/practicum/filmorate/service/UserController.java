package ru.yandex.practicum.filmorate.service;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.InMemoryStorage;

import javax.validation.Valid;
import javax.validation.ValidationException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController {

    InMemoryStorage inMemoryStorage = InMemoryStorage.getInstance();

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public User postUser(@Valid @RequestBody User user) {

        // no storage checking
        if (user.getId() != null) {
            throw new ResponseStatusException(400, "User ID must be empty", new ValidationException());
        }

        // storage checking
        String email = user.getEmail();
        String login = user.getLogin();

        boolean emailAlreadyUsed = inMemoryStorage.emailAlreadyUsed(email);
        boolean loginAlreadyUsed = inMemoryStorage.loginAlreadyUsed(login);

        if (emailAlreadyUsed || loginAlreadyUsed) {
            List<String> warningList = new ArrayList<>();
            if (emailAlreadyUsed) {
                warningList.add("E-mail: " + email + " is already used");
            }
            if (loginAlreadyUsed) {
                warningList.add("Login: " + login + " is already used");
            }
            String warning = String.join(", ", warningList);
            throw new ResponseStatusException(409, warning, new ValidationException());
        }

        // add
        setNameAsLogin(user);
        User result = inMemoryStorage.addUser(user, true);
        assert result != null;
        ResponseEntity.status(201);
        return result;

    }

    @PutMapping
    public User putUser(@RequestBody User user) {

        // no storage checking
        if (user.getId() == null) {
            throw new ResponseStatusException(400, "User ID must be not empty", new ValidationException());
        }

        // storage checking
        int id = user.getId();
        String email = user.getEmail();
        String login = user.getLogin();

        boolean idNotFound = (inMemoryStorage.getUser(id) == null);
        if (idNotFound) {
            throw new ResponseStatusException(404, "ID: " + id + " not found", new ValidationException());
        }

        boolean emailAlreadyUsed = inMemoryStorage.emailAlreadyUsed(email, user.getId());
        boolean loginAlreadyUsed = inMemoryStorage.loginAlreadyUsed(login, user.getId());
        if (emailAlreadyUsed || loginAlreadyUsed) {
            List<String> warningList = new ArrayList<>();
            if (emailAlreadyUsed) {
                warningList.add("E-mail: " + email + " is already used");
            }
            if (loginAlreadyUsed) {
                warningList.add("Login: " + login + " is already used");
            }
            String warning = String.join(", ", warningList);
            throw new ResponseStatusException(409, warning, new ValidationException());
        }

        // update
        setNameAsLogin(user);
        User result = inMemoryStorage.updateUser(user, true);
        assert result != null;
        return result;

    }

    @GetMapping
    public Collection<User> getUsers() {
        return inMemoryStorage.getUsers();
    }

    private void setNameAsLogin(User user) {
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
    }

}