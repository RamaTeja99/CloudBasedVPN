import { useState } from "react";
import { Link, useNavigate } from "react-router-dom";
import { Button } from "@/components/ui/button";
import { DarkModeToggle } from "@/components/DarkModeToggle";
import { Menu, X } from "lucide-react";

export default function NavigationBar() {
  const [isMenuOpen, setIsMenuOpen] = useState(false);
  const navigate = useNavigate();

  const isAuthenticated = !!localStorage.getItem("token");

  return (
    <nav className="bg-background border-b border-gray-700">
      <div className="container flex items-center justify-between h-16 px-4 md:px-6">
        {/* Logo on the left */}
        <Link to="/" className="text-2xl font-semibold text-primary">
          SecureVPN
        </Link>

        {/* Right Side: Pushed using `ml-auto` */}
        <div className="ml-auto flex items-center space-x-4">
          <DarkModeToggle />
          {!isAuthenticated ? (
            <>
              <Link to="/login">
                <Button variant="ghost">Login</Button>
              </Link>
              <Link to="/register">
                <Button className="bg-blue-500 hover:bg-blue-600 text-white">
                  Register
                </Button>
              </Link>
            </>
          ) : (
            <Button onClick={() => navigate("/dashboard")} variant="ghost">
              Dashboard
            </Button>
          )}
        </div>

        {/* Mobile Menu Button (Hidden on Desktop) */}
        <button
          className="md:hidden text-primary ml-4"
          onClick={() => setIsMenuOpen(!isMenuOpen)}
        >
          {isMenuOpen ? <X size={28} /> : <Menu size={28} />}
        </button>
      </div>
    </nav>
  );
}
