// TODO: create a method to retrieve a schedule for a particular week

package com.roostify;

import org.springframework.stereotype.Repository;

import java.sql.*;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class SchedulesRepository {

    private static final String DB_URL = "jdbc:postgresql://ep-noisy-haze-a2jsd1gs-pooler.eu-central-1.aws.neon.tech:5432/neondb?sslmode=require&channel_binding=require";
    private static final String DB_USER = "neondb_owner";
    private static final String DB_PASSWORD = System.getenv("DB_PASSWORD");

    // TODO: check that the schedule does not yet exist in the db, if it does delete the old one
    public void saveSchedule(Schedule schedule) {
        String sql = "INSERT INTO \"shifts\" (year, week, day, start_time, end_time, employee_id) VALUES (?, ?, ?, ?, ?, ?)";
        //TODO: check sql string is okay before executing
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
            PreparedStatement ps = conn.prepareStatement(sql)) {

            for (Shift shift : schedule.getShifts()) {
                ps.setInt(1, shift.year);
                ps.setInt(2, shift.week);
                ps.setDate(3, Date.valueOf(shift.getDay()));  // Assuming shift.getDay() returns yyyy-MM-dd
                ps.setTime(4, Time.valueOf(shift.getStartTime()));
                ps.setTime(5, Time.valueOf(shift.getEndTime()));
                ps.setLong(6, shift.getEmployeeId());
                ps.addBatch();
            }

            ps.executeBatch();
            System.out.println("✅ Rooster saved successfully to Neon DB.");

        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to save schedule to Neon DB.", e);
        }
    }

    /*
    Queries the database for the schedule for a particular year and week.
     */
    public Map<String, List<Shift>> getSchedule(int year, int week) {
        String sql = "SELECT day, start_time, end_time, employee_id FROM shifts WHERE year = ? AND week = ?";
        Map<String, List<Shift>> schedule = new HashMap<>();

        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, year);
            ps.setInt(2, week);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    String day = rs.getString("day").toLowerCase();
                    LocalTime startTime = rs.getTime("start_time").toLocalTime();
                    LocalTime endTime = rs.getTime("end_time").toLocalTime();
                    Long employeeId = rs.getLong("employee_id");

                    Shift shift = new Shift(year, week, day);
                    shift.startTime = startTime;
                    shift.endTime = endTime;
                    shift.employeeId = employeeId;

                    // Initialize list for day if it doesn’t exist, and add shift to the list
                    schedule.computeIfAbsent(day, k -> new ArrayList<>()).add(shift);
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to fetch schedule from DB", e);
        }

        return schedule;
    }


}
