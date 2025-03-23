package com.att.tdp.popcorn_palace.integration_tests;

import com.att.tdp.popcorn_palace.entities.Movie;
import com.att.tdp.popcorn_palace.entities.Showtime;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class ShowtimeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private Movie sampleMovie;
    private Showtime sampleShowtime;
    private Showtime conflictsShowtime;

    @BeforeEach
    void setUp() throws Exception {
        sampleMovie = new Movie();
        sampleMovie.setTitle("Sample Movie Integration");
        sampleMovie.setGenre("Action");
        sampleMovie.setDuration(120);
        sampleMovie.setRating(8.7F);
        sampleMovie.setReleaseYear(2025);

        sampleShowtime = new Showtime();
        sampleShowtime.setPrice(50.0);
        sampleShowtime.setTheater("Test Theater");
        sampleShowtime.setStartTime(Instant.now().plusSeconds(3600));
        sampleShowtime.setEndTime(Instant.now().plusSeconds(7200));

        conflictsShowtime = new Showtime();
        conflictsShowtime.setPrice(33.0);
        conflictsShowtime.setTheater("Test Theater");
        conflictsShowtime.setStartTime(Instant.now().plusSeconds(4000));
        conflictsShowtime.setEndTime(Instant.now().plusSeconds(7300));

        // have to hold movie in db so sampleShowtime would be able to be added
        MvcResult movieResult = mockMvc.perform(post("/movies")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(sampleMovie)))
                .andExpect(status().isOk()).andReturn();

        String responseMovieResult = movieResult.getResponse().getContentAsString();
        sampleShowtime.setMovieId(objectMapper.readTree(responseMovieResult).get("id").asLong());
        conflictsShowtime.setMovieId(objectMapper.readTree(responseMovieResult).get("id").asLong());
    }

    @Test
    void addShowtime_ShouldReturnOk() throws Exception {
        mockMvc.perform(post("/showtimes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(sampleShowtime)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.theater").value("Test Theater"));
    }

    @Test
    void addShowtime_ShouldReturnBadRequest_ShowtimeConflictsExistingShowtime() throws Exception {
        mockMvc.perform(post("/showtimes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(sampleShowtime)))
                .andExpect(status().isOk());

        mockMvc.perform(post("/showtimes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(conflictsShowtime)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Showtime not added, already exists OR conflicts with another existing showtimes"));
    }

    @Test
    void addShowtime_ShouldReturnBadRequest_ShowtimeExists() throws Exception {
        mockMvc.perform(post("/showtimes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(sampleShowtime)))
                .andExpect(status().isOk());

        mockMvc.perform(post("/showtimes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(sampleShowtime)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Showtime not added, already exists OR conflicts with another existing showtimes"));
    }

    @Test
    void getShowtimeById_ShouldReturnOk() throws Exception {
        MvcResult responseResult = mockMvc.perform(post("/showtimes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(sampleShowtime)))
                .andExpect(status().isOk()).andReturn();

        String responseBody = responseResult.getResponse().getContentAsString();

        mockMvc.perform(get("/showtimes/{showtimeId}", objectMapper.readTree(responseBody).get("id").asLong()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.theater").value("Test Theater"));
    }

    @Test
    void getShowtimeById_ShouldReturnNotFound_NotExists() throws Exception {
        mockMvc.perform(get("/showtimes/{showtimeId}", 10L))
                .andExpect(status().isNotFound());
    }

    @Test
    void updateShowtime_ShouldReturnOk() throws Exception {
        MvcResult responseResult = mockMvc.perform(post("/showtimes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(sampleShowtime)))
                .andExpect(status().isOk()).andReturn();

        String responseBody = responseResult.getResponse().getContentAsString();

        mockMvc.perform(post("/showtimes/update/{showtimeId}", objectMapper.readTree(responseBody).get("id").asLong())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(conflictsShowtime)))
                .andExpect(status().isOk());
    }

    @Test
    void updateShowtime_ShouldReturnBadRequest_NotExists() throws Exception {
        mockMvc.perform(post("/showtimes/update/{showtimeId}", 10L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(sampleShowtime)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void deleteShowtime_ShouldReturnOk() throws Exception {
        MvcResult responseResult = mockMvc.perform(post("/showtimes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(sampleShowtime)))
                .andExpect(status().isOk()).andReturn();

        String responseBody = responseResult.getResponse().getContentAsString();

        mockMvc.perform(delete("/showtimes/{showtimeId}", objectMapper.readTree(responseBody).get("id").asLong()))
                .andExpect(status().isOk());
    }

    @Test
    void deleteShowtime_ShouldReturnNotFound_NotExists() throws Exception {
        mockMvc.perform(delete("/showtimes/{showtimeId}", 10L))
                .andExpect(status().isNotFound());
    }
}
