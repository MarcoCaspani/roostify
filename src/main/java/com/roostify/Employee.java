package com.roostify;


/**
 * Represents an employee with attributes such as ID, name, minimum hours,
 * maximum days, and shift restrictions.
 */
public class Employee {
    int id;
    String employeeName;
    int minHours; // how many hours per week the employee needs to work at least.
    int maxDays; // how many days the employee can work during one week
    boolean noNightAndMorningShift; // true: employee cant work in the morning if they worked the previous night (age)

    public Employee(int id, String employeeName, int minHours) {
        this.id = id;
        this.employeeName = employeeName;
        this.minHours = minHours;
        this.maxDays = 7;
        this.noNightAndMorningShift = false;
    }

    // Getters and setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getEmployeeName() { return employeeName; }
    public void setEmployeeName(String employeeName) { this.employeeName = employeeName; }

    public int getMinHours() { return minHours; }
    public void setMinHours(int minHours) { this.minHours = minHours; }

    public int getMaxDays() { return maxDays; }
    public void setMaxDays(int maxDays) { this.maxDays = maxDays; }

    public boolean isNoNightAndMorningShift() { return noNightAndMorningShift; }
    public void setNoNightAndMorningShift(boolean noNightAndMorningShift) {
        this.noNightAndMorningShift = noNightAndMorningShift;
    }
}
