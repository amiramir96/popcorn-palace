package com.att.tdp.popcorn_palace.controllers;


import com.att.tdp.popcorn_palace.entities.Showtime;
import com.att.tdp.popcorn_palace.services.DeleteManagerService;
import com.att.tdp.popcorn_palace.services.ShowtimeService;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/showtimes")
public class ShowtimeController {

    private final ShowtimeService showtimeService;
    private final DeleteManagerService deleteManagerService;

    public ShowtimeController(ShowtimeService showtimeService, DeleteManagerService deleteManagerService) {
        this.showtimeService = showtimeService;
        this.deleteManagerService = deleteManagerService;
    }

    @GetMapping("/{showtimeId}")
    public ResponseEntity<?> getShowtimeByShowtimeId(@PathVariable Long showtimeId){
        Showtime showtime = this.showtimeService.getShowtimeById(showtimeId);

        if (showtime == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Showtime: " + showtimeId + " not found");
        }

        return ResponseEntity.status(HttpStatus.OK).body(showtime);
    }

    @Transactional
    @PostMapping
    public ResponseEntity<?> addShowtime(@Valid @RequestBody Showtime showtime){
        Showtime addedShowtime = this.showtimeService.addShowtime(showtime);

        if (addedShowtime == null){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Showtime not added, already exists OR conflicts with another existing showtimes");
        }

        return ResponseEntity.status(HttpStatus.OK).body(addedShowtime);
    }

    @Transactional
    @PostMapping("/update/{showtimeId}")
    public ResponseEntity<?> updateShowtimeByShowtimeId(@PathVariable Long showtimeId, @Valid @RequestBody Showtime showtime){
        Showtime updatedShowtime = this.showtimeService.updateShowtimeById(showtimeId, showtime);

        if (updatedShowtime == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Showtime not updated, not exists OR conflicts with another existing showtimes");
        }
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @Transactional
    @DeleteMapping("/{showtimeId}")
    public ResponseEntity<?> deleteShowtimeByShowtimeId(@PathVariable Long showtimeId){
        boolean deleteSucceed = this.deleteManagerService.deleteShowtimeByShowtimeId(showtimeId);
        if (deleteSucceed){
            return ResponseEntity.status(HttpStatus.OK).build();
        }

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Showtime Id: " + showtimeId + " not found");
    }

}
