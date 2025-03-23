package com.att.tdp.popcorn_palace.controllers;

import com.att.tdp.popcorn_palace.entities.Booking;
import com.att.tdp.popcorn_palace.services.BookingService;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/bookings")
public class BookingController {

    private final BookingService bookingService;

    public BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    @Transactional
    @PostMapping
    public ResponseEntity<?> addBooking(@Valid @RequestBody Booking booking){
        System.out.println(">>> Booking = " + booking);
        Booking addedBooking = this.bookingService.addBooking(booking);

        if (addedBooking == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Booking not added, Seat: " + booking.getSeatNumber() + " is taken for showtime: " + booking.getShowtimeId() + " or the showtime is not available anymore");
        }

        return ResponseEntity.status(HttpStatus.OK).body(addedBooking);
    }

}
