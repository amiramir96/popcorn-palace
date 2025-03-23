
package com.att.tdp.popcorn_palace.integration_tests;

import com.att.tdp.popcorn_palace.entities.Booking;
import com.att.tdp.popcorn_palace.entities.Movie;
import com.att.tdp.popcorn_palace.entities.Showtime;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import jakarta.transaction.Transactional;
import org.springframework.test.web.servlet.MvcResult;

import java.time.Instant;
import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class BookingControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private Movie sampleMovie;
    private Showtime sampleShowtime;
    private Booking validBooking;

    @BeforeEach
    void setUp() {
        sampleMovie = new Movie();
        sampleMovie.setTitle("Sample Movie Integration");
        sampleMovie.setGenre("Action");
        sampleMovie.setDuration(120);
        sampleMovie.setRating(8.7F);
        sampleMovie.setReleaseYear(2025);

        sampleShowtime = new Showtime();
        sampleShowtime.setPrice(50.0);
        sampleShowtime.setMovieId(1L);
        sampleShowtime.setTheater("Test Theater");
        sampleShowtime.setStartTime(Instant.now().plusSeconds(3600));
        sampleShowtime.setEndTime(Instant.now().plusSeconds(7200));

        validBooking = new Booking();
        validBooking.setSeatNumber(15);
        validBooking.setUserId(UUID.randomUUID().toString());
    }

    @Test
    void addBooking_ShouldReturnOk() throws Exception {
        MvcResult movieResult = mockMvc.perform(post("/movies")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(sampleMovie)))
                .andExpect(status().isOk()).andReturn();

        String responseMovieResult = movieResult.getResponse().getContentAsString();
        sampleShowtime.setMovieId(objectMapper.readTree(responseMovieResult).get("id").asLong());

        MvcResult responseResult = mockMvc.perform(post("/showtimes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(sampleShowtime)))
                .andExpect(status().isOk()).andReturn();

        String responseBody = responseResult.getResponse().getContentAsString();
        BookingTestHelperClass addedBooking = new BookingTestHelperClass();
        addedBooking.seatNumber = validBooking.getSeatNumber();
        addedBooking.userId = validBooking.getUserId();
        addedBooking.showtimeId = objectMapper.readTree(responseBody).get("id").asLong();

        mockMvc.perform(post("/bookings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(addedBooking)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.bookingId").exists());
    }

    @Test
    void addBooking_ShouldReturnBadRequest_BookingFails() throws Exception {
        BookingTestHelperClass failedBooking = new BookingTestHelperClass();
        failedBooking.setShowtimeId(10L);
        failedBooking.setSeatNumber(15);
        failedBooking.setUserId(UUID.randomUUID().toString());

        mockMvc.perform(post("/bookings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(failedBooking)))
                .andExpect(status().isBadRequest());
    }
}
