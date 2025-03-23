package com.att.tdp.popcorn_palace.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;


// { "title": "Sample Movie Title", "genre": "Action", "duration": 120, "rating": 8.7, "releaseYear": 2025 }
@Setter
@Getter
@Entity
public class Movie {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "title cannot be blank") // java side
    @Column(unique = true, nullable = false) // db side
    private String title;

    private String genre;

    @Min(value = 1, message = "duration must be at least 1 minute")
    private Integer duration;

    @Min(value = 0, message = "rating must be positive number")
    private Float rating;

    @Min(value = 1826, message = "release year can't be before 1826") // first camera invented in 1826 :-P
    @Max(value = 2025, message = "release year of ongoing movie can't be in the future") // greater than present
    private Integer releaseYear;

    public Movie(String title, String genre, Integer duration, Integer releaseYear, Float rating) {
        this.title = title;
        this.genre = genre;
        this.duration = duration;
        this.releaseYear = releaseYear;
        this.rating = rating;
    }

    public Movie() {
    }

}
