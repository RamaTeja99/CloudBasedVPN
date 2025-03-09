import { BrowserRouter, Routes, Route, Navigate } from "react-router-dom";
import NavigationBar from "@/components/Navbar";
import Login from "@/pages/Login";
import Register from "@/pages/Register";
import UserDashboard from "@/pages/UserDashboard";
import AdminDashboard from "@/pages/AdminDashboard";
import PaymentPage from "@/pages/PaymentPage";
import HomePage from "@/pages/HomePage";
//import ProtectedRoute from "@/components/ProtectedRoute";

export default function App() {
  const isAuthenticated = !!localStorage.getItem("token");
  const isAdmin = localStorage.getItem("role") === "ADMIN";

  return (
    <BrowserRouter>
      <div className="min-h-screen bg-background text-foreground">
        <NavigationBar />
        <div className="flex-grow-1">
          <Routes>
            {/* Public Routes */}
            <Route path="/" element={<HomePage />} />
            <Route path="/login" element={<Login />} />
            <Route path="/register" element={<Register />} />

            {/* Protected Routes for Authenticated Users */}
            <Route>
              <Route path="/dashboard" element={<UserDashboard />} />
              <Route path="/payment" element={<PaymentPage />} />
            </Route>

            {/* Protected Routes for Admins Only */}
            <Route
              // element={
              //   <ProtectedRoute
              //     isAllowed={isAuthenticated && isAdmin}
              //     redirectPath="/login"
              //   />
              // }
            >
              <Route path="/admin" element={<AdminDashboard />} />
            </Route>

            {/* Default Redirect */}
            <Route path="*" element={<Navigate to="/" />} />
          </Routes>
        </div>
      </div>
    </BrowserRouter>
  );
}