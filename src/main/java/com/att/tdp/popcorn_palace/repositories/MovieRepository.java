package com.att.tdp.popcorn_palace.repositories;

import com.att.tdp.popcorn_palace.entities.Movie;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/*
framework repository of JPA
helpful to communicate via to the dbs
this repository would manage the calls for the Movies table
 */

public interface MovieRepository extends JpaRepository<Movie, Long> {
    Optional<Movie> findByTitle(String title);
}
