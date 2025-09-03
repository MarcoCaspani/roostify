import React from "react";

type Day = "mon" | "tue" | "wed" | "thu" | "fri" | "sat" | "sun";
interface Shift {
    startTime: string;
    endTime: string;
    employeeId: number;
}
const days: Day[] = ["mon", "tue", "wed", "thu", "fri", "sat", "sun"];

const schedule: Record<Day, Shift[]> = {
    mon: [
        { startTime: "09:30", endTime: "17:00", employeeId: 1 },
        { startTime: "09:30", endTime: "17:00", employeeId: 2 },
    ],
    tue: [
        { startTime: "08:00", endTime: "14:00", employeeId: 3 },
        { startTime: "14:00", endTime: "20:00", employeeId: 4 },
    ],
    wed: [
        { startTime: "09:00", endTime: "15:00", employeeId: 5 },
    ],
    thu: [
        { startTime: "10:00", endTime: "18:00", employeeId: 6 },
        { startTime: "12:00", endTime: "20:00", employeeId: 7 },
    ],
    fri: [
        { startTime: "08:00", endTime: "16:00", employeeId: 8 },
    ],
    sat: [
        { startTime: "09:00", endTime: "13:00", employeeId: 9 },
    ],
    sun: [
        { startTime: "10:00", endTime: "14:00", employeeId: 10 },
    ],
};

const WeeklySchedule: React.FC = () => {
    // @ts-ignore
    return (
        <div className="w-full p-6">
            <h1 className="text-2xl font-bold mb-4 text-gray-800">Weekly Schedule</h1>
            <div className="grid grid-cols-7 gap-4">
                {days.map((day) => (
                    <div
                        key={day}
                        className="bg-white rounded-2xl shadow-md p-3 flex flex-col items-center border border-gray-200 hover:shadow-lg transition"
                    >
                        <h2 className="text-lg font-semibold text-gray-700 mb-2 capitalize">{day}</h2>
                        {schedule[day]?.map((shift: Shift, index: number) => (
                            <div
                                key={index}
                                className="w-full bg-blue-100 text-blue-800 rounded-xl p-2 mb-2 text-center cursor-pointer hover:bg-blue-200 transition"
                            >
                                <span className="block font-medium">Employee {shift.employeeId}</span>
                                <span className="text-xs text-gray-600">{shift.startTime} - {shift.endTime}</span>
                            </div>
                        ))}
                        {!schedule[day]?.length && (
                            <span className="text-gray-400 text-sm">No shifts</span>
                        )}
                    </div>
                ))}
            </div>
        </div>
    );
};


export default WeeklySchedule;
