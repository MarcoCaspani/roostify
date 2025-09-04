/*
@returns number - Week number (1-53)
*/
export const getCurrentWeekNumber = (): number => {
    let currentDate = new Date();
    const dayNum = currentDate.getUTCDay() || 7;
    currentDate.setUTCDate(currentDate.getUTCDate() + 4 - dayNum);
    const yearStart = new Date(Date.UTC(currentDate.getUTCFullYear(), 0, 1));
    return Math.ceil((((currentDate.getTime() - yearStart.getTime()) / 86400000) + 1) / 7);
};

// TODO: extract this on a different file
interface Shift {
    startTime: string;
    endTime: string;
    employeeId: number;
}
// TODO: extract this on a different file
type Day = "mon" | "tue" | "wed" | "thu" | "fri" | "sat" | "sun";

// A function to check if the schedule contains no shifts
export const isScheduleEmpty = (schedule: Record<Day, Shift[]> | null): boolean => {
    if (schedule == null) {
        return true;
    }
    return Object.values(schedule).every(shifts => shifts.length === 0);
}