package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;

import java.util.*;

public class InMemoryStorage {

    private static final InMemoryStorage instance = new InMemoryStorage();

    public void deleteAllData() {
        idUser.clear();
        emailUser.clear();
        loginUser.clear();
        idFilm.clear();
        filmSet.clear();
        userId = 0;
        filmId = 0;
    }

    // users
    private int userId = 0;
    private final Map<Integer, User> idUser = new HashMap<>();
    private final Map<String, User> emailUser = new HashMap<>();
    private final Map<String, User> loginUser = new HashMap<>();

    // films
    private int filmId = 0;
    private final Map<Integer, Film> idFilm = new HashMap<>();
    private final Set<Film> filmSet = new HashSet<>();

    public static InMemoryStorage getInstance() {
        return instance;
    }


    // Users. Checking.

    public boolean emailAlreadyUsed(String email) {
        return emailUser.containsKey(email);
    }

    public boolean emailAlreadyUsed(String email, int excludedUserId) {
        User user = emailUser.get(email);
        return user != null && excludedUserId != user.getId();
    }

    public boolean loginAlreadyUsed(String login) {
        return loginUser.containsKey(login);
    }

    public boolean loginAlreadyUsed(String login, int excludedUserId) {
        User user = loginUser.get(login);
        return user != null && excludedUserId != user.getId();
    }

    public User getUser(int id) {
        return idUser.get(id);
    }


    // Users. CRUD.

    public User addUser(User user, boolean forced) {
        if (forced
                || (!emailAlreadyUsed(user.getEmail()) && !loginAlreadyUsed(user.getLogin()))) {
            int currentId = getUserId();
            user.setId(currentId);
            idUser.put(currentId, user);
            emailUser.put(user.getEmail(), user);
            loginUser.put(user.getLogin(), user);
            return user;
        } else {
            return null;
        }
    }

    public User updateUser(User user, boolean forced) {
        if (forced
            || (idUser.containsKey(user.getId())
                && !emailAlreadyUsed(user.getEmail(), user.getId())
                && !loginAlreadyUsed(user.getLogin(), user.getId()))) {
            idUser.put(user.getId(), user);
            emailUser.put(user.getEmail(), user);
            loginUser.put(user.getLogin(), user);
            return user;
        } else {
            return null;
        }
    }

    public Collection<User> getUsers() {
        return idUser.values();
    }



    // Films. CRUD.

    public Film getFilm(int id) {
        return idFilm.get(id);
    }

    public boolean hasFilm(Film film){
        return filmSet.contains(film);
    }

    public Film addFilm(Film film, boolean forced) {
        if (forced || !hasFilm(film)) {
            int currentId = getFilmId();
            film.setId(currentId);
            idFilm.put(currentId, film);
            filmSet.add(film);
            return film;
        } else {
            return null;
        }
    }

    public Film updateFilm(Film film, boolean forced) {
        if (forced || !hasFilm(film)) {
            filmSet.remove(idFilm.get(film.getId()));
            filmSet.add(film);
            idFilm.put(film.getId(), film);
            return film;
        } else {
            return null;
        }
    }

    public Collection<Film> getFilms() {
        return filmSet;
    }


    // private

    private InMemoryStorage() {
    }

    // users
    private int getUserId(){
        return ++userId;
    }

    // films
    private int getFilmId(){
        return ++filmId;
    }


}