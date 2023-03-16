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
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.InMemoryStorage;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class FilmControllerTest {

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
    public void getFilmsTest() {

        // post user
        String validFilm =
                        "{\"name\": \"Movie1\", " +
                        "\"description\": \"Description1\", " +
                        "\"releaseDate\": \"2000-01-01\", " +
                        "\"duration\": 100}";

        mockMvc.perform(post("/films")
                        .contentType("application/json")
                        .content(validFilm))
                .andExpect(status().is(201));

        String contentAsString = mockMvc.perform(get("/films")).andReturn().getResponse().getContentAsString();
        List<Film> userList = objectMapper.readValue(contentAsString, new TypeReference<List<Film>>() {});
        assertEquals(1,userList.size());

    }

    // POST
    @Test
    @SneakyThrows
    public void postFilm() {

        String validFilm =
                        "{\"name\": \"Movie1\", " +
                        "\"description\": \"Description1\", " +
                        "\"releaseDate\": \"2000-01-01\", " +
                        "\"duration\": 100}";

        mockMvc.perform(post("/films")
                        .contentType("application/json")
                        .content(validFilm))
                .andExpect(status().is(201));

    }

    @Test
    @SneakyThrows
    public void postFilmFail_EmptyName_LongDescription_InvalidReleaseDate_NegativeDuration() {

        // EmptyName
        String emptyNameFilm =
                "{\"name\": \"\", " +
                        "\"description\": \"Description1\", " +
                        "\"releaseDate\": \"2000-01-01\", " +
                        "\"duration\": 100}";

        mockMvc.perform(post("/films")
                        .contentType("application/json")
                        .content(emptyNameFilm))
                .andExpect(status().is(400));

        // LongDescription
        String longDescriptionFilm =
                "{\"name\": \"Movie1\", " +
                        "\"description\": \"Description11111111111111111111111111111111111111111111111111111111" +
                        "11111111111111111111111111111111111111111111111111111111111111111111111111111111111111" +
                        "11111111111111111111111111111111111111111111111111111111111111111111111111111111111111\", " +
                        "\"releaseDate\": \"2000-01-01\", " +
                        "\"duration\": 100}";

        mockMvc.perform(post("/films")
                        .contentType("application/json")
                        .content(longDescriptionFilm))
                .andExpect(status().is(400));

        // InvalidReleaseDate
        String invalidReleaseDateFilm =
                "{\"name\": \"Movie1\", " +
                        "\"description\": \"Description1\", " +
                        "\"releaseDate\": \"1800-01-01\", " +
                        "\"duration\": 100}";

        mockMvc.perform(post("/films")
                        .contentType("application/json")
                        .content(invalidReleaseDateFilm))
                .andExpect(status().is(400));

        // NegativeDuration
        String negativeDurationFilm =
                "{\"name\": \"Movie1\", " +
                        "\"description\": \"Description1\", " +
                        "\"releaseDate\": \"2000-01-01\", " +
                        "\"duration\": -100}";

        mockMvc.perform(post("/films")
                        .contentType("application/json")
                        .content(negativeDurationFilm))
                .andExpect(status().is(400));

    }

    @Test
    @SneakyThrows
    public void postFilm_FilmHasATwin() {

        String validFilm =
                        "{\"name\": \"Movie1\", " +
                        "\"description\": \"Description1\", " +
                        "\"releaseDate\": \"2000-01-01\", " +
                        "\"duration\": 100}";

        mockMvc.perform(post("/films")
                        .contentType("application/json")
                        .content(validFilm))
                .andExpect(status().is(201));

        String twinFilm =
                        "{\"name\": \"Movie1\", " +
                        "\"description\": \"Description1\", " +
                        "\"releaseDate\": \"2000-01-01\", " +
                        "\"duration\": 100}";

        mockMvc.perform(post("/films")
                        .contentType("application/json")
                        .content(twinFilm))
                .andExpect(status().is(409));

    }


    // PUT
    @Test
    @SneakyThrows
    public void putFilm() {

        // post film
        String validFilm =
                        "{\"name\": \"Movie1\", " +
                        "\"description\": \"Description1\", " +
                        "\"releaseDate\": \"2000-01-01\", " +
                        "\"duration\": 100}";

        mockMvc.perform(post("/films")
                        .contentType("application/json")
                        .content(validFilm))
                .andExpect(status().is(201));

        // put film
        String filmToUpdate =
                        "{\"id\" : 1, " +
                        "\"name\": \"Movie1\", " +
                        "\"description\": \"Description1 (update)\", " +
                        "\"releaseDate\": \"2000-01-01\", " +
                        "\"duration\": 100}";

        mockMvc.perform(put("/films")
                        .contentType("application/json")
                        .content(filmToUpdate))
                .andExpect(status().is(200));

    }

    @Test
    @SneakyThrows
    public void putFilmFail_EmptyId_IDNotFound_FilmHasATwin() {

        // ID empty
        String emptyFilm =
                        "{\"id\" : , " +
                        "{\"name\": \"Movie1\", " +
                        "\"description\": \"Description1\", " +
                        "\"releaseDate\": \"2000-01-01\", " +
                        "\"duration\": 100}";

        mockMvc.perform(put("/films")
                        .contentType("application/json")
                        .content(emptyFilm))
                .andExpect(status().is(400));

        // ID not found
        String notFoundIdFilm =
                        "{\"id\" : 999, " +
                        "\"name\": \"Movie1\", " +
                        "\"description\": \"Description1\", " +
                        "\"releaseDate\": \"2000-01-01\", " +
                        "\"duration\": 100}";

        mockMvc.perform(put("/films")
                        .contentType("application/json")
                        .content(notFoundIdFilm))
                .andExpect(status().is(404));


        // Film has a twin
        // - post film 1
        String validFilm1 =
                        "{\"name\": \"Movie1\", " +
                        "\"description\": \"Description1\", " +
                        "\"releaseDate\": \"2000-01-01\", " +
                        "\"duration\": 100}";

        mockMvc.perform(post("/films")
                        .contentType("application/json")
                        .content(validFilm1))
                .andExpect(status().is(201));

        // - post film 2
        String validFilm2 =
                        "{\"name\": \"Movie2\", " +
                        "\"description\": \"Description2\", " +
                        "\"releaseDate\": \"2000-01-01\", " +
                        "\"duration\": 100}";

        mockMvc.perform(post("/films")
                        .contentType("application/json")
                        .content(validFilm2))
                .andExpect(status().is(201));


        // - put film
        String twinFilm =
                        "{\"id\" : 2, " +
                        "\"name\": \"Movie1\", " +
                        "\"description\": \"Description1\", " +
                        "\"releaseDate\": \"2000-01-01\", " +
                        "\"duration\": 100}";

        mockMvc.perform(put("/films")
                        .contentType("application/json")
                        .content(twinFilm))
                .andExpect(status().is(409));

    }

}
