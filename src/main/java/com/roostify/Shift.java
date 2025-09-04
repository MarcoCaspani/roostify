package com.roostify;

import java.time.LocalTime;
import java.util.UUID;

public class Shift {
    String shiftId;
    String day;
    int year;
    int week;
    LocalTime startTime;
    LocalTime endTime;

    Long employeeId;

    public Shift(int year, int week, String day){
        // TODO: generate shift id
        this.shiftId = UUID.randomUUID().toString();
        this.year = year;
        this.week = week;
        this.day = day;
    }

    public String getDay() {
        return day;
    }

    public LocalTime getStartTime() {
        return startTime;
    }

    public LocalTime getEndTime() {
        return endTime;
    }

    public String getShiftId() {
        return this.shiftId;
    }

    public Long getEmployeeId() {
        if (this.employeeId == null) {return 0L;}

        return employeeId;
    }
}
