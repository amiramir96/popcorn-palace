package com.att.tdp.popcorn_palace.services;

import com.att.tdp.popcorn_palace.entities.Movie;
import com.att.tdp.popcorn_palace.entities.Showtime;
import com.att.tdp.popcorn_palace.repositories.ShowtimeRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ShowtimeService {

    private final ShowtimeRepository showtimeRepository;
    private final MovieService movieService;

    public ShowtimeService(ShowtimeRepository showtimeRepository, MovieService movieService) {
        this.showtimeRepository = showtimeRepository;
        this.movieService = movieService;
    }

    public Showtime getShowtimeById(Long id){
        Optional<Showtime> showtime = this.showtimeRepository.findById(id);

        return showtime.orElse(null);

    }

    public Showtime addShowtime(Showtime newShowtime){
        Movie movieOfShowtime = this.movieService.findMovieByMovieId(newShowtime.getMovieId());
        List<Showtime> showtimeConflicts = this.showtimeRepository.findConflictingBetweenShowtimes(newShowtime.getTheater(),
                newShowtime.getStartTime(), newShowtime.getEndTime());

        // there is conflicts with older showtime OR there is no movie that the current showtime point to
        if (!showtimeConflicts.isEmpty() || movieOfShowtime == null) {
            return null;
        }

        return this.showtimeRepository.save(newShowtime);
    }

    public Showtime updateShowtimeById(Long showtimeId, Showtime showtime){

        // check existence in db
        Optional<Showtime> updatedShowtime = this.showtimeRepository.findById(showtimeId);
        if (updatedShowtime.isEmpty()){
            return null;
        }

        // set null fields that required for finding conflicts or updating data INTO showtime
        prepareToUpdateShowtimeData(updatedShowtime.get(), showtime);

        // search for conflicts
        List<Showtime> showtimeConflicts = this.showtimeRepository.findConflictingBetweenShowtimesWithoutCurrent
                (showtime.getTheater(), showtime.getStartTime(), showtime.getEndTime(), showtimeId);

        if (!showtimeConflicts.isEmpty()) {
            return null;
        }

        return this.showtimeRepository.save(showtime);
    }

    public boolean deleteShowtimeById(Long showtimeId){
        Optional<Showtime> showtime = this.showtimeRepository.findById(showtimeId);

        if (showtime.isEmpty()){
            return false;
        }

        this.showtimeRepository.delete(showtime.get());
        return true;
    }

    public boolean deleteAllByMovieId(Long movieId){
        List<Long> showtime = this.showtimeRepository.findIdsByMovieId(movieId);

        if (showtime.isEmpty()){
            return false;
        }

        this.showtimeRepository.deleteAllByMovieId(movieId);
        return true;
    }

    public List<Long> findShowtimeIdsByMovieId(Long movieId){
        return this.showtimeRepository.findIdsByMovieId(movieId);
    }

    private void prepareToUpdateShowtimeData(Showtime fromShowtime, Showtime toShowtime){
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
