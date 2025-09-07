package com.roostify;

/**
 * Represents a scheduling constraint for an employee on a specific day.
 * Each constraint includes the employee ID, the day (formatted as "MON" or "YYYY-MM-DD"),
 * and the type of constraint (e.g., mandatory shift, preferred shift, or no shift).
 */
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
