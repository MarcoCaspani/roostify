import java.time.LocalDate;
import java.time.LocalTime;
import java.time.temporal.WeekFields;
import java.util.*;

public class ScheduleCreator {

    public ScheduleCreator() {
    }

    public Schedule createSchedule(int year, int week, Employee[] employees, Constraint[] constraints) {
        Schedule schedule = new Schedule();
        String[] weekDays = getWeekDates(year, week); // 7 days ISO yyyy-MM-dd
        Map<Integer, Integer> employeeWorkDays = new HashMap<>();

        int employeeIndex = 0; // round-robin fallback

        for (int i = 0; i < weekDays.length; i++) {
            String day = weekDays[i];

            // Create two shifts per day
            Shift earlyShift = new Shift(day);
            earlyShift.startTime = LocalTime.of(9, 30);
            earlyShift.endTime = LocalTime.of(17, 0);

            Shift lateShift = new Shift(day);
            lateShift.startTime = LocalTime.of(14, 0);
            lateShift.endTime = LocalTime.of(20, 0);

            Set<Integer> assignedEmployees = new HashSet<>();

            // 1) Handle EARLYMANDATORY & LATEMANDATORY first
            for (Constraint c : constraints) {
                if (c.getDay().equals(day)) {
                    switch (c.getPreference()) {
                        case EARLYMANDATORY:
                            if (canWorkMoreDays(c.getEmployeeId(), employeeWorkDays, employees)) {
                                earlyShift.employeeId = (long) c.getEmployeeId();
                                assignedEmployees.add(c.getEmployeeId());
                                incrementWorkDays(c.getEmployeeId(), employeeWorkDays);
                            }
                            break;

                        case LATEMANDATORY:
                            if (canWorkMoreDays(c.getEmployeeId(), employeeWorkDays, employees)) {
                                lateShift.employeeId = (long) c.getEmployeeId();
                                assignedEmployees.add(c.getEmployeeId());
                                incrementWorkDays(c.getEmployeeId(), employeeWorkDays);
                            }
                            break;
                    }
                }
            }

            // 2) Handle EARLY / LATE preferences if shift is still unassigned
            for (Constraint c : constraints) {
                if (c.getDay().equals(day)) {
                    if (c.getPreference() == ConstraintType.EARLY
                            && earlyShift.employeeId == null
                            && !assignedEmployees.contains(c.getEmployeeId())
                            && canWorkMoreDays(c.getEmployeeId(), employeeWorkDays, employees)
                            && canWorkMorningAfterNight(c.getEmployeeId(), employees, schedule, weekDays, i)) {
                        earlyShift.employeeId = (long) c.getEmployeeId();
                        assignedEmployees.add(c.getEmployeeId());
                        incrementWorkDays(c.getEmployeeId(), employeeWorkDays);
                    }

                    if (c.getPreference() == ConstraintType.LATE
                            && lateShift.employeeId == null
                            && !assignedEmployees.contains(c.getEmployeeId())
                            && canWorkMoreDays(c.getEmployeeId(), employeeWorkDays, employees)) {
                        lateShift.employeeId = (long) c.getEmployeeId();
                        assignedEmployees.add(c.getEmployeeId());
                        incrementWorkDays(c.getEmployeeId(), employeeWorkDays);
                    }
                }
            }

            // 3) Assign remaining early shift (round-robin)
            int attempts = 0;
            while (earlyShift.employeeId == null && attempts < employees.length) {
                Employee candidate = employees[employeeIndex % employees.length];
                employeeIndex++;
                attempts++;

                if (isEmployeeAvailable(candidate.getId(), day, constraints, assignedEmployees)
                        && canWorkMoreDays(candidate.getId(), employeeWorkDays, employees)
                        && canWorkMorningAfterNight(candidate.getId(), employees, schedule, weekDays, i)) {
                    earlyShift.employeeId = (long) candidate.getId();
                    assignedEmployees.add(candidate.getId());
                    incrementWorkDays(candidate.getId(), employeeWorkDays);
                    break;
                }
            }

            // 4) Assign remaining late shift (round-robin)
            attempts = 0;
            employeeIndex = 0;
            while (lateShift.employeeId == null && attempts < employees.length) {
                Employee candidate = employees[employeeIndex % employees.length];
                employeeIndex++;
                attempts++;

                if (isEmployeeAvailable(candidate.getId(), day, constraints, assignedEmployees)
                        && canWorkMoreDays(candidate.getId(), employeeWorkDays, employees)) {
                    lateShift.employeeId = (long) candidate.getId();
                    assignedEmployees.add(candidate.getId());
                    incrementWorkDays(candidate.getId(), employeeWorkDays);
                    break;
                }
            }

            if (earlyShift.employeeId == null) {
                System.out.println("⚠ No available employee for early shift on " + day);
            }
            if (lateShift.employeeId == null) {
                System.out.println("⚠ No available employee for late shift on " + day);
            }

            // 5) Save shifts into rooster
            schedule.addShift(earlyShift);
            schedule.addShift(lateShift);
        }

        return schedule;
    }

    /**
     * Helper to check if an employee can work on a given day.
     */
    private boolean isEmployeeAvailable(int employeeId, String day, Constraint[] constraints, Set<Integer> assigned) {
        if (assigned.contains(employeeId)) return false;

        for (Constraint c : constraints) {
            if (c.getEmployeeId() == employeeId && c.getDay().equals(day) && c.getPreference() == ConstraintType.NO) {
                return false; // Employee unavailable
            }
        }
        return true;
    }

    /**
     * Helper to check if an employee can work more days considering they have already been partially scheduled.
     */
    private boolean canWorkMoreDays(int employeeId, Map<Integer, Integer> workDays, Employee[] employees) {
        int maxDays = Arrays.stream(employees)
                .filter(e -> e.getId() == employeeId)
                .findFirst()
                .map(Employee::getMaxDays)
                .orElse(7);
        return workDays.getOrDefault(employeeId, 0) < maxDays;
    }

    /** Increment worked days counter */
    private void incrementWorkDays(int employeeId, Map<Integer, Integer> workDays) {
        workDays.put(employeeId, workDays.getOrDefault(employeeId, 0) + 1);
    }

    /**
      This methods returns true if the employeeId has not worked the previous day the late shift
     */
    private boolean canWorkMorningAfterNight(
            int employeeId,
            Employee[] employees,
            Schedule schedule,
            String[] weekDays,
            int currentIndex
    ) {
        // If it's the first day of the week → no previous day → always OK
        if (currentIndex == 0) return true;

        Employee employee = Arrays.stream(employees)
                .filter(e -> e.getId() == employeeId)
                .findFirst()
                .orElse(null);

        if (employee == null || !employee.isNoNightAndMorningShift()) {
            return true; // Rule doesn't apply to this employee
        }

        String previousDay = weekDays[currentIndex - 1];
        List<Shift> previousDayShifts = schedule.getShiftsByDay(previousDay);

        if (previousDayShifts == null) return true;

        // If the employee worked the late shift yesterday, block them
        for (Shift shift : previousDayShifts) {
            if (shift.employeeId != null
                    && shift.employeeId == employeeId
                    && shift.startTime.isAfter(LocalTime.of(14, 0))) {
                return false;
            }
        }

        return true;
    }

    public String[] getWeekDates(int year, int week) {
        WeekFields weekFields = WeekFields.ISO; // ISO weeks start on Monday

        LocalDate startOfWeek = LocalDate
                .now()
                .withYear(year)
                .with(weekFields.weekOfYear(), week)
                .with(weekFields.dayOfWeek(), 1); // Monday

        String[] weekDays =  new String[7];
        for (int i = 0; i < 7; i++) {
            weekDays[i] = startOfWeek.plusDays(i).toString();
        }

        return weekDays;
    }

}
