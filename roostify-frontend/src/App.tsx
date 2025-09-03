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
    const [loading, setLoading] = useState<boolean>(false);
    const [error, setError] = useState<string | null>(null);

    const fetchSchedule = async () => {
        setLoading(true);
        setError(null);

        try {
            const res = await fetch(`http://localhost:8080/schedules/${year}/${week}`);
            if (!res.ok) {
                throw new Error(`Failed to fetch schedule: ${res.status}`);
            }

            const data: Record<string, any[]> = await res.json();

            // Prepare empty week structure
            const transformed: Record<Day, Shift[]> = {
                mon: [],
                tue: [],
                wed: [],
                thu: [],
                fri: [],
                sat: [],
                sun: [],
            };

            const dayMap: Day[] = ["sun", "mon", "tue", "wed", "thu", "fri", "sat"];

            // Map date -> day of week
            Object.keys(data).forEach((date) => {
                const dayIndex = new Date(date).getDay(); // 0=Sun ... 6=Sat
                const dayName = dayMap[dayIndex];

                transformed[dayName].push(
                    ...data[date].map((shift) => ({
                        startTime: shift.startTime.slice(0, 5), // "09:30"
                        endTime: shift.endTime.slice(0, 5),
                        employeeId: shift.employeeId,
                    }))
                );
            });

            console.log("Transformed Schedule:", transformed);

            setScheduleData(transformed);
        } catch (err: any) {
            setError(err.message || "Something went wrong");
            setScheduleData(null);
        } finally {
            setLoading(false);
        }

        /*
            // Mock data for testing without backend
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
        */
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
                    disabled={loading}
                    className="mt-6 px-4 py-2 bg-blue-500 text-white rounded hover:bg-blue-600 transition disabled:opacity-50"
                >
                    {loading ? "Loading..." : "Load Schedule"}
                </button>
            </div>

            {error && <p className="text-red-500">{error}</p>}

            {scheduleData ? (
                <WeeklySchedule schedule={scheduleData} />
            ) : (
                !loading && !error && (
                    <p className="text-gray-500">Select year and week, then click Load Schedule.</p>
                )
            )}
        </div>
    );
}

export default App;
