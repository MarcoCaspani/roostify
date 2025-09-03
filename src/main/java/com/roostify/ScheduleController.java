package com.roostify;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@CrossOrigin(origins = "http://localhost:3000")
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
}
