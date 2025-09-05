package com.roostify;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class ExcelGenerator {
    /**
     * Generates an XLSX file in the classic Dutch rooster format.
     */
    public static void generateScheduleExcel(Schedule schedule, Map<Integer, Employee> employees, OutputStream out, int year, int week) throws IOException {
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Rooster");

        // Define columns with space columns between days
        String[] days = {"MON", "TUE", "WED", "THU", "FRI", "SAT", "SUN"};
        String[] columns = {"NAME", "HOURS"};
        List<String> headers = new ArrayList<>();
        headers.addAll(Arrays.asList(columns));
        for (String day : days) {
            headers.add(day);
            headers.add(""); // Add space column after each day
        }
        headers.add("TOTAL");

        // Top row: Week and dates (no merged regions)
        Row weekRow = sheet.createRow(0);
        weekRow.createCell(0).setCellValue("Week " + week);
        weekRow.createCell(1).setCellValue("");
        int colIdx = 2;
        for (int i = 0; i < days.length; i++) {
            String dateStr = getDateStr(year, week, i); // e.g., "15-Sep"
            weekRow.createCell(colIdx).setCellValue(dateStr);
            colIdx++;
            weekRow.createCell(colIdx).setCellValue(""); // space column
            colIdx++;
        }
        weekRow.createCell(headers.size() - 1).setCellValue("");

        // Header row
        Row headerRow = sheet.createRow(1);
        for (int i = 0; i < headers.size(); i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers.get(i));
            CellStyle style = workbook.createCellStyle();
            Font font = workbook.createFont();
            font.setBold(true);
            style.setFont(font);
            style.setBorderBottom(BorderStyle.THIN);
            style.setBorderTop(BorderStyle.THIN);
            style.setBorderLeft(BorderStyle.THIN);
            style.setBorderRight(BorderStyle.THIN);
            cell.setCellStyle(style);
        }

        // Group shifts by employee and day
        Map<Integer, Map<String, Shift>> empDayShift = new LinkedHashMap<>();
        for (Shift shift : schedule.getShifts()) {
            empDayShift.computeIfAbsent(shift.employeeId != null ? shift.employeeId.intValue() : 0, k -> new HashMap<>()).put(shift.day, shift);
        }

        // For each employee, create two rows: first/last name, hours, times, totals
        int rowNum = 2;
        for (Employee emp : employees.values()) {
            // First row: first name, total hours, start times, total
            Row row1 = sheet.createRow(rowNum);
            row1.createCell(0).setCellValue(getFirstName(emp.getEmployeeName()));
            row1.createCell(1).setCellValue(emp.getMinHours());
            double totalHours = 0.0;
            int totalDays = 0;
            colIdx = 2;
            for (int i = 0; i < days.length; i++) {
                String day = getDateStr(year, week, i, true); // yyyy-MM-dd
                Shift shift = empDayShift.getOrDefault(emp.getId(), Collections.emptyMap()).get(day);
                if (shift != null && shift.startTime != null) {
                    row1.createCell(colIdx).setCellValue(formatTime(shift.startTime));
                } else {
                    row1.createCell(colIdx).setCellValue("");
                }
                colIdx++;
                // space column after start time (leave empty)
                row1.createCell(colIdx).setCellValue("");
                colIdx++;
            }

            // Second row: last name, (no max days), end times, hours, total days
            Row row2 = sheet.createRow(rowNum + 1);
            row2.createCell(0).setCellValue(getLastName(emp.getEmployeeName()));
            row2.createCell(1).setCellValue(""); // Do not print max days
            colIdx = 2;
            for (int i = 0; i < days.length; i++) {
                String day = getDateStr(year, week, i, true);
                Shift shift = empDayShift.getOrDefault(emp.getId(), Collections.emptyMap()).get(day);
                if (shift != null && shift.endTime != null) {
                    row2.createCell(colIdx).setCellValue(formatTime(shift.endTime));
                    double hours = getPreciseHours(shift);
                    row2.createCell(colIdx + 1).setCellValue(hours > 0 ? String.format("%.2f", hours) : "");
                    totalHours += hours;
                    totalDays++;
                } else {
                    row2.createCell(colIdx).setCellValue("");
                    row2.createCell(colIdx + 1).setCellValue("");
                }
                colIdx += 2;
            }
            row2.createCell(headers.size() - 1).setCellValue(String.format("%.2f", totalHours));
            rowNum += 2;
        }

        // Bottom summary rows (placeholders + sum of worked hours per day)
        Row totalRow = sheet.createRow(rowNum);
        totalRow.createCell(0).setCellValue("TOTAL");
        totalRow.createCell(1).setCellValue("totaal");
        colIdx = 2;
        for (int i = 0; i < days.length; i++) {
            // Sum worked hours for this day
            double dayTotalHours = 0.0;
            String dayIso = getDateStr(year, week, i, true);
            for (Employee emp : employees.values()) {
                Shift shift = empDayShift.getOrDefault(emp.getId(), Collections.emptyMap()).get(dayIso);
                if (shift != null && shift.startTime != null && shift.endTime != null) {
                    dayTotalHours += getPreciseHours(shift);
                }
            }
            totalRow.createCell(colIdx).setCellValue("totaal");
            colIdx++;
            totalRow.createCell(colIdx).setCellValue(String.format("%.2f", dayTotalHours)); // space column: sum of worked hours
            colIdx++;
        }
        totalRow.createCell(headers.size() - 1).setCellValue("80");

        // Autosize columns
        for (int i = 0; i < headers.size(); i++) {
            sheet.autoSizeColumn(i);
        }

        // Write to file
        workbook.write(out);
    }

    // Helper: get first name
    private static String getFirstName(String name) {
        String[] parts = name.split(" ");
        return parts.length > 0 ? parts[0] : name;
    }
    // Helper: get last name
    private static String getLastName(String name) {
        String[] parts = name.split(" ");
        return parts.length > 1 ? parts[1] : "";
    }
    // Helper: format time
    private static String formatTime(java.time.LocalTime time) {
        return time != null ? time.format(DateTimeFormatter.ofPattern("H.mm")) : "";
    }
    // Helper: get hours
    private static int getHours(Shift shift) {
        if (shift.startTime != null && shift.endTime != null) {
            return (int) java.time.Duration.between(shift.startTime, shift.endTime).toHours();
        }
        return 0;
    }
    // Helper: get precise hours (e.g. 9:30-17:00 = 7.50)
    private static double getPreciseHours(Shift shift) {
        if (shift.startTime != null && shift.endTime != null) {
            double minutes = java.time.Duration.between(shift.startTime, shift.endTime).toMinutes();
            return minutes / 60.0;
        }
        return 0.0;
    }
    // Helper: get date string for week/day
    private static String getDateStr(int year, int week, int dayIndex) {
        java.time.LocalDate date = java.time.LocalDate.ofYearDay(year, 1)
                .with(java.time.temporal.IsoFields.WEEK_OF_WEEK_BASED_YEAR, week)
                .with(java.time.temporal.WeekFields.ISO.dayOfWeek(), dayIndex + 1);
        return date.format(DateTimeFormatter.ofPattern("d-MMM"));
    }
    // Helper: get date string for week/day in yyyy-MM-dd
    private static String getDateStr(int year, int week, int dayIndex, boolean iso) {
        java.time.LocalDate date = java.time.LocalDate.ofYearDay(year, 1)
                .with(java.time.temporal.IsoFields.WEEK_OF_WEEK_BASED_YEAR, week)
                .with(java.time.temporal.WeekFields.ISO.dayOfWeek(), dayIndex + 1);
        return iso ? date.toString() : date.format(DateTimeFormatter.ofPattern("d-MMM"));
    }
}
