package com.roostify;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
public class ScheduleController {

    private final SchedulesRepository repository;

    public ScheduleController(SchedulesRepository repository) {
        this.repository = repository;
    }

    @GetMapping("/schedule/{year}/{week}")
    public Map<String, List<Shift>> getSchedule(
            @PathVariable int year,
            @PathVariable int week) {
        // For now, return a static schedule or fetch from repository

        return repository.getSchedule(year, week);
    }
}
