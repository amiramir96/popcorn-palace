package com.att.tdp.popcorn_palace.integration_tests;

import com.att.tdp.popcorn_palace.entities.Movie;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class MovieControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private Movie sampleMovie;
    private Movie validMovie1;
    private Movie validMovie2;

    @BeforeEach
    void setUp() {
        sampleMovie = new Movie();
        sampleMovie.setTitle("Sample Movie Integration");
        sampleMovie.setGenre("Action");
        sampleMovie.setDuration(120);
        sampleMovie.setRating(8.7F);
        sampleMovie.setReleaseYear(2025);

        validMovie1 = new Movie();
        validMovie1.setTitle("Sample Movie Test1");
        validMovie1.setGenre("Action");
        validMovie1.setDuration(120);
        validMovie1.setRating(8.7F);
        validMovie1.setReleaseYear(2025);

        validMovie2 = new Movie();
        validMovie2.setTitle("Sample Movie Test2");
        validMovie2.setGenre("Drama");
        validMovie2.setDuration(120);
        validMovie2.setRating(9F);
        validMovie2.setReleaseYear(2025);
    }

    @Test
    void addMovie_ShouldReturnOk_MovieIsValid() throws Exception {
        mockMvc.perform(post("/movies")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(sampleMovie)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Sample Movie Integration"));
    }

    @Test
    void addMovie_ShouldReturnBadRequest_MovieExistsInDb() throws Exception {
        mockMvc.perform(post("/movies")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(sampleMovie)));

        mockMvc.perform(post("/movies")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(sampleMovie)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getAllMovies_ShouldReturnListOfMovies() throws Exception {
        mockMvc.perform(post("/movies")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(sampleMovie)));

        mockMvc.perform(get("/movies/all"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].title").value("Sample Movie Integration"))
                .andExpect(jsonPath("$", hasSize(1)));

        mockMvc.perform(post("/movies")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(validMovie1)));

        mockMvc.perform(get("/movies/all"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$", hasSize(2)));
    }

    @Test
    void updateMovieByTitle_ShouldReturnOk() throws Exception {
        mockMvc.perform(post("/movies")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(sampleMovie)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Sample Movie Integration"));

        mockMvc.perform(post("/movies/update/{movieTitle}", sampleMovie.getTitle())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(validMovie1)))
                .andExpect(status().isOk());
    }

    @Test
    void addMovie_ShouldReturnNotFound_MovieDoesntExistsInDb() throws Exception {
        mockMvc.perform(post("/movies")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(sampleMovie)))
                .andExpect(status().isOk());

        mockMvc.perform(post("/movies/update/{movieTitle}", validMovie1.getTitle())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validMovie1)))
                .andExpect(status().isNotFound());
    }

    @Test
    void deleteMovieByTitle_ShouldReturnOk_WhenMovieExists() throws Exception {
        mockMvc.perform(post("/movies")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(sampleMovie)))
                .andExpect(status().isOk());

        mockMvc.perform(delete("/movies/{movieTitle}", sampleMovie.getTitle()))
                .andExpect(status().isOk());
    }

    @Test
    void deleteMovieByTitle_ShouldReturnNotFound_WhenMovieDoesNotExist() throws Exception {
        mockMvc.perform(delete("/movies/{movieTitle}", "Nothing"))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Movie Nothing not found"));
    }
}
