package com.att.tdp.popcorn_palace.services;

import com.att.tdp.popcorn_palace.entities.Movie;
import com.att.tdp.popcorn_palace.repositories.MovieRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/*
The logic class with functionality for the Movie objects and Movies table.
This class supports Add, Update, Get, GetAll, Delete, actions for Movie entities.
In addition, all functions got logs to monitor the functioning of the program.
 */

@Service
public class MovieService {

    private final MovieRepository movieRepository;
    private static final Logger logger = LoggerFactory.getLogger(MovieService.class);

    public MovieService(MovieRepository movieRepository) {
        this.movieRepository = movieRepository;
    }

    public List<Movie> getAllMovies() {
        logger.info("Info: getting all movies");
        return this.movieRepository.findAll();
    }

    public Movie addMovie(Movie newMovie) {
        if (this.movieRepository.findByTitle(newMovie.getTitle()).isPresent()) {
            logger.warn("Attempt to add existing instance Failed. movie {} already exists", newMovie.getTitle());
            return null;
        }

        try {
            Movie addedMovie = this.movieRepository.save(newMovie);
            logger.info("Info: movie {} added successfully", addedMovie.getTitle());
            return addedMovie;
        } catch (Exception e) {
            logger.error("Error: failed to add movie {}. Error: {}", newMovie.getTitle(), e.getMessage(), e);
            return null;
        }
    }

    public Movie updateMovieByTitle(String movieTitle, Movie movie) {
        Optional<Movie> updatedMovie = this.movieRepository.findByTitle(movieTitle);

        if (updatedMovie.isEmpty()) {
            logger.warn("movie with title {} not found, failed to update non-existing movie", movieTitle);
            return null;
        }

        // set null fields that required for updating data INTO movie
        prepareToUpdateMovieData(updatedMovie.get(), movie);

        try{
            Movie resultMovie = this.movieRepository.save(updatedMovie.get());
            logger.info("Info: movie {} updated successfully", resultMovie.getTitle());
            return resultMovie;
        }catch (Exception e){
            logger.error("Error: failed to update movie {}. Error: {}", movieTitle, e.getMessage(), e);
            return null;
        }
    }

    public boolean deleteMovieByTitle(String movieTitle) {
        Optional<Movie> movieToDelete = this.movieRepository.findByTitle(movieTitle);
        if (movieToDelete.isEmpty()) {
            logger.warn("movie with title {} not found, cannot delete non-existing movie", movieTitle);
            return false;
        }

        // if we here -> movie exists and found -> delete
        try{
            this.movieRepository.delete(movieToDelete.get());
            logger.info("Info: movie {} deleted successfully", movieTitle);
            return true;
        } catch (Exception e){
            logger.error("Error: failed to delete movie {}. Error: {}", movieTitle, e.getMessage(), e);
            return false;
        }
    }

    public Movie findMovieByMovieId(Long movieId) {
        logger.info("Info: searching for movie with id {}", movieId);
        Optional<Movie> movie = this.movieRepository.findById(movieId);
        return movie.orElse(null);
    }

    public Movie findMovieByTitle(String movieTitle) {
        logger.info("Info: searching for movie with title {}", movieTitle);
        Optional<Movie> movie = this.movieRepository.findByTitle(movieTitle);
        return movie.orElse(null);
    }

    private void prepareToUpdateMovieData(Movie upadatedMovie, Movie newMovie) {
        /*
          inner function, helps to prepare upadatedMovie object before sending him to update in the db
         */

        if (newMovie.getGenre() != null) {
            upadatedMovie.setGenre(newMovie.getGenre());
        }

        if (newMovie.getDuration() != null) {
            upadatedMovie.setDuration(newMovie.getDuration());
        }

        if (newMovie.getRating() != null) {
            upadatedMovie.setRating(newMovie.getRating());
        }

        if (newMovie.getReleaseYear() != null) {
            upadatedMovie.setReleaseYear(newMovie.getReleaseYear());
        }
    }
}
