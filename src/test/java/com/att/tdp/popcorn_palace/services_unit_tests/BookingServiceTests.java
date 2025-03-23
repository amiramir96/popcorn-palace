package com.att.tdp.popcorn_palace.services_unit_tests;

import com.att.tdp.popcorn_palace.entities.Booking;
import com.att.tdp.popcorn_palace.entities.Showtime;
import com.att.tdp.popcorn_palace.repositories.BookingRepository;
import com.att.tdp.popcorn_palace.services.BookingService;
import com.att.tdp.popcorn_palace.services.ShowtimeService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookingServiceTest {

    @Mock
    private ShowtimeService showtimeService;

    @Mock
    private BookingRepository bookingRepository;

    @InjectMocks
    private BookingService bookingService;

    private Booking validBooking;
    private Showtime validShowtime;

    @BeforeEach
    void setUp() {
        validBooking = new Booking(15, "84438967-f68f-4fa0-b620-0f08217e76af", 1L);
        validShowtime = new Showtime();
        validShowtime.setId(1L);
        validShowtime.setStartTime(Instant.now().plusSeconds(3600)); // שעה קדימה
    }

    @Test
    void addBooking_ShouldSucceed_Valid() {
        when(bookingRepository.findByShowtimeIdAndSeatNumber(1L, 15))
                .thenReturn(Optional.empty());
        when(showtimeService.getShowtimeById(1L)).thenReturn(validShowtime);
        when(bookingRepository.save(any(Booking.class))).thenReturn(validBooking);

        Booking addedBooking = bookingService.addBooking(validBooking);

        assertNotNull(addedBooking);
        assertEquals(validBooking.getSeatNumber(), addedBooking.getSeatNumber());
    }

    @Test
    void addBooking_ShouldFail_ShowtimeSeatAlreadyTaken() {
        when(bookingRepository.findByShowtimeIdAndSeatNumber(1L, 15))
                .thenReturn(Optional.of(new Booking()));

        Booking result = bookingService.addBooking(validBooking);

        assertNull(result);
    }

    @Test
    void addBooking_ShouldFail_ShowtimeDoesNotExist() {
        when(bookingRepository.findByShowtimeIdAndSeatNumber(1L, 15))
                .thenReturn(Optional.empty());
        when(showtimeService.getShowtimeById(1L)).thenReturn(null);

        Booking result = bookingService.addBooking(validBooking);

        assertNull(result);
    }

    @Test
    void addBooking_ShouldFail_ShowtimeAlreadyStarted() {
        when(bookingRepository.findByShowtimeIdAndSeatNumber(1L, 15))
                .thenReturn(Optional.empty());
        when(showtimeService.getShowtimeById(1L)).thenReturn(validShowtime);

        validShowtime.setStartTime(Instant.now().minusSeconds(60));

        Booking result = bookingService.addBooking(validBooking);

        assertNull(result);
    }
}
