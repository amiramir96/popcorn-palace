package com.att.tdp.popcorn_palace.services;

import com.att.tdp.popcorn_palace.entities.Movie;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DeleteManagerService {

    private final MovieService movieService;
    private final ShowtimeService showtimeService;
    private final BookingService bookingService;
    private final Logger logger =  LoggerFactory.getLogger(DeleteManagerService.class);

    public DeleteManagerService(MovieService movieService, ShowtimeService showtimeService, BookingService bookingService) {
        this.movieService = movieService;
        this.showtimeService = showtimeService;
        this.bookingService = bookingService;
    }

    public boolean deleteShowtimeByShowtimeId(Long showtimeId){
        if (this.showtimeService.getShowtimeById(showtimeId) == null){
            logger.warn("showtime with id {} not found, cannot delete non-existing showtime", showtimeId);
            return false;
        }

        this.bookingService.deleteAllBookingsByShowtimeId(showtimeId);
        this.showtimeService.deleteShowtimeById(showtimeId);
        return true;
    }

    public boolean deleteMovieByTitle(String movieTitle){
        Movie movieToDelete = this.movieService.findMovieByTitle(movieTitle);
        if (movieToDelete == null){
            logger.warn("movie with title {} not found, cannot delete non-existing movie", movieTitle);
            return false;
        }

        List<Long> showtimeIds = this.showtimeService.findShowtimeIdsByMovieId(movieToDelete.getId());
        if (!showtimeIds.isEmpty()){
            // there is loggers commands inside this function, no needs duplicates
            this.bookingService.deleteAllBookingsByShowtimeIds(showtimeIds);
            this.showtimeService.deleteAllByMovieId(movieToDelete.getId());
        }
        else{
            logger.info("Info: there is not showtime with movieId {}, ", showtimeIds);
        }

        this.movieService.deleteMovieByTitle(movieTitle);
        return true;
    }
}
