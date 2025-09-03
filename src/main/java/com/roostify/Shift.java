package com.roostify;

import java.time.LocalTime;

public class Shift {
    String day;
    String year; //TODO
    String week; //TODO
    LocalTime startTime;
    LocalTime endTime;

    Long employeeId;

    public Shift(String day){
        // TODO: generate shift id
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
