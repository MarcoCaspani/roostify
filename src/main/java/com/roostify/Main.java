package com.roostify;


import java.util.List;
import java.util.Map;

public class Main {
    public static void main(String[] args) {
        // simulate creation of roosters

        int year = 2025;
        int week = 36;

        // create employees
        Employee employee1 = new Employee(1, "employee1", 32);
        employee1.setMaxDays(5);
        employee1.setNoNightAndMorningShift(true);
        Employee employee2 = new Employee(2, "employee2", 18);
        employee2.setMaxDays(3);
        Employee employee3 = new Employee(3, "employee3", 24);
        employee3.setMaxDays(4);
        employee3.setNoNightAndMorningShift(true);
        Employee[] employees = new  Employee[]{ employee1, employee2, employee3 };

        // generate list of constraints
        Constraint c1 = new Constraint(employee1.getId(), "MON", ConstraintType.EARLYMANDATORY);
        Constraint c2 = new Constraint(employee3.getId(), "WED", ConstraintType.NO);
        Constraint[] constraints = new Constraint[]{c1, c2};

        // Create a schedule
        ScheduleCreator scheduleCreator = new ScheduleCreator();
        Schedule schedule = scheduleCreator.createSchedule(year, week, employees, constraints);

        System.out.println("Schedule created");

        // Save schedule on Neon database
        SchedulesRepository  schedulesRepository = new SchedulesRepository();
        schedulesRepository.saveSchedule(schedule);

        Map<String, List<Shift>> downloadedSchedule = null;
        downloadedSchedule = schedulesRepository.getSchedule(year, week);
        for (String day : downloadedSchedule.keySet()) {
            System.out.println(day + ":");
            for (Shift shift : downloadedSchedule.get(day)) {
                System.out.println("  Shift from " + shift.getStartTime() + " to " + shift.getEndTime() + " assigned to employee ID " + shift.getEmployeeId());
            }
        }
    }
}