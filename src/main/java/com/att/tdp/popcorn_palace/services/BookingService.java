package com.att.tdp.popcorn_palace.services;

import com.att.tdp.popcorn_palace.entities.Booking;
import com.att.tdp.popcorn_palace.entities.Showtime;
import com.att.tdp.popcorn_palace.repositories.BookingRepository;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Service
public class BookingService {

    private final ShowtimeService showtimeService;
    private final BookingRepository bookingRepository;

    public BookingService(ShowtimeService showtimeService, BookingRepository bookingRepository) {
        this.showtimeService = showtimeService;
        this.bookingRepository = bookingRepository;
    }

    public Booking addBooking(Booking newBooking){
        Optional<Booking> ticketTaken = this.bookingRepository.findByShowtimeIdAndSeatNumber(newBooking.getShowtimeId(), newBooking.getSeatNumber());
        Showtime showtime = this.showtimeService.getShowtimeById(newBooking.getShowtimeId());

        // ticket is taken         OR showtime not exists OR   showtime already started
        if(ticketTaken.isPresent() || showtime == null || Instant.now().isAfter(showtime.getStartTime())){
            return null;
        }

        return this.bookingRepository.save(newBooking);
    }

    public boolean deleteAllBookingsByShowtimeId(Long showtimeId) {
        try{
            this.bookingRepository.deleteAllByShowtimeId(showtimeId);
        } catch (Exception e){
            return false;
        }
        return true;
    }

    public boolean deleteAllBookingsByShowtimeIds(List<Long> showtimeIds){
        try {
            this.bookingRepository.deleteByShowtimeIdIn(showtimeIds);
        } catch (Exception e) {
            return false;
        }
        return true;
    }
}
