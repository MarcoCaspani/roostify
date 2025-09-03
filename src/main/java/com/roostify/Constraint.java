package com.roostify;

public class Constraint {
    private final int employeeId;
    private final String day;
    private final ConstraintType constraintType;

    public Constraint(int employeeId, String day, ConstraintType constraintType) {
        this.employeeId = employeeId;

        // TODO: check the day string is formatted in either "MON" or "2025-09-27"
        this.day = day;
        this.constraintType = constraintType;
    }

    public int getEmployeeId() {
        return employeeId;
    }

    public String getDay() {
        return day;
    }
    public ConstraintType getPreference() {
        return constraintType;
    }
}
