package com.att.tdp.popcorn_palace.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

/*
This class represent Movie entity for the API
fields defined to be non-null.

entity request example POST
{ "movieId": 1, "price":20.2, "theater": "Sample Theater", "startTime": "2025-02-14T11:47:46.125405Z",
      "endTime": "2025-02-14T14:47:46.125405Z" }
response:
{ "id": 1, "price":50.2,"movieId": 1, "theater": "Sample Theater", "startTime": "2025-02-14T11:47:46.125405Z", "endTime": "2025-02-14T14:47:46.125405Z" }
*/

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
