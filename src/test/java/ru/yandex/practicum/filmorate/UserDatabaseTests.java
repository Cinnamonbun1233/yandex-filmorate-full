package ru.yandex.practicum.filmorate;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.friend.FriendStorage;
import ru.yandex.practicum.filmorate.storage.user.UserDbStorage;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class UserDatabaseTests {

    @Autowired
    private final UserDbStorage userStorage;
    @Autowired
    private final FriendStorage friendStorage;

    @BeforeEach
    public void setUp() {
        userStorage.deleteAllData();
    }

    @Test
    public void testUserCrud() {

        User user = userStorage.getUser(1L);
        assertThat(user)
                .isNull();

        List<User> users = userStorage.getUsers();
        assertThat(users)
                .size().isZero();

        userStorage.addUser(User.builder()
                .name("John Winston Lennon")
                .email("john@beatles.uk")
                .login("john")
                .birthday(LocalDate.of(1940, 10, 9))
                .build());
        User john = userStorage.getUser(1L);
        assertThat(john)
                .isNotNull()
                .hasFieldOrPropertyWithValue("id", 1L)
                .hasFieldOrPropertyWithValue("name", "John Winston Lennon");

        john.setName("John Ono Lennon");
        userStorage.updateUser(john);
        assertThat(john)
                .hasFieldOrPropertyWithValue("name", "John Ono Lennon");

        List<User> allUsers = userStorage.getUsers();
        assertThat(allUsers)
                .size().isEqualTo(1);

    }

    @Test
    public void testUserChecking() {

        userStorage.addUser(User.builder()
                .name("John")
                .email("john@beatles.uk")
                .login("john")
                .birthday(LocalDate.of(1940, 10, 9))
                .build());

        assertThat(userStorage.emailAlreadyUsed("john@beatles.uk"))
                .isTrue();

        assertThat(userStorage.emailAlreadyUsed("john@beatles.uk", 1L))
                .isFalse();

        assertThat(userStorage.emailAlreadyUsed("paul@beatles.uk"))
                .isFalse();

    }

    @Test
    public void testFriendCrud() {

        userStorage.addUser(User.builder()
                .name("John")
                .email("john@beatles.uk")
                .login("john")
                .birthday(LocalDate.of(1940, 10, 9))
                .build());
        userStorage.addUser(User.builder()
                .name("Paul")
                .email("paul@beatles.uk")
                .login("paul")
                .birthday(LocalDate.of(1940, 10, 9))
                .build());
        friendStorage.addFriend(1L, 2L);

        // getFriends
        assertThat(friendStorage.getFriends(1L))
                .size().isEqualTo(1);

        assertThat(friendStorage.getFriends(2L))
                .size().isZero();

        // hasConnection
        assertThat(friendStorage.hasConnection(1L, 2L))
                .isTrue();

        assertThat(friendStorage.hasConnection(2L, 1L))
                .isFalse();

        // deleteFriend
        assertThat(friendStorage.deleteFriend(1L, 2L))
                .isTrue();

        assertThat(friendStorage.deleteFriend(2L, 1L))
                .isFalse();

        // delete (unsuccessfully)
        assertThat(friendStorage.deleteFriend(1L, 2L))
                .isFalse();


    }

    @Test
    public void deleteUserTest() {
        userStorage.addUser(User.builder()
                .name("John Winston Lennon")
                .email("john@beatles.uk")
                .login("john")
                .birthday(LocalDate.of(1940, 10, 9))
                .build());
        userStorage.deleteUserById(1L);
        List<User> users = userStorage.getUsers();
        assertTrue(users.isEmpty());
    }
}