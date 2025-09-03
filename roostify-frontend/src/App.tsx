import React, { useState } from "react";

import './App.css';
import WeeklySchedule from "./WeeklySchedule";

type Day = "mon" | "tue" | "wed" | "thu" | "fri" | "sat" | "sun";

interface Shift {
    startTime: string;
    endTime: string;
    employeeId: number;
}

// Returns the current week number
function getCurrentWeekNumber(): number {
    let currentDate = new Date();
    const dayNum = currentDate.getUTCDay() || 7;
    currentDate.setUTCDate(currentDate.getUTCDate() + 4 - dayNum);
    const yearStart = new Date(Date.UTC(currentDate.getUTCFullYear(), 0, 1));
    return Math.ceil((((currentDate.getTime() - yearStart.getTime()) / 86400000) + 1) / 7);
}

function App() {
    const [year, setYear] = useState<number>(new Date().getFullYear());
    const [week, setWeek] = useState<number>(getCurrentWeekNumber());
    const [scheduleData, setScheduleData] = useState<Record<Day, Shift[]> | null>(null);

    const fetchSchedule = () => {
        // In a real app, use API call with year and week
        console.log(`Fetching schedule for Year: ${year}, Week: ${week}`);

        const schedule: Record<Day, Shift[]> = {
            mon: [{ startTime: "09:30", endTime: "17:00", employeeId: 1 }],
            tue: [{ startTime: "08:00", endTime: "14:00", employeeId: 2 }],
            wed: [],
            thu: [
                { startTime: "10:00", endTime: "18:00", employeeId: 6 },
                { startTime: "12:00", endTime: "20:00", employeeId: 7 },
            ],
            fri: [],
            sat: [],
            sun: [],
        };

        setScheduleData(schedule);
    };

    // compute the last 5 years for the dropdown menu
    const currentYear = new Date().getFullYear();
    const years = Array.from({ length: 6 }, (_, i) => currentYear - i);

    return (
        <div className="p-6 space-y-4">
            <div className="flex space-x-4 items-center">
                <div>
                    <label className="block text-sm font-medium text-gray-700">Year</label>
                    <select
                        value={year}
                        onChange={(e) => setYear(Number(e.target.value))}
                        className="mt-1 p-2 border rounded w-32"
                    >
                        {years.map((y) => (
                            <option key={y} value={y}>
                                {y}
                            </option>
                        ))}
                    </select>
                </div>

                <div>
                    <label className="block text-sm font-medium text-gray-700">Week</label>
                    <input
                        type="number"
                        min={1}
                        max={53}
                        value={week}
                        onChange={(e) => setWeek(Number(e.target.value))}
                        className="mt-1 p-2 border rounded w-20"
                    />
                </div>

                <button
                    onClick={fetchSchedule}
                    className="px-4 py-2 bg-blue-500 text-white rounded hover:bg-blue-600 transition"
                >
                    Load Schedule
                </button>
            </div>


            {scheduleData ? (
                <WeeklySchedule schedule={scheduleData} />
            ) : (
                <p className="text-gray-500">Select year and week, then click Load Schedule.</p>
            )}
        </div>
    );
}

export default App;
