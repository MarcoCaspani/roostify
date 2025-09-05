import React, { useState } from "react";

import './App.css';
import WeeklySchedule from "./WeeklySchedule";
import {getCurrentWeekNumber, isScheduleEmpty} from "./utils/utils";
import {deleteShift} from "./services/api"
import AddShiftModal from "./AddShiftModal";

// TODO: extract this on a different file
type Day = "mon" | "tue" | "wed" | "thu" | "fri" | "sat" | "sun";

// TODO: extract this on a different file
interface Shift {
    shiftId: string;
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
    const [addShiftDayModal, setAddShiftDayModal] = useState<Day | null>(null);
    const [employees, setEmployees] = useState<Record<number, { id: number; name: string }>>({});

    const deleteSchedule = async () => {
        const confirmDelete = window.confirm("Are you sure you want to delete this schedule?");
        if (!confirmDelete) return;

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

    // Fetch employees once on component mount
    React.useEffect(() => {
        const fetchEmployees = async () => {
            try {
                const res = await fetch(`http://localhost:8080/employees`);
                if (!res.ok) {
                    throw new Error(`Failed to fetch employees: ${res.status}`);
                }

                const data = await res.json();

                // Transform into a dictionary with id as key
                const transformed: Record<number, { id: number; name: string }> = {};
                Object.values(data).forEach((item: any) => {
                    transformed[item.id] = { id: item.id, name: item.employeeName };
                });

                console.log("transformed", transformed);
                setEmployees(transformed);
            } catch (err) {
                console.error(err);
            }
        };

        fetchEmployees();
    }, []);

    // Remove shift handler
    const handleRemoveShift = async (shiftId: string) => {
        const confirmDelete = window.confirm("Are you sure you want to remove this shift?");
        if (!confirmDelete) return;

        try {
            // Call API to delete shift by ID
            console.log(`Deleting shift ${shiftId}`);
            await deleteShift(shiftId); // API call

            await fetchSchedule();      // Refresh schedule after deletion
        } catch (err) {
            console.error("Failed to delete shift:", err);
            alert("Something went wrong while deleting the shift!");
        }
    };

    const handleAddShift = (day: Day) => {
        setAddShiftDayModal(day);
    };

    const handleConfirmAddShift = async (employeeId: number, startTime: string, endTime: string) => {
        try {
            await fetch(`http://localhost:8080/shifts`, {
                method: "POST",
                headers: { "Content-Type": "application/json" },
                body: JSON.stringify({
                    year: Number(year),
                    week: Number(week),
                    day: addShiftDayModal, // e.g., "MONDAY"
                    employeeId: Number(employeeId),
                    startTime: startTime.includes(":") ? `${startTime}:00` : startTime, // ensure HH:mm:ss
                    endTime: endTime.includes(":") ? `${endTime}:00` : endTime,
                }),
            });
            fetchSchedule(); // refresh UI
        } catch (err) {
            console.error(err);
            alert("Failed to add shift");
        } finally {
            setAddShiftDayModal(null);
        }
    };

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
                        shiftId: shift.shiftId,
                        startTime: shift.startTime.slice(0, 5), // "09:30"
                        endTime: shift.endTime.slice(0, 5),
                        employeeId: shift.employeeId
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
                <WeeklySchedule
                    year = {year}
                    week = {week}
                    schedule={scheduleData}
                    employees={employees}
                    onRemoveShift={handleRemoveShift}
                    onAddShift={handleAddShift}
                />
            ) : (
                !loading && !error && (
                    <p className="text-gray-500">Select year and week, then click Load Schedule.</p>
                )
            )}

            { scheduleEmpty && !loading && !error && (
                <p className="text-red-400">No schedule available for week {week}. Please generate a schedule.</p>
            ) }

            {addShiftDayModal && (
                <AddShiftModal
                    day={addShiftDayModal}
                    employees={employees}
                    onClose={() => setAddShiftDayModal(null)}
                    onConfirm={handleConfirmAddShift}
                />
            )}
        </div>
    );
}

export default App;
