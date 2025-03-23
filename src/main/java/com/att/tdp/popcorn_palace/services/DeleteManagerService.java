package com.att.tdp.popcorn_palace.services;

import com.att.tdp.popcorn_palace.entities.Movie;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DeleteManagerService {

    private final MovieService movieService;
    private final ShowtimeService showtimeService;
    private final BookingService bookingService;

    public DeleteManagerService(MovieService movieService, ShowtimeService showtimeService, BookingService bookingService) {
        this.movieService = movieService;
        this.showtimeService = showtimeService;
        this.bookingService = bookingService;
    }

    public boolean deleteShowtimeByShowtimeId(Long showtimeId){
        if (this.showtimeService.getShowtimeById(showtimeId) == null){
            return false;
        }

        this.bookingService.deleteAllBookingsByShowtimeId(showtimeId);
        this.showtimeService.deleteShowtimeById(showtimeId);
        return true;
    }

    public boolean deleteMovieByTitle(String movieTitle){
        Movie movieToDelete = this.movieService.findMovieByTitle(movieTitle);
        if (movieToDelete == null){
            return false;
        }

        List<Long> showtimeIds = this.showtimeService.findShowtimeIdsByMovieId(movieToDelete.getId());
        if (!showtimeIds.isEmpty()){
            this.bookingService.deleteAllBookingsByShowtimeIds(showtimeIds);
            this.showtimeService.deleteAllByMovieId(movieToDelete.getId());
        }

        this.movieService.deleteMovieByTitle(movieTitle);
        return true;
    }
}
