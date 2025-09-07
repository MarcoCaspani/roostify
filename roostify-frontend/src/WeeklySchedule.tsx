import React, {useState} from "react";
import {isScheduleEmpty} from "./utils/utils";

type Day = "mon" | "tue" | "wed" | "thu" | "fri" | "sat" | "sun";
interface Shift {
    shiftId: string;
    startTime: string;
    endTime: string;
    employeeId: number;
}
const days: Day[] = ["mon", "tue", "wed", "thu", "fri", "sat", "sun"];

interface WeeklyScheduleProps {
    year: number;
    week: number;
    schedule: Record<Day, Shift[]> | null;
    employees?: Record<number, { id: number; name: string; minHours: number }>;
    onAddShift?: (day: Day) => void; //TODO: day should have full day specification 2025-09-03 for instance
    onEditShift?: (shiftId: string) => void;
    onRemoveShift?: (shiftIt: string) => void;
}

const WeeklySchedule: React.FC<WeeklyScheduleProps> = ({
   year,
   week,
   schedule,
   employees,
   onAddShift,
   onEditShift,
   onRemoveShift
}) => {

    const [selectedDay, setSelectedDay] = useState<Day | null>(null);
    const [editMode, setEditMode] = useState<boolean>(false);
    const [loading, setLoading] = useState<boolean>(false);
    const [error, setError] = useState<string | null>(null);

    if (isScheduleEmpty(schedule)) {
        return (
            <div className="w-full p-6">
                <h1 className="text-2xl font-bold mb-4 text-gray-800">Weekly Schedule</h1>
                <p className="text-gray-600">No schedule available. Please generate a schedule.</p>
            </div>
        );
    }
    if (!schedule) {
        return (
            <div />
        )
    }

    // Download Rooster XLSX file
    const downloadRooster = async () => {
        setLoading(true);
        setError(null);
        try {
            const res = await fetch(`http://localhost:8080/schedules/${year}/${week}/download`, {
                method: 'GET',
            });
            if (!res.ok) {
                throw new Error(`Failed to download rooster: ${res.status}`);
            }
            const blob = await res.blob();
            const url = window.URL.createObjectURL(blob);
            const a = document.createElement('a');
            a.href = url;
            const fileName = `rooster year${year} week${week}.xlsx`;
            a.download = fileName;
            document.body.appendChild(a);
            a.click();
            a.remove();
            window.URL.revokeObjectURL(url);
        } catch (err: any) {
            setError(err.message || "Something went wrong");
        } finally {
            setLoading(false);
        }
    };

    // Helper: Calculate total hours worked for each employee
    const calculateEmployeeOvertime = () => {
        if (!schedule || !employees) return {};
        const overtime: Record<number, number> = {};
        Object.values(employees).forEach(emp => {
            let total = 0;
            days.forEach(day => {
                schedule[day]?.forEach(shift => {
                    if (shift.employeeId === emp.id && shift.startTime && shift.endTime) {
                        // Calculate hours (e.g. 9:30-17:00 = 7.50)
                        const [startH, startM] = shift.startTime.split(":").map(Number);
                        const [endH, endM] = shift.endTime.split(":").map(Number);
                        const start = startH + startM / 60;
                        const end = endH + endM / 60;
                        total += end - start;
                    }
                });
            });
            overtime[emp.id] = parseFloat((total - emp.minHours).toFixed(2));
        });
        return overtime;
    };
    const overtime = calculateEmployeeOvertime();

    // @ts-ignore
    return (
        <div className="w-full p-6">
            {/* Header + Edit Mode Toggle */}
            <div className="flex items-center gap-4">
                <h1 className="text-2xl font-bold text-gray-800">Weekly Schedule</h1>
                <div className="flex items-center gap-2">
                    <span className="text-sm text-gray-600">Edit Mode</span>
                    <label className="relative inline-flex items-center cursor-pointer">
                        <input
                            type="checkbox"
                            className="sr-only peer"
                            checked={editMode}
                            onChange={() => setEditMode(!editMode)}
                        />
                        <div className="w-11 h-6 bg-gray-300 peer-focus:outline-none peer-focus:ring-2 peer-focus:ring-blue-500 rounded-full peer peer-checked:bg-blue-600 transition-all"></div>
                        <div className="absolute left-1 top-1 w-4 h-4 bg-white rounded-full transition-transform peer-checked:translate-x-5"></div>
                    </label>
                </div>
                <button
                    className="bg-green-600 text-white px-3 py-1 text-sm rounded-full hover:bg-green-700 transition-all"
                    onClick={downloadRooster}
                    disabled={loading}
                >
                    {loading ? "Downloading..." : "Download Rooster in .xslx format"}
                </button>
            </div>
            <br/>
            <div className="grid grid-cols-7 gap-4">
                {days.map((day) => (
                    <div
                        key={day}
                        className="bg-white rounded-2xl shadow-md p-3 flex flex-col items-center border border-gray-200 hover:shadow-lg transition"
                    >
                        <h2 className="text-lg font-semibold text-gray-700 mb-2 capitalize">{day}</h2>

                        {/* Existing shifts */}
                        {schedule[day]
                            // Sort shifts by start time
                            ?.sort((a, b) => a.startTime.localeCompare(b.startTime))
                            .map((shift: Shift, index: number) => (
                            <div
                                key={index}
                                className="w-full bg-blue-100 text-blue-800 rounded-xl p-2 mb-2 text-center"
                            >
                                <span className="block font-medium">
                                    {employees ? (employees[shift.employeeId] ? (`${employees[shift.employeeId]?.name}`) : "No employee assigned" ) : ""}
                                </span>
                                <span className="text-xs text-gray-600">
                                    {shift.startTime} - {shift.endTime}
                                </span>

                                {/* Edit & Remove Buttons visible only in edit mode */}
                                { editMode && (
                                    <div className="flex justify-center gap-2 mt-2">
                                        {/* Edit Shift */}
                                        <button
                                            disabled
                                            onClick={() => onEditShift?.(shift.shiftId)}
                                            className="opacity-50 cursor-not-allowed bg-orange-400 hover:bg-orange-400 text-white px-2 py-1 rounded-lg text-xs transition"
                                        >
                                            Edit
                                        </button>
                                        {/* Remove Shift */}
                                        <button
                                            onClick={() => onRemoveShift?.(shift.shiftId)}
                                            className="bg-red-500 hover:bg-red-600 text-white px-2 py-1 rounded-lg text-xs transition"
                                        >
                                            Remove
                                        </button>
                                    </div>
                                )}

                            </div>
                        ))}

                        {/* No shifts */}
                        {!schedule[day]?.length && (
                            <span className="text-gray-400 text-sm mb-2">No shifts</span>
                        )}

                        {/* Add Shift Button — only visible in edit mode */}
                        {editMode && (
                            <button
                                onClick={() => onAddShift?.(day)}
                                className="bg-green-500 hover:bg-green-600 text-white px-3 py-1 mt-2 rounded-lg text-xs transition"
                            >
                                + Add Shift
                            </button>
                        )}
                    </div>
                ))}
            </div>

            {/* Overtime/Saldo Section */}
            <div className="mt-8 flex justify-left">
                <div className="w-auto max-w-xl">
                    <table className="min-w-full border text-sm">
                        <thead>
                            <tr>
                                <th className="border px-2 py-1 bg-gray-100">PER WEEK</th>
                                {employees && Object.values(employees).map(emp => (
                                    <th key={emp.id} className="border px-2 py-1 bg-gray-100">{emp.name}</th>
                                ))}
                            </tr>
                        </thead>
                        <tbody>
                            <tr>
                                <td className="border px-2 py-1">saldo oud</td>
                                {employees && Object.values(employees).map(emp => (
                                    <td key={emp.id} className="border px-2 py-1"></td>
                                ))}
                            </tr>
                            <tr>
                                <td className="border px-2 py-1">uren (+/-)</td>
                                {employees && Object.values(employees).map(emp => (
                                    <td key={emp.id} className="border px-2 py-1 font-bold text-blue-700">{overtime[emp.id]}</td>
                                ))}
                            </tr>
                            <tr>
                                <td className="border px-2 py-1">saldo nieuw</td>
                                {employees && Object.values(employees).map(emp => (
                                    <td key={emp.id} className="border px-2 py-1"></td>
                                ))}
                            </tr>
                            <tr>
                                <td className="border px-2 py-1">uitbetaald</td>
                                {employees && Object.values(employees).map(emp => (
                                    <td key={emp.id} className="border px-2 py-1"></td>
                                ))}
                            </tr>
                            <tr>
                                <td className="border px-2 py-1">ziekte-uren</td>
                                {employees && Object.values(employees).map(emp => (
                                    <td key={emp.id} className="border px-2 py-1"></td>
                                ))}
                            </tr>
                        </tbody>
                    </table>
                </div>
            </div>
        </div>
    );
};


export default WeeklySchedule;
