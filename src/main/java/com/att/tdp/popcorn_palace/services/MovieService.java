package com.att.tdp.popcorn_palace.services;

import com.att.tdp.popcorn_palace.entities.Movie;
import com.att.tdp.popcorn_palace.repositories.MovieRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

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

    public Movie updateMovieByTitle(String title, Movie movie) {
        Optional<Movie> updatedMovie = this.movieRepository.findByTitle(title);

        if (updatedMovie.isEmpty()) {
            logger.warn("movie with title {} not found, failed to update non-existing movie", title);
            return null;
        }

        // set null fields that required for updating data INTO movie
        prepareToUpdateMovieData(updatedMovie.get(), movie);

        try{
            Movie resultMovie = this.movieRepository.save(updatedMovie.get());
            logger.info("Info: movie {} updated successfully", resultMovie.getTitle());
            return resultMovie;
        }catch (Exception e){
            logger.error("Error: failed to update movie {}. Error: {}", title, e.getMessage(), e);
            return null;
        }
    }

    public boolean deleteMovieByTitle(String title) {
        Optional<Movie> movieToDelete = this.movieRepository.findByTitle(title);
        if (movieToDelete.isEmpty()) {
            logger.warn("movie with title {} not found, failed to delete non-existing movie", title);
            return false;
        }

        // if we here -> movie exists and found -> delete
        try{
            this.movieRepository.delete(movieToDelete.get());
            logger.info("Info: movie {} deleted successfully", title);
            return true;
        } catch (Exception e){
            logger.error("Error: failed to delete movie {}. Error: {}", title, e.getMessage(), e);
            return false;
        }
    }

    public Movie findMovieByMovieId(Long movieId) {
        logger.info("Info: searching for movie with id {}", movieId);
        Optional<Movie> movie = this.movieRepository.findById(movieId);
        return movie.orElse(null);
    }

    public Movie findMovieByTitle(String title) {
        logger.info("Info: searching for movie with title {}", title);
        Optional<Movie> movie = this.movieRepository.findByTitle(title);
        return movie.orElse(null);
    }

    private void prepareToUpdateMovieData(Movie upadatedMovie, Movie newMovie) {
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
