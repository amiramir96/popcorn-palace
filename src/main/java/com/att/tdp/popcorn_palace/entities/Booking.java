package com.att.tdp.popcorn_palace.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

//{ "showtimeId": 1, "seatNumber": 15 , userId:"84438967-f68f-4fa0-b620-0f08217e76af"}
//{ "bookingId":"d1a6423b-4469-4b00-8c5f-e3cfc42eacae" }
@Setter
@Getter
@Entity
public class Booking {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID bookingId;

    @NotNull
    @Min(0)
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private Integer seatNumber;

    @NotBlank
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String userId;

    @NotNull
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private Long showtimeId;

    public Booking() {}

    public Booking(Integer seatNumber, String userId, Long showtimeId) {
        this.seatNumber = seatNumber;
        this.userId = userId;
        this.showtimeId = showtimeId;
    }

}
