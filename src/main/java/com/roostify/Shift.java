package com.roostify;

import java.time.LocalTime;

public class Shift {
    String day;
    int year;
    int week;
    LocalTime startTime;
    LocalTime endTime;

    Long employeeId;

    public Shift(int year, int week, String day){
        // TODO: generate shift id
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

    public Long getEmployeeId() {
        if (this.employeeId == null) {return 0L;}

        return employeeId;
    }
}
