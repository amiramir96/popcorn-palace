package com.att.tdp.popcorn_palace.services_unit_tests;

import com.att.tdp.popcorn_palace.entities.Movie;
import com.att.tdp.popcorn_palace.repositories.MovieRepository;
import com.att.tdp.popcorn_palace.services.MovieService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class MovieServiceTests {

    @Mock
    private MovieRepository movieRepository;

    @InjectMocks
    private MovieService movieService;

    private Movie validMovie1;
    private Movie validMovie2;

    @BeforeEach
    void setUp() {
        validMovie1 = new Movie();
        validMovie1.setId(12345L);
        validMovie1.setTitle("Sample Movie Test1");
        validMovie1.setGenre("Action");
        validMovie1.setDuration(120);
        validMovie1.setRating(8.7F);
        validMovie1.setReleaseYear(2025);

        validMovie2 = new Movie();
        validMovie2.setId(67890L);
        validMovie2.setTitle("Sample Movie Test2");
        validMovie2.setGenre("Drama");
        validMovie2.setDuration(120);
        validMovie2.setRating(9F);
        validMovie2.setReleaseYear(2025);
    }

    /*
     * Tests for addMovie function
     */

    @Test
    void addMovie_ShouldSucceed() {
        when(movieRepository.findByTitle(validMovie1.getTitle())).thenReturn(Optional.empty());
        when(movieRepository.save(validMovie1)).thenReturn(validMovie1);

        Movie addedMovie = movieService.addMovie(validMovie1);
        assertNotNull(addedMovie);
        assertEquals(validMovie1.getTitle(), addedMovie.getTitle());
    }

    @Test
    void addMovie_ShouldFail_MovieAlreadyExists() {
        when(movieRepository.findByTitle(validMovie1.getTitle())).thenReturn(Optional.of(validMovie1));

        Movie addedMovie = movieService.addMovie(validMovie1);
        assertNull(addedMovie);
    }

    @Test
    void addMovie_ShouldFail_SaveOfRepositoryReturnsNull() {
        when(movieRepository.findByTitle(validMovie1.getTitle())).thenReturn(Optional.empty());
        when(movieRepository.save(validMovie1)).thenReturn(null);

        Movie addedMovie = movieService.addMovie(validMovie1);
        assertNull(addedMovie);
    }

    /*
     * Tests for updateMovieByTitle function
     */

    @Test
    void updateMovie_ShouldSucceed_MovieExists() {
        when(movieRepository.findByTitle(validMovie1.getTitle())).thenReturn(Optional.of(validMovie1));
        when(movieRepository.save(validMovie1)).thenReturn(validMovie1);

        Movie update = new Movie();
        update.setGenre("Comedy");

        Movie updatedMovie = movieService.updateMovieByTitle(validMovie1.getTitle(), update);

        assertNotNull(updatedMovie);
        assertEquals("Comedy", updatedMovie.getGenre());
    }

    @Test
    void updateMovie_ShouldFail_MovieDoesNotExist() {
        when(movieRepository.findByTitle("Fake Movie")).thenReturn(Optional.empty());

        Movie updatedMovie = movieService.updateMovieByTitle("Fake Movie", validMovie1);
        assertNull(updatedMovie);
    }

    @Test
    void updateMovie_ShouldFail_SaveReturnsNull() {
        when(movieRepository.findByTitle(validMovie1.getTitle())).thenReturn(Optional.of(validMovie1));
        when(movieRepository.save(any())).thenReturn(null);

        Movie updatedMovie = movieService.updateMovieByTitle(validMovie1.getTitle(), validMovie2);
        assertNull(updatedMovie);
    }

    /*
     * Tests for deleteMovieByTitle function
     */

    @Test
    void deleteMovie_ShouldSucceed_WhenMovieExists() {
        when(movieRepository.findByTitle(validMovie1.getTitle())).thenReturn(Optional.of(validMovie1));

        boolean deletedMovie = movieService.deleteMovieByTitle(validMovie1.getTitle());

        assertTrue(deletedMovie);
    }

    @Test
    void deleteMovie_ShouldFail_WhenMovieDoesNotExist() {
        when(movieRepository.findByTitle("Missing")).thenReturn(Optional.empty());

        boolean deletedMovie = movieService.deleteMovieByTitle("Missing");

        assertFalse(deletedMovie);
    }

    /*
     * Tests for findMovieByTitle function
     */

    @Test
    void findMovieByTitle_ShouldReturnMovie_WhenExists() {
        when(movieRepository.findByTitle(validMovie1.getTitle())).thenReturn(Optional.of(validMovie1));

        Movie movie = movieService.findMovieByTitle(validMovie1.getTitle());

        assertNotNull(movie);
        assertEquals(validMovie1.getTitle(), movie.getTitle());
    }

    @Test
    void findMovieByTitle_ShouldReturnNull_WhenNotExists() {
        when(movieRepository.findByTitle("nothing")).thenReturn(Optional.empty());

        Movie movie = movieService.findMovieByTitle("nothing");

        assertNull(movie);
    }

    /*
     * Tests for getAllMovies function
     */

    @Test
    void getAllMovies_ShouldReturnListOfMovies() {
        List<Movie> movies = List.of(validMovie1, validMovie2);
        when(movieRepository.findAll()).thenReturn(movies);

        List<Movie> result = movieService.getAllMovies();

        assertEquals(2, result.size());
        assertTrue(result.contains(validMovie1));
        assertTrue(result.contains(validMovie2));
    }
}
