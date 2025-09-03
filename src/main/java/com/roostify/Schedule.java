package com.roostify;

import java.util.ArrayList;
import java.util.List;

public class Schedule {
    List<Shift> shifts =  new ArrayList<>();

    public void addShift(Shift shift) {
        shifts.add(shift);
    }

    public List<Shift> getShiftsByDay(String day) {
        List<Shift> shifts = new ArrayList<>();

        for  (Shift shift : this.shifts) {
            if (shift.day.equals(day)) {
                shifts.add(shift);
            }
        }
        return shifts;
    }

    public List<Shift> getShifts() {
        return shifts;
    }
}
