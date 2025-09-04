import React, { useState } from "react";

import './App.css';
import WeeklySchedule from "./WeeklySchedule";
import {getCurrentWeekNumber, isScheduleEmpty} from "./utils/utils";

// TODO: extract this on a different file
type Day = "mon" | "tue" | "wed" | "thu" | "fri" | "sat" | "sun";

// TODO: extract this on a different file
interface Shift {
    startTime: string;
    endTime: string;
    employeeId: number;
}

function App() {
    const [year, setYear] = useState<number>(new Date().getFullYear());
    const [week, setWeek] = useState<number>(getCurrentWeekNumber());
    const [scheduleData, setScheduleData] = useState<Record<Day, Shift[]> | null>(null);
    const [loading, setLoading] = useState<boolean>(false);
    const [error, setError] = useState<string | null>(null);
    const [scheduleEmpty, setScheduleEmpty] = useState<boolean>(false);

    const deleteSchedule = async () => {
        // delete the schedule for the selected year and week using a DELETE request
        setLoading(true);
        setError(null);
        try {
            const res = await fetch(`http://localhost:8080/schedules/${year}/${week}`, {
                method: 'DELETE',
            });
            if (!res.ok) {
                throw new Error(`Failed to delete schedule: ${res.status}`);
            }
            setScheduleData(null);
        } catch (err: any) {
            setError(err.message || "Something went wrong");
        } finally {
            setLoading(false);
        }
    }

    const createSchedule = async () => {
        // 1) request the api to create the schedule for the selected year and week using a POST request
        setLoading(true);
        setError(null);
        try {
            const res = await fetch(`http://localhost:8080/schedules/${year}/${week}`, {
                method: 'POST',
            });
            if (!res.ok) {
                throw new Error(`Failed to create schedule: ${res.status}`);
            }
            setScheduleData(null);
        } catch (err: any) {
            setError(err.message || "Something went wrong");
        } finally {
            setLoading(false);
        }

        // 2: fetch the schedule to update the UI
        fetchSchedule();
    }

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
            let transformed: Record<Day, Shift[]> = {
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

            if (isScheduleEmpty(transformed)) {
                setScheduleEmpty(true);
                setScheduleData(null);
            } else {
                setScheduleEmpty(false);
                setScheduleData(transformed);
            }
        } catch (err: any) {
            setError(err.message || "Something went wrong");
            setScheduleData(null);
        } finally {
            setLoading(false);
        }
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

                { scheduleData ? (
                    <button
                        onClick={deleteSchedule}
                        disabled={loading}
                        className="mt-6 px-4 py-2 bg-red-500 text-white rounded hover:bg-red-600 transition disabled:opacity-50"
                    >
                        {loading ? "Loading..." : "Delete Schedule"}
                    </button>
                ) : null }

                { scheduleEmpty ? (
                    <button
                        onClick={createSchedule}
                        disabled={loading}
                        className="mt-6 px-4 py-2 bg-orange-500 text-white rounded hover:bg-orange-600 transition disabled:opacity-50"
                    >
                        {loading ? "Creating schedule..." : "Create Schedule"}
                    </button>
                ) : null }
            </div>

            {error && <p className="text-red-500">{error}</p>}

            { scheduleData ? (
                <WeeklySchedule schedule={scheduleData} />
            ) : (
                !loading && !error && (
                    <p className="text-gray-500">Select year and week, then click Load Schedule.</p>
                )
            )}

            { scheduleEmpty && !loading && !error && (
                <p className="text-red-400">No schedule available for week {week}. Please generate a schedule.</p>
            ) }


        </div>
    );
}

export default App;
