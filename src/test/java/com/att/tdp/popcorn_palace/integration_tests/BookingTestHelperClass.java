package com.att.tdp.popcorn_palace.integration_tests;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;

/*
Had to create new class for as booking test beacuse my original Booking class
    holds JsonIgnore commands to handle template of responses
    but along the test i have to use booking also for requests so thats my solution
 */
@Getter
@Setter
@Entity
public class BookingTestHelperClass {
    Integer seatNumber;
    String userId;
    Long showtimeId;

    @Id
    @JsonIgnore
    private Long id;

}
