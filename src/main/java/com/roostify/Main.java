package com.roostify;


public class Main {
    public static void main(String[] args) {
        // simulate creation of roosters

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
        Schedule schedule = scheduleCreator.createSchedule(2025, 36, employees, constraints);

        System.out.println("Schedule created");

        // Save schedule on Neon database
        SchedulesRepository  schedulesRepository = new SchedulesRepository();
        schedulesRepository.saveSchedule(schedule);
    }
}