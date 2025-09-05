import React, { useState } from "react";

interface AddShiftModal {
    day: string;
    employees: Record<number, { id: number; name: string }>;
    onClose: () => void;
    onConfirm: (employeeId: number, startTime: string, endTime: string) => void;
}

const AddShiftModal: React.FC<AddShiftModal> = ({ day, employees, onClose, onConfirm }) => {
    const [employeeId, setEmployeeId] = useState<number>(employees[0]?.id || 0);
    const [startTime, setStartTime] = useState<string>("09:00");
    const [endTime, setEndTime] = useState<string>("17:00");

    const handleConfirm = () => {
        onConfirm(employeeId, startTime, endTime);
        onClose();
    };

    return (
        <div className="fixed inset-0 bg-black/50 flex items-center justify-center z-50">
            <div className="bg-white p-6 rounded-lg w-96">
                <h2 className="text-xl font-bold mb-4">Add Shift for {day}</h2>

                <label className="block mb-2 text-sm font-medium">Employee</label>
                <select
                    value={employeeId}
                    onChange={(e) => setEmployeeId(Number(e.target.value))}
                    className="w-full p-2 border rounded mb-4"
                >
                    {Object.values(employees).map(emp => (
                        <option key={emp.id} value={emp.id}>{emp.name}</option>
                    ))}
                </select>

                <label className="block mb-2 text-sm font-medium">Start Time</label>
                <input
                    type="time"
                    value={startTime}
                    onChange={(e) => setStartTime(e.target.value)}
                    className="w-full p-2 border rounded mb-4"
                />

                <label className="block mb-2 text-sm font-medium">End Time</label>
                <input
                    type="time"
                    value={endTime}
                    onChange={(e) => setEndTime(e.target.value)}
                    className="w-full p-2 border rounded mb-4"
                />

                <div className="flex justify-end gap-2">
                    <button onClick={onClose} className="px-4 py-2 rounded border">Cancel</button>
                    <button onClick={handleConfirm} className="px-4 py-2 rounded bg-green-500 text-white hover:bg-green-600">Add</button>
                </div>
            </div>
        </div>
    );
};

export default AddShiftModal;
