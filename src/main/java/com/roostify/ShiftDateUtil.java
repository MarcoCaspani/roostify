package com.roostify;

import java.time.*;
import java.time.temporal.*;

public class ShiftDateUtil {

    // from e.g. 2025, 36, "mon" return "2025-09-01"
    public static LocalDate getDateFromWeek(int year, int week, String day) {
        DayOfWeek dayOfWeek = switch (day.toLowerCase()) {
            case "mon" -> DayOfWeek.MONDAY;
            case "tue" -> DayOfWeek.TUESDAY;
            case "wed" -> DayOfWeek.WEDNESDAY;
            case "thu" -> DayOfWeek.THURSDAY;
            case "fri" -> DayOfWeek.FRIDAY;
            case "sat" -> DayOfWeek.SATURDAY;
            case "sun" -> DayOfWeek.SUNDAY;
            default -> throw new IllegalArgumentException("Invalid day: " + day);
        };

        // Start from the first day of the year
        LocalDate firstDayOfYear = LocalDate.of(year, 1, 1);

        // Get the date of the Monday of the given week
        LocalDate date = firstDayOfYear
                .with(IsoFields.WEEK_OF_WEEK_BASED_YEAR, week)
                .with(TemporalAdjusters.nextOrSame(dayOfWeek));

        return date;
    }
}