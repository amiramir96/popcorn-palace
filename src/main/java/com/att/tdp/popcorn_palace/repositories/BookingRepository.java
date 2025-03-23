package com.att.tdp.popcorn_palace.repositories;

import com.att.tdp.popcorn_palace.entities.Booking;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface BookingRepository extends JpaRepository<Booking, UUID> {

    Optional<Booking> findByShowtimeIdAndSeatNumber(Long showtimeId, Integer seatNumber);

    List<Booking> findByShowtimeId(Long showtimeId);

    void deleteAllByShowtimeId(Long showtimeId);

    void deleteByShowtimeIdIn(List<Long> showtimeIds);
}
