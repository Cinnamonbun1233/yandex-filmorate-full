package ru.yandex.practicum.filmorate.database;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserDbStorage;

import java.time.LocalDate;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class UserDatabaseTests {

    @Autowired
    private final UserDbStorage userStorage;

    @BeforeEach
    public void setUp() {
        userStorage.deleteAllData();
    }

    @Test
    public void testUserCrud() {

        User user = userStorage.getUser(1L);
        assertThat(user)
                .isNull();

        Set<User> users = userStorage.getUsers();
        assertThat(users)
                .size().isZero();

        userStorage.addUser(User.builder().name("John").email("john@beatles.uk").login("john").build());
        User john = userStorage.getUser(1L);
        assertThat(john)
                .isNotNull()
                .hasFieldOrPropertyWithValue("id", 1L)
                .hasFieldOrPropertyWithValue("name", "John")
                .hasFieldOrPropertyWithValue("birthday", null);

        john.setBirthday(LocalDate.of(1940, 10, 9));
        userStorage.updateUser(john);
        assertThat(john)
                .hasFieldOrPropertyWithValue("birthday", LocalDate.of(1940, 10, 9));

        Set<User> allUsers = userStorage.getUsers();
        assertThat(allUsers)
                .size().isEqualTo(1);

    }

    @Test
    public void testUserChecking() {

        userStorage.addUser(User.builder().name("John").email("john@beatles.uk").login("john").build());

        assertThat(userStorage.emailAlreadyUsed("john@beatles.uk"))
                .isTrue();

        assertThat(userStorage.emailAlreadyUsed("john@beatles.uk", 1L))
                .isFalse();

        assertThat(userStorage.emailAlreadyUsed("paul@beatles.uk"))
                .isFalse();

    }

    @Test
    public void testFriendCrud() {

        userStorage.addUser(User.builder().name("John").email("john@beatles.uk").login("john").build());
        userStorage.addUser(User.builder().name("Paul").email("paul@beatles.uk").login("paul").build());
        userStorage.addFriend(1L, 2L);

        // getFriends
        assertThat(userStorage.getFriends(1L))
                .size().isEqualTo(1);

        assertThat(userStorage.getFriends(2L))
                .size().isZero();

        // hasConnection
        assertThat(userStorage.hasConnection(1L, 2L))
                .isTrue();

        assertThat(userStorage.hasConnection(2L, 1L))
                .isFalse();

        // deleteFriend
        assertThat(userStorage.deleteFriend(1L, 2L))
                .isTrue();

        assertThat(userStorage.deleteFriend(2L, 1L))
                .isFalse();

        // delete (unsuccessfully)
        assertThat(userStorage.deleteFriend(1L, 2L))
                .isFalse();


    }

}