import React from "react";
import { BrowserRouter as Router, Routes, Route, NavLink, Navigate } from "react-router-dom";
import EmployeesManagement from "./EmployeesManagement";
import ScheduleView from "./ScheduleView";

// Main Application Component
function App() {
    return (
        <Router>
            <div className="min-h-screen bg-gray-50">
                {/* Home Menu Navigation */}
                <nav className="bg-gray-100 p-4 flex gap-4 shadow-md">
                    <NavLink
                        to="/schedules"
                        className={({ isActive }) =>
                            `px-4 py-2 rounded transition-all ${
                                isActive
                                    ? "bg-blue-600 text-white shadow-md"
                                    : "bg-white border text-gray-700 hover:bg-blue-100"
                            }`
                        }
                    >
                        Schedules Management
                    </NavLink>

                    <NavLink
                        to="/employees"
                        className={({ isActive }) =>
                            `px-4 py-2 rounded transition-all ${
                                isActive
                                    ? "bg-blue-600 text-white shadow-md"
                                    : "bg-white border text-gray-700 hover:bg-green-100"
                            }`
                        }
                    >
                        Employees Management
                    </NavLink>
                </nav>

                {/* Page Content */}
                <div className="p-6">
                    <Routes>
                        <Route path="/" element={<Navigate to="/schedules" />} />
                        <Route path="/schedules" element={<ScheduleView />} />
                        <Route path="/employees" element={<EmployeesManagement />} />
                    </Routes>
                </div>
            </div>
        </Router>
    );
}

export default App;
