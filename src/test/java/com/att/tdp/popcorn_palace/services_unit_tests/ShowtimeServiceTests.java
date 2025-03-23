package com.att.tdp.popcorn_palace.services_unit_tests;

import com.att.tdp.popcorn_palace.entities.Movie;
import com.att.tdp.popcorn_palace.entities.Showtime;
import com.att.tdp.popcorn_palace.repositories.ShowtimeRepository;
import com.att.tdp.popcorn_palace.services.MovieService;
import com.att.tdp.popcorn_palace.services.ShowtimeService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ShowtimeServiceTests {

    @Mock
    private ShowtimeRepository showtimeRepository;
    @Mock
    private MovieService movieService;

    @InjectMocks
    private ShowtimeService showtimeService;

    private Showtime validShowtime;

    @BeforeEach
    void setUp() {
        validShowtime = new Showtime();
        validShowtime.setId(1L);
        validShowtime.setTheater("Random theater");
        validShowtime.setMovieId(1L);
        validShowtime.setPrice(20.2);
        validShowtime.setStartTime(Instant.now().plusSeconds(3600)); // שעה קדימה
        validShowtime.setEndTime(Instant.now().plusSeconds(7200));
    }

    @Test
    void addShowtime_ShouldSucceed_Valid() {
        when(showtimeRepository.findConflictingBetweenShowtimes(validShowtime.getTheater(), validShowtime.getStartTime(),
                validShowtime.getEndTime())).thenReturn(new ArrayList<>());
        when(showtimeRepository.save(validShowtime)).thenReturn(validShowtime);
        when(movieService.findMovieByMovieId(validShowtime.getMovieId())).thenReturn(new Movie());

        Showtime addedShowtime = showtimeService.addShowtime(validShowtime);

        assertNotNull(addedShowtime);
        assertEquals(addedShowtime.getMovieId(), validShowtime.getMovieId());
    }

    @Test
    void addShowtime_ShouldFail_NewShowtimeHaveConflicts() {
        ArrayList<Showtime> dummyList = new ArrayList<>();
        dummyList.add(new Showtime());
        when(showtimeRepository.findConflictingBetweenShowtimes(validShowtime.getTheater(), validShowtime.getStartTime(),
                validShowtime.getEndTime())).thenReturn(dummyList);

        Showtime addedShowtime = showtimeService.addShowtime(validShowtime);

        assertNull(addedShowtime);
    }

    @Test
    void getShowtimeById_ShouldSucceed() {
        when(showtimeRepository.findById(validShowtime.getId()))
                .thenReturn(Optional.ofNullable(validShowtime));

        Showtime showtime = showtimeService.getShowtimeById(validShowtime.getId());

        assertNotNull(showtime);
        assertEquals(showtime.getId(), validShowtime.getId());
    }

    @Test
    void getShowtimeById_ShouldFail_NotExistsInDb() {
        when(showtimeRepository.findById(validShowtime.getId()))
                .thenReturn(Optional.ofNullable(null));

        Showtime showtime = showtimeService.getShowtimeById(validShowtime.getId());

        assertNull(showtime);
    }

    @Test
    void findShowtimeIdsByMovieId_ShouldSucceed() {
        ArrayList<Long> dummyList = new ArrayList<>();
        dummyList.add(1L);
        when(showtimeRepository.findIdsByMovieId(validShowtime.getMovieId()))
                .thenReturn(dummyList);

        List<Long> showtimeIds = showtimeService.findShowtimeIdsByMovieId(validShowtime.getMovieId());

        assertNotNull(showtimeIds);
        assertEquals(showtimeIds.size(), dummyList.size());
        assertEquals(showtimeIds.get(0), dummyList.get(0));
    }

    @Test
    void updateShowtimeById_shouldSucceed_Valid() {
        when(showtimeRepository.findById(1L)).thenReturn(Optional.of(validShowtime));
        when(showtimeRepository.findConflictingBetweenShowtimesWithoutCurrent(validShowtime.getTheater(),
                validShowtime.getStartTime(), validShowtime.getEndTime(), 1L)).thenReturn(new ArrayList<>());

        // https://stackoverflow.com/questions/2684630/making-a-mocked-method-return-an-argument-that-was-passed-to-it
        //      seriously that great
        when(showtimeRepository.save(any(Showtime.class))).thenAnswer(instance -> instance.getArgument(0));

        Showtime updatedShowtime = showtimeService.updateShowtimeById(1L, validShowtime);

        assertNotNull(updatedShowtime);
        assertEquals(validShowtime.getMovieId(), updatedShowtime.getMovieId());
    }

    @Test
    void updateShowtimeById_shouldFail_ShowtimeNotFound() {
        when(showtimeRepository.findById(1L)).thenReturn(Optional.empty());

        Showtime updatedShowtime = showtimeService.updateShowtimeById(1L, validShowtime);

        assertNull(updatedShowtime);
    }

    @Test
    void updateShowtimeById_shouldFail_ConflictsExist() {
        ArrayList<Showtime> dummyList = new ArrayList<>();
        dummyList.add(new Showtime());
        when(showtimeRepository.findById(1L)).thenReturn(Optional.of(validShowtime));
        when(showtimeRepository.findConflictingBetweenShowtimesWithoutCurrent(validShowtime.getTheater(),
                validShowtime.getStartTime(), validShowtime.getEndTime(), validShowtime.getId())).thenReturn(dummyList);

        Showtime updatedShowtime = showtimeService.updateShowtimeById(1L, validShowtime);

        assertNull(updatedShowtime);
    }

    @Test
    void deleteShowtimeById_ShouldSucceed_ShowtimeExists() {
        when(showtimeRepository.findById(1L)).thenReturn(Optional.of(validShowtime));

        boolean deleteSucceed = showtimeService.deleteShowtimeById(1L);

        assertTrue(deleteSucceed);
    }

    @Test
    void deleteShowtimeById_ShouldFail_ShowtimeMissing() {
        when(showtimeRepository.findById(1L)).thenReturn(Optional.empty());

        boolean deleteSucceed = showtimeService.deleteShowtimeById(1L);

        assertFalse(deleteSucceed);
    }

    @Test
    void deleteAllByMovieId_ShouldSucceed() {
        ArrayList<Long> dummyList = new ArrayList<>();
        dummyList.add(1L);
        when(showtimeRepository.findIdsByMovieId(1L)).thenReturn(dummyList);

        boolean deleteSucceed = showtimeService.deleteAllByMovieId(1L);

        assertTrue(deleteSucceed);
    }

    @Test
    void deleteAllByMovieId_ShouldFail_RepositoryNotFindingMatchingShowtimesToDelete() {
        when(showtimeRepository.findIdsByMovieId(1L)).thenReturn(new ArrayList<>());

        boolean deleteSucceed = showtimeService.deleteAllByMovieId(1L);

        assertFalse(deleteSucceed);
    }

}
