
package com.att.tdp.popcorn_palace.services_unit_tests;

import com.att.tdp.popcorn_palace.entities.Movie;
import com.att.tdp.popcorn_palace.entities.Showtime;
import com.att.tdp.popcorn_palace.services.BookingService;
import com.att.tdp.popcorn_palace.services.DeleteManagerService;
import com.att.tdp.popcorn_palace.services.MovieService;
import com.att.tdp.popcorn_palace.services.ShowtimeService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class DeleteManagerServiceTests {

    @Mock
    private MovieService movieService;

    @Mock
    private ShowtimeService showtimeService;

    @Mock
    private BookingService bookingService;

    @InjectMocks
    private DeleteManagerService deleteManagerService;

    private Movie movieToDelete;

    @BeforeEach
    void setUp() {
        movieToDelete = new Movie();
        movieToDelete.setId(1L);
        movieToDelete.setTitle("Test Movie");
    }

    @Test
    void deleteMovieByTitle_ShouldSucceed_Valid() {
        when(movieService.findMovieByTitle("Test Movie")).thenReturn(movieToDelete);
        when(showtimeService.findShowtimeIdsByMovieId(1L)).thenReturn(List.of(11L, 12L));
        when(bookingService.deleteAllBookingsByShowtimeIds(List.of(11L, 12L))).thenReturn(true);
        when(showtimeService.deleteAllByMovieId(1L)).thenReturn(true);
        when(movieService.deleteMovieByTitle("Test Movie")).thenReturn(true);

        boolean deleteSucceed = deleteManagerService.deleteMovieByTitle("Test Movie");

        assertTrue(deleteSucceed);
    }

    @Test
    void deleteMovieByTitle_ShouldFail_MovieNotFound() {
        when(movieService.findMovieByTitle("Fake Movie")).thenReturn(null);

        boolean deleteSucceed = deleteManagerService.deleteMovieByTitle("Fake Movie");

        assertFalse(deleteSucceed);
    }

    @Test
    void deleteShowtimeById_ShouldSucceed_ShowtimeExists() {
        when(showtimeService.getShowtimeById(11L)).thenReturn(new Showtime());
        when(bookingService.deleteAllBookingsByShowtimeId(11L)).thenReturn(true);
        when(showtimeService.deleteShowtimeById(11L)).thenReturn(true);

        boolean deleteSucceed = deleteManagerService.deleteShowtimeByShowtimeId(11L);

        assertTrue(deleteSucceed);
    }

    @Test
    void deleteShowtimeById_ShouldFail_ShowtimeNotFound() {
        when(showtimeService.getShowtimeById(99L)).thenReturn(null);

        boolean result = deleteManagerService.deleteShowtimeByShowtimeId(99L);

        assertFalse(result);
    }
}
