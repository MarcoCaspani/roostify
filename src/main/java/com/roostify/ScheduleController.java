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

    //// This endpoint automatically creates a schedule for the given year and week
    @PostMapping("/schedules/{year}/{week}")
    public ResponseEntity<Object> createSchedule(
            @PathVariable int year,
            @PathVariable int week) {
        this.repository.createSchedule(year, week);

        // TODO: check if successful and respond with 201 Created if successful
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    //// This endpoint deletes a specific shift given the shift ID
    @DeleteMapping("/shifts/{shiftId}")
    public void deleteShift(
            @PathVariable String shiftId) {
        this.repository.deleteShift(shiftId);
    }

    //// This endpoint returns the full list of employees
    @GetMapping("/employees")
    public Map<Integer, Employee> getEmployees() {
        return this.repository.getEmployees();
    }

    //// This endpoint adds a new shift given the shift details in the request body
    /// body: JSON.stringify({
    ///                     year,
    ///                     week,
    ///                     day: addShiftDayModal, //the selected day of the week
    ///                     employeeId,
    ///                     startTime,
    ///                     endTime,
    ///                 }),


    public static class ShiftRequest {
        private int year;
        private int week;
        private String day;
        private Long employeeId;
        private String startTime;
        private String endTime;

        public int getYear() { return year; }

        public int getWeek() { return week; }

        public String getDay() { return day; }

        public Long getEmployeeId() { return employeeId; }

        public String getStartTime() { return startTime; }

        public String getEndTime() { return endTime; }

    }

    @PostMapping("/shifts")
    public ResponseEntity<Object> addShift(
            @RequestBody ShiftRequest shift) {
        try {
            repository.addShift(
                shift.getYear(),
                shift.getWeek(),
                shift.getDay(),
                shift.getEmployeeId(),
                shift.getStartTime(),
                shift.getEndTime()
            );
            return ResponseEntity.status(HttpStatus.CREATED).build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid shift details");
        }
    }

}
