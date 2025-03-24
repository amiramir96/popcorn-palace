package com.att.tdp.popcorn_palace.repositories;

import com.att.tdp.popcorn_palace.entities.Showtime;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.Optional;
import java.util.List;

/*
framework repository of JPA
helpful to communicate via to the dbs
this repository would manage the calls for the Showtime table
 */

public interface ShowtimeRepository extends JpaRepository<Showtime, Long>{
    Optional<Showtime> findByTheater(String title);
    void deleteAllByMovieId(Long movieId);

    @Query("SELECT s.id FROM Showtime s WHERE s.movieId = :movieId")
    List<Long> findIdsByMovieId(@Param("movieId") Long movieId);

    @Query("""
            SELECT s FROM Showtime s WHERE s.theater = :theater
                AND s.startTime < :endTime AND s.endTime > :startTime
            """)
    List<Showtime> findConflictingBetweenShowtimes(@Param("theater") String theater,
                                            @Param("startTime") Instant startTime, @Param("endTime") Instant endTime);


    @Query("""
            SELECT s FROM Showtime s WHERE s.theater = :theater
                AND s.startTime < :endTime AND s.endTime > :startTime
                            AND s.id <> :currentShowtimeId
            """)
    List<Showtime> findConflictingBetweenShowtimesWithoutCurrent(@Param("theater") String theater,
                                                   @Param("startTime") Instant startTime, @Param("endTime") Instant endTime,
                                                                 @Param("currentShowtimeId") Long id);

}

