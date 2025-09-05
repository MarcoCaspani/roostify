package com.roostify;

import java.io.IOException;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.Map;

public class ExcelGeneratorTest {
    public static void main(String[] args) {
        // Create employees
        Employee emp1 = new Employee(1, "Alice", 32);
        emp1.setMaxDays(5);
        Employee emp2 = new Employee(2, "Bob", 24);
        emp2.setMaxDays(4);
        Employee emp3 = new Employee(3, "Charlie", 18);
        emp3.setMaxDays(3);

        Map<Integer, Employee> employees = new HashMap<>();
        employees.put(emp1.getId(), emp1);
        employees.put(emp2.getId(), emp2);
        employees.put(emp3.getId(), emp3);

        // Create schedule
        Schedule schedule = new Schedule();
        Shift shift1 = new Shift(2025, 36, "2025-09-01"); // Monday
        shift1.startTime = LocalTime.of(9, 30);
        shift1.endTime = LocalTime.of(17, 0);
        shift1.employeeId = 1L;
        schedule.addShift(shift1);

        Shift shift2 = new Shift(2025, 36, "2025-09-01"); // Monday
        shift2.startTime = LocalTime.of(14, 0);
        shift2.endTime = LocalTime.of(20, 0);
        shift2.employeeId = 2L;
        schedule.addShift(shift2);

        Shift shift3 = new Shift(2025, 36, "2025-09-02"); // Tuesday
        shift3.startTime = LocalTime.of(9, 30);
        shift3.endTime = LocalTime.of(17, 0);
        shift3.employeeId = 3L;
        schedule.addShift(shift3);

        Shift shift4 = new Shift(2025, 36, "2025-09-02"); // Tuesday
        shift4.startTime = LocalTime.of(14, 0);
        shift4.endTime = LocalTime.of(20, 0);
        shift4.employeeId = 1L;
        schedule.addShift(shift4);

        // Generate Excel file
//        try {
//            ExcelGenerator.generateScheduleExcel(schedule, employees, "test_schedule.xlsx", 2025, 36);
//            System.out.println("✅ Excel file generated: test_schedule.xlsx");
//        } catch (IOException e) {
//            System.err.println("❌ Failed to generate Excel file: " + e.getMessage());
//        }
    }
}

