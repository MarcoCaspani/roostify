package com.roostify;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@CrossOrigin(origins = "http://localhost:3000", allowedHeaders = "*", methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.DELETE})
public class ScheduleController {

    private final SchedulesRepository repository;

    public ScheduleController(SchedulesRepository repository) {
        this.repository = repository;
    }

    @GetMapping("/schedules/{year}/{week}")
    public Map<String, List<Shift>> getSchedule(
            @PathVariable int year,
            @PathVariable int week) {
        // For now, return a static schedule or fetch from repository

        return repository.getSchedule(year, week);
    }

    @DeleteMapping("/schedules/{year}/{week}")
    public void deleteSchedule(
            @PathVariable int year,
            @PathVariable int week) {
        this.repository.deleteSchedule(year, week);
    }

    @PostMapping("/schedules/{year}/{week}")
    public ResponseEntity<Object> createSchedule(
            @PathVariable int year,
            @PathVariable int week) {
        this.repository.createSchedule(year, week);

        // TODO: check if successful and respond with 201 Created if successful
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}
