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

        if (movie.getGenre() != null) {
            updatedMovie.get().setGenre(movie.getGenre());
        }

        if (movie.getDuration() != null) {
            updatedMovie.get().setDuration(movie.getDuration());
        }

        if (movie.getRating() != null) {
            updatedMovie.get().setRating(movie.getRating());
        }

        if (movie.getReleaseYear() != null) {
            updatedMovie.get().setReleaseYear(movie.getReleaseYear());
        }


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
}
