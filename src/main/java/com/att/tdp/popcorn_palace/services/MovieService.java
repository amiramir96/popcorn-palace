package com.att.tdp.popcorn_palace.services;

import com.att.tdp.popcorn_palace.entities.Movie;
import com.att.tdp.popcorn_palace.repositories.MovieRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class MovieService {

    private final MovieRepository movieRepository;

    public MovieService(MovieRepository movieRepository) {
        this.movieRepository = movieRepository;
    }

    public List<Movie> getAllMovies() {
        return this.movieRepository.findAll();
    }

    public Movie addMovie(Movie newMovie) {
        if (this.movieRepository.findByTitle(newMovie.getTitle()).isPresent()) {
            return null;
        }
        return this.movieRepository.save(newMovie);
    }

    public Movie updateMovieByTitle(String title, Movie movie) {
        Optional<Movie> updatedMovie = this.movieRepository.findByTitle(title);

        if (updatedMovie.isEmpty()) {
            return null;
        }

        // set null fields that required for updating data INTO movie
        prepareToUpdateMovieData(updatedMovie.get(), movie);

        return this.movieRepository.save(updatedMovie.get());
    }

    public boolean deleteMovieByTitle(String title) {
        Optional<Movie> movie = this.movieRepository.findByTitle(title);
        if (movie.isEmpty()) {
            return false;
        }
        // if we here -> movie exists and found
        this.movieRepository.delete(movie.get());
        return true;
    }

    public Movie findMovieByMovieId(Long movieId) {
        Optional<Movie> movie = this.movieRepository.findById(movieId);
        return movie.orElse(null);
    }

    public Movie findMovieByTitle(String title) {
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
