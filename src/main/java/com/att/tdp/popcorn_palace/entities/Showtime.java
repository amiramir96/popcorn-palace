package com.att.tdp.popcorn_palace.entities;

import com.fasterxml.jackson.annotation.*;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

//{ "movieId": 1, "price":20.2, "theater": "Sample Theater", "startTime": "2025-02-14T11:47:46.125405Z",
//      "endTime": "2025-02-14T14:47:46.125405Z" }

@Setter
@Getter
@Entity
public class Showtime {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    private Long movieId;
    @NotNull
    @Min(0)
    private Double price;
    @NotBlank
    private String theater;

    @NotNull
    private Instant startTime;
    @NotNull
    private Instant endTime;

    public Showtime(Double price, Long id, Long movieId, String theater, Instant startTime, Instant endTime) {
        this.price = price;
        this.id = id;
        this.movieId = movieId;
        this.theater = theater;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public Showtime() {

    }
}
