package com.att.tdp.popcorn_palace.services;

import com.att.tdp.popcorn_palace.entities.Movie;
import com.att.tdp.popcorn_palace.entities.Showtime;
import com.att.tdp.popcorn_palace.repositories.ShowtimeRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ShowtimeService {

    private final ShowtimeRepository showtimeRepository;
    private final MovieService movieService;
    private final Logger logger = LoggerFactory.getLogger(ShowtimeService.class);

    public ShowtimeService(ShowtimeRepository showtimeRepository, MovieService movieService) {
        this.showtimeRepository = showtimeRepository;
        this.movieService = movieService;
    }

    public Showtime getShowtimeById(Long id){
        logger.info("Info: searching for showtime with id {}", id);
        Optional<Showtime> showtime = this.showtimeRepository.findById(id);
        return showtime.orElse(null);
    }

    public Showtime addShowtime(Showtime newShowtime){
        Movie movieOfShowtime = this.movieService.findMovieByMovieId(newShowtime.getMovieId());
        List<Showtime> showtimeConflicts = this.showtimeRepository.findConflictingBetweenShowtimes(newShowtime.getTheater(),
                newShowtime.getStartTime(), newShowtime.getEndTime());

        if (!showtimeConflicts.isEmpty() || movieOfShowtime == null) {
            // there is conflicts with older showtime OR there is no movie that the current showtime point to
            logger.warn("Attempt to add showtime {} in theater {} Failed. There is {} conflicts, and movieId is {}",
                    newShowtime.getId(), newShowtime.getTheater(), showtimeConflicts.size(), newShowtime.getMovieId());
            return null;
        }

        try{
            Showtime addedShowtime = this.showtimeRepository.save(newShowtime);
            logger.info("Info: showtime {} in theater {} added successfully", addedShowtime.getId(), addedShowtime.getTheater());
            return addedShowtime;
        } catch (Exception e){
            logger.error("Error: failed to add showtime theater {}, Error: {}", newShowtime.getTheater(), e.getMessage(), e);
            return null;
        }
    }

    public Showtime updateShowtimeById(Long showtimeId, Showtime showtime){
        Optional<Showtime> updatedShowtime = this.showtimeRepository.findById(showtimeId);
        if (updatedShowtime.isEmpty()){
            logger.warn("showtime with id {} not found", showtimeId);
            return null;
        }

        // set null fields that required for finding conflicts or updating data INTO showtime
        prepareToUpdateShowtimeData(updatedShowtime.get(), showtime);

        // search for conflicts
        List<Showtime> showtimeConflicts = this.showtimeRepository.findConflictingBetweenShowtimesWithoutCurrent
                (showtime.getTheater(), showtime.getStartTime(), showtime.getEndTime(), showtimeId);

        if (!showtimeConflicts.isEmpty()) {
            logger.warn("showtime {} conflicts with {} showtime", showtimeId, showtimeConflicts.size());
            return null;
        }

        try{
            Showtime resultShowtime = this.showtimeRepository.save(showtime);
            logger.info("Info: showtime {} updated successfully", resultShowtime.getId());
            return resultShowtime;
        } catch (Exception e){
            logger.error("Error: failed to update showtime {}, Error: {}", showtimeId, e.getMessage(), e);
            return null;
        }
    }

    public boolean deleteShowtimeById(Long showtimeId){
        Optional<Showtime> showtime = this.showtimeRepository.findById(showtimeId);

        if (showtime.isEmpty()){
            logger.warn("showtime with id {} not found, cannot delete non-existing showtime", showtimeId);
            return false;
        }

        try{
            this.showtimeRepository.delete(showtime.get());
            logger.info("Info: showtime {} deleted successfully", showtimeId);
            return true;
        } catch (Exception e){
            logger.error("Error: failed to delete showtime {}, Error {}",showtimeId, e.getMessage(), e);
            return false;
        }
    }

    public boolean deleteAllByMovieId(Long movieId){
        List<Long> showtime = this.showtimeRepository.findIdsByMovieId(movieId);

        if (showtime.isEmpty()){
            logger.warn("showtime with moviedId {} not found", movieId);
            return false;
        }

        try{
            this.showtimeRepository.deleteAllByMovieId(movieId);
            logger.info("Info: all showtime with movieId {} deleted successfully", movieId);
            return true;
        } catch (Exception e) {
            logger.error("Error: failed to delete showtime with movieId {}, Error {}",movieId, e.getMessage(), e);
            return false;
        }
    }

    public List<Long> findShowtimeIdsByMovieId(Long movieId){
        logger.info("Info: searching for showtime with movieId {}", movieId);
        return this.showtimeRepository.findIdsByMovieId(movieId);
    }

    private void prepareToUpdateShowtimeData(Showtime fromShowtime, Showtime toShowtime){
        /*
          inner function, helps to prepare toShowtime object before sending him to update in the db
         */

        if (toShowtime.getTheater() == null){
            toShowtime.setTheater(fromShowtime.getTheater());
        }

        if (toShowtime.getStartTime() == null){
            toShowtime.setStartTime(fromShowtime.getStartTime());
        }

        if (toShowtime.getEndTime() == null){
            toShowtime.setEndTime(fromShowtime.getEndTime());
        }

        if (toShowtime.getPrice() == null ){
            toShowtime.setPrice(fromShowtime.getPrice());
        }

        if (toShowtime.getMovieId() == null){
            toShowtime.setMovieId(fromShowtime.getMovieId());
        }

        toShowtime.setId(fromShowtime.getId());
    }

}
