package com.roostify;


public class Employee {
    int id;
    String name;

    int minHours; // how many hours per week the employee needs to work at least.
    int maxDays; // how many days the employee can work during one week
    boolean noNightAndMorningShift; // true: employee cant work in the morning if they worked the previous night (age)

    public Employee(int id, String name, int minHours) {
        this.id = id;
        this.name = name;
        this.minHours = minHours;
        this.maxDays = 7;
        this.noNightAndMorningShift = false;
    }

    public int getId() {
        return id;
    }

    public boolean isNoNightAndMorningShift() { return noNightAndMorningShift; }

    public void setMaxDays(int maxDays) {
        this.maxDays = maxDays;
    }
    public void setNoNightAndMorningShift(boolean noNightAndMorningShift) {
        this.noNightAndMorningShift = noNightAndMorningShift;
    }

    public int getMaxDays() {
        return maxDays;
    }
}
