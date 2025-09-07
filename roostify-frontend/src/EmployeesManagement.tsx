import React, { useState, useEffect } from "react";

interface Employee {
    id: number;
    name: string;
    minHours: number;
}

const EmployeesManagement: React.FC = () => {
    const [employees, setEmployees] = useState<Record<number, Employee>>({});
    const [loading, setLoading] = useState<boolean>(true);
    const [error, setError] = useState<string | null>(null);

    // Fetch employees once on component mount
    useEffect(() => {
        const fetchEmployees = async () => {
            try {
                const res = await fetch(`http://localhost:8080/employees`);
                if (!res.ok) throw new Error(`Failed to fetch employees: ${res.status}`);

                const data = await res.json();

                // Transform into a dictionary with id as key
                const transformed: Record<number, Employee> = {};
                Object.values(data).forEach((item: any) => {
                    transformed[item.id] = {
                        id: item.id,
                        name: item.employeeName,
                        minHours: item.minHours,
                    };
                });

                setEmployees(transformed);
            } catch (err: any) {
                console.error(err);
                setError(err.message);
            } finally {
                setLoading(false);
            }
        };

        fetchEmployees();
    }, []);

    return (
        <div className="p-6 bg-white rounded-lg shadow-md">
            <h2 className="text-2xl font-bold mb-4">Employees Management</h2>

            {loading ? (
                <p className="text-gray-500">Loading employees...</p>
            ) : error ? (
                <p className="text-red-500">Error: {error}</p>
            ) : Object.keys(employees).length === 0 ? (
                <p className="text-gray-500">No employees found.</p>
            ) : (
                <div className="overflow-x-auto">
                    <table className="min-w-full border border-gray-200 rounded-lg">
                        <thead>
                        <tr className="bg-gray-100 text-gray-700">
                            <th className="px-4 py-2 border">Employee ID</th>
                            <th className="px-4 py-2 border">Name</th>
                            <th className="px-4 py-2 border">Min Hours</th>
                        </tr>
                        </thead>
                        <tbody>
                        {Object.values(employees).map((emp) => (
                            <tr key={emp.id} className="hover:bg-gray-50 transition">
                                <td className="px-4 py-2 border text-center">{emp.id}</td>
                                <td className="px-4 py-2 border">{emp.name}</td>
                                <td className="px-4 py-2 border text-center">{emp.minHours}</td>
                            </tr>
                        ))}
                        </tbody>
                    </table>
                </div>
            )}
        </div>
    );
};

export default EmployeesManagement;
