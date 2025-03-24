package com.att.tdp.popcorn_palace.services;

import com.att.tdp.popcorn_palace.entities.Booking;
import com.att.tdp.popcorn_palace.entities.Showtime;
import com.att.tdp.popcorn_palace.repositories.BookingRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

/*
The logic class with functionality for the Showtime entities and Showtime table.
This class supports Add, Update, Get, Delete, actions for Showtime entities.
In addition, all functions got logs to monitor the functioning of the program.

This class holds pointer to the ShowtimeService of the program since every booking point to showtime by showtimeId field.
 */

@Service
public class BookingService {

    private final ShowtimeService showtimeService;
    private final BookingRepository bookingRepository;
    private final Logger logger = LoggerFactory.getLogger(BookingService.class);

    public BookingService(ShowtimeService showtimeService, BookingRepository bookingRepository) {
        this.showtimeService = showtimeService;
        this.bookingRepository = bookingRepository;
    }

    public Booking addBooking(Booking newBooking){
        Optional<Booking> ticketTaken = this.bookingRepository.findByShowtimeIdAndSeatNumber(newBooking.getShowtimeId(), newBooking.getSeatNumber());
        Showtime showtime = this.showtimeService.getShowtimeById(newBooking.getShowtimeId());

        // ticket is taken         OR showtime not exists OR   showtime already started
        if(ticketTaken.isPresent() || showtime == null || Instant.now().isAfter(showtime.getStartTime())){
            logger.warn("Can't book seat number {} of showtime {}.", newBooking.getSeatNumber(), newBooking.getShowtimeId());
            return null;
        }

        try{
            Booking addedBooking = this.bookingRepository.save(newBooking);
            logger.info("Booking {} added successfully.", addedBooking.getBookingId());
            return addedBooking;
        } catch (Exception e) {
            logger.error("Error: failed to book seat {} in showtime {}. Error: {}",
                    newBooking.getSeatNumber(), newBooking.getShowtimeId(), e.getMessage(), e);
            return null;

        }
    }

    public boolean deleteAllBookingsByShowtimeId(Long showtimeId) {
        try{
            this.bookingRepository.deleteAllByShowtimeId(showtimeId);
            logger.info("Info: all bookings of showtime {} deleted successfully", showtimeId);
            return true;
        } catch (Exception e){
            logger.error("Error: failed to delete bookings of showtime {}. Error: {}", showtimeId, e.getMessage(), e);
            return false;
        }
    }

    public boolean deleteAllBookingsByShowtimeIds(List<Long> showtimeIds){
        try {
            this.bookingRepository.deleteByShowtimeIdIn(showtimeIds);
            logger.info("Info: all bookings of showtime {} deleted successfully", showtimeIds);
            return true;
        } catch (Exception e) {
            logger.error("Error: failed to delete bookings of showtime {}. Error: {}", showtimeIds, e.getMessage(), e);
            return false;
        }
    }
}
