package ru.yandex.practicum.filmorate;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.InMemoryStorage;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    ObjectMapper objectMapper;

    @BeforeEach
    public void setUp() {
        InMemoryStorage.getInstance().deleteAllData();
    }

    // GET
    @Test
    @SneakyThrows
    public void getUsersTest() {

        // post user
        String validUser =
                        "{\"login\": \"login\", " +
                        "\"name\": \"Name\", " +
                        "\"email\": \"mail@mail.ru\", " +
                        "\"birthday\": \"2000-01-01\"}";

        mockMvc.perform(post("/users")
                        .contentType("application/json")
                        .content(validUser))
                .andExpect(status().is(201));

        String contentAsString = mockMvc.perform(get("/users")).andReturn().getResponse().getContentAsString();
        List<User> userList = objectMapper.readValue(contentAsString, new TypeReference<List<User>>() {});
        assertEquals(1,userList.size());

    }

    // POST
    @Test
    @SneakyThrows
    public void postUser() {

        String validUser =
                "{\"login\": \"login\", " +
                        "\"name\": \"Name\", " +
                        "\"email\": \"mail@mail.ru\", " +
                        "\"birthday\": \"2000-01-01\"}";

        mockMvc.perform(post("/users")
                .contentType("application/json")
                .content(validUser))
                .andExpect(status().is(201));

    }

    @Test
    @SneakyThrows
    public void postUserFail_BadEmail_BadLogin_BadBirthday() {

        // BadEmail
        String badEmailUser =
                        "{\"login\": \"login\", " +
                        "\"name\": \"Name\", " +
                        "\"email\": \"mail.ru\", " +
                        "\"birthday\": \"2000-01-01\"}";

        mockMvc.perform(post("/users")
                        .contentType("application/json")
                        .content(badEmailUser))
                .andExpect(status().is(400));

        // BadLogin
        String badLoginUser =
                        "{\"login\": \"login with whitspaces\", " +
                        "\"name\": \"Name\", " +
                        "\"email\": \"mail@mail.ru\", " +
                        "\"birthday\": \"2000-01-01\"}";

        mockMvc.perform(post("/users")
                        .contentType("application/json")
                        .content(badLoginUser))
                .andExpect(status().is(400));

        // BadBirthday
        String badBirthdayUser =
                        "{\"login\": \"login\", " +
                        "\"name\": \"Name\", " +
                        "\"email\": \"mail@mail.ru\", " +
                        "\"birthday\": \"2024-01-01\"}";

        mockMvc.perform(post("/users")
                        .contentType("application/json")
                        .content(badBirthdayUser))
                .andExpect(status().is(400));


        // id present
        String idPresentUser =
                        "{\"id\" : 100, " +
                        "{\"login\": \"login\", " +
                        "\"name\": \"Name\", " +
                        "\"email\": \"mail@mail.ru\", " +
                        "\"birthday\": \"2000-01-01\"}";

        mockMvc.perform(post("/users")
                        .contentType("application/json")
                        .content(idPresentUser))
                .andExpect(status().is(400));

    }

    @Test
    @SneakyThrows
    public void postUserFail_EmailAlreadyUsed_LoginAlreadyUsed() {

        String validUser =
                        "{\"login\": \"login\", " +
                        "\"name\": \"Name\", " +
                        "\"email\": \"mail@mail.ru\", " +
                        "\"birthday\": \"2000-01-01\"}";

        mockMvc.perform(post("/users")
                        .contentType("application/json")
                        .content(validUser))
                .andExpect(status().is(201));

        // EmailAlreadyUsed
        String EmailAlreadyUsedUser =
                        "{\"login\": \"login2\", " +
                        "\"name\": \"Name\", " +
                        "\"email\": \"mail@mail.ru\", " +
                        "\"birthday\": \"2000-01-01\"}";

        mockMvc.perform(post("/users")
                        .contentType("application/json")
                        .content(EmailAlreadyUsedUser))
                .andExpect(status().is(409));

        // LoginAlreadyUsed
        String LoginAlreadyUsedUser =
                        "{\"login\": \"login\", " +
                        "\"name\": \"Name\", " +
                        "\"email\": \"mail1@mail.ru\", " +
                        "\"birthday\": \"2000-01-01\"}";

        mockMvc.perform(post("/users")
                        .contentType("application/json")
                        .content(LoginAlreadyUsedUser))
                .andExpect(status().is(409));

    }

    // PUT
    @Test
    @SneakyThrows
    public void putUser() {

        // post user
        String validUser =
                "{\"login\": \"login\", " +
                        "\"name\": \"Name\", " +
                        "\"email\": \"mail@mail.ru\", " +
                        "\"birthday\": \"2000-01-01\"}";

        mockMvc.perform(post("/users")
                        .contentType("application/json")
                        .content(validUser));


        // put user
        String userToUpdate =
                        "{\"id\" : 1, " +
                        "\"login\": \"login\", " +
                        "\"name\": \"Name (updated)\", " +
                        "\"email\": \"mail@mail.ru\", " +
                        "\"birthday\": \"2000-01-01\"}";

        mockMvc.perform(put("/users")
                        .contentType("application/json")
                        .content(userToUpdate))
                .andExpect(status().is(200));

    }

    @Test
    @SneakyThrows
    public void putUser_IdNotFound_IdIsEmpty() {

        String IdIsEmptyUser =
                        "{\"login\": \"login\", " +
                        "\"name\": \"Name\", " +
                        "\"email\": \"mail@mail.ru\", " +
                        "\"birthday\": \"2000-01-01\"}";

        mockMvc.perform(put("/users")
                        .contentType("application/json")
                        .content(IdIsEmptyUser))
                .andExpect(status().is(400));

        String badIdUser =
                        "{\"id\" : 999," +
                        "\"login\": \"login\", " +
                        "\"name\": \"Name\", " +
                        "\"email\": \"mail@mail.ru\", " +
                        "\"birthday\": \"2000-01-01\"}";

        mockMvc.perform(put("/users")
                        .contentType("application/json")
                        .content(badIdUser))
                .andExpect(status().is(404));

    }

    @Test
    @SneakyThrows
    public void putUser_EmailAlreadyUsed_LoginAlreadyUsed() {

        // validUser1
        String validUser1 =
                        "{\"login\": \"login1\", " +
                        "\"name\": \"Name1\", " +
                        "\"email\": \"mail1@mail.ru\", " +
                        "\"birthday\": \"2000-01-01\"}";

        mockMvc.perform(post("/users")
                        .contentType("application/json")
                        .content(validUser1))
                .andExpect(status().is(201));

        // validUser2
        String validUser2 =
                        "{\"login\": \"login2\", " +
                        "\"name\": \"Name2\", " +
                        "\"email\": \"mail2@mail.ru\", " +
                        "\"birthday\": \"2000-01-01\"}";

        mockMvc.perform(post("/users")
                        .contentType("application/json")
                        .content(validUser2))
                .andExpect(status().is(201));

        // EmailAlreadyUsed
        String emailAlreadyUsedUser =
                        "{\"id\" : 2," +
                        "\"login\": \"login2\", " +
                        "\"name\": \"Name2\", " +
                        "\"email\": \"mail1@mail.ru\", " +
                        "\"birthday\": \"2000-01-01\"}";

        mockMvc.perform(put("/users")
                        .contentType("application/json")
                        .content(emailAlreadyUsedUser))
                .andExpect(status().is(409));


        // LoginAlreadyUsed
        String loginAlreadyUsedUser =
                        "{\"id\" : 2," +
                        "\"login\": \"login1\", " +
                        "\"name\": \"Name2\", " +
                        "\"email\": \"mail2@mail.ru\", " +
                        "\"birthday\": \"2000-01-01\"}";

        mockMvc.perform(put("/users")
                        .contentType("application/json")
                        .content(loginAlreadyUsedUser))
                .andExpect(status().is(409));

    }

}