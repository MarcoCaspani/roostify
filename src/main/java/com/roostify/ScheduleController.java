package com.roostify;

import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.List;
import java.util.Map;

/**
    This controller handles all endpoints related to schedules, shifts, and employees.
    It interacts with the SchedulesRepository to perform CRUD operations and generate schedules.
*/
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

    @GetMapping("/schedules/{year}/{week}/download")
    public ResponseEntity<InputStreamResource> downloadScheduleExcel(@PathVariable int year, @PathVariable int week) {
        try {
            // Retrieve employees and schedule
            Map<Integer, Employee> employees = repository.getEmployees();
            Map<String, List<Shift>> scheduleMap = repository.getSchedule(year, week);
            Schedule schedule = new Schedule();
            for (List<Shift> shifts : scheduleMap.values()) {
                for (Shift shift : shifts) {
                    schedule.addShift(shift);
                }
            }
            // Generate Excel file in memory
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            ExcelGenerator.generateScheduleExcel(schedule, employees, out, year, week);
            ByteArrayInputStream in = new ByteArrayInputStream(out.toByteArray());
            String fileName = String.format("rooster year%d week%d.xlsx", year, week);
            HttpHeaders headers = new HttpHeaders();
            headers.add("Content-Disposition", "attachment; filename=" + fileName);
            return ResponseEntity.ok()
                    .headers(headers)
                    .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                    .body(new InputStreamResource(in));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

}
