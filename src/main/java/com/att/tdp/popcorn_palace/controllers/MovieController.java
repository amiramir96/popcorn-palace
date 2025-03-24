package com.att.tdp.popcorn_palace.controllers;

import com.att.tdp.popcorn_palace.entities.Movie;
import com.att.tdp.popcorn_palace.services.DeleteManagerService;
import com.att.tdp.popcorn_palace.services.MovieService;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/*
This class responsible to managing the requests and responses for the Movie entities of the Movies table.
holds the MovieService and DeleteManageService.
No complex logic is presented in this class.
This class is transfers the requests to service function calls. And the output of the services to responses msgs.
 */

@RestController
@RequestMapping("/movies")
public class MovieController {

    private final MovieService movieService;
    private final DeleteManagerService deleteManagerService;

    public MovieController(MovieService movieService, DeleteManagerService deleteManagerService) {
        this.movieService = movieService;
        this.deleteManagerService = deleteManagerService;
    }

    @GetMapping("/all")
    public ResponseEntity<List<Movie>> getAllMovies() {
        List<Movie> movies = this.movieService.getAllMovies();
        return ResponseEntity.status(HttpStatus.OK).body(movies);
    }

    @Transactional
    @PostMapping
    public ResponseEntity<?> addMovie(@Valid @RequestBody Movie movie) {
        Movie addedMovie = this.movieService.addMovie(movie);

        if (addedMovie == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Movie: " + movie.getTitle() + " already exists");
        }

        return ResponseEntity.status(HttpStatus.OK).body(addedMovie);
    }

    @Transactional
    @PostMapping("/update/{movieTitle}")
    public ResponseEntity<?> updateMovieByTitle(@PathVariable String movieTitle, @Valid @RequestBody Movie movie) {
        Movie updatedMovie = this.movieService.updateMovieByTitle(movieTitle, movie);

        if (updatedMovie == null){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Movie not found, Title: " + movieTitle + " don't exist");
        }

        return ResponseEntity.ok().build();
    }

    @Transactional
    @DeleteMapping("/{movieTitle}")
    public ResponseEntity<?> deleteMovieByTitle(@PathVariable String movieTitle) {
        boolean deleteSucceed = this.deleteManagerService.deleteMovieByTitle(movieTitle);

        if (deleteSucceed){
            return ResponseEntity.status(HttpStatus.OK).build();
        }

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Movie " + movieTitle + " not found");
    }
}
