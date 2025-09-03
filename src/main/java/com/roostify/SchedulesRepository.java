// TODO: create a method to retrieve a schedule for a particular week

package com.roostify;

import java.sql.*;

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

}
