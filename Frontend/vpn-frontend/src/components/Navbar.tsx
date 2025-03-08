import { Link } from "react-router-dom";
import { Button } from "@/components/ui/button";
import { Avatar, AvatarFallback, AvatarImage } from "@/components/ui/avatar";
import { DropdownMenu, DropdownMenuContent, DropdownMenuItem, DropdownMenuTrigger } from "@/components/ui/dropdown-menu";
import {DarkModeToggle} from "@/components/DarkModeToggle";

export default function NavigationBar() {
  const isAuthenticated = !localStorage.getItem("token");
  const userRole = localStorage.getItem("role");

  return (
    <nav className="bg-background border-b border-dark-secondary">
      <div className="container flex items-center justify-between h-16">
        <Link to="/" className="text-xl font-bold text-dark-primary">
          SecureVPN
        </Link>
        <div className="flex items-center space-x-4">
          <DarkModeToggle /> 
          {!isAuthenticated ? (
            <>
              <Link to="/login">
                <Button variant="ghost" className="text-dark-foreground">
                  Login
                </Button>
              </Link>
              <Link to="/register">
                <Button variant="ghost" className="text-dark-foreground">
                  Register
                </Button>
              </Link>
            </>
          ) : (
            <DropdownMenu>
              <DropdownMenuTrigger>
                <Avatar>
                  <AvatarImage src="https://github.com/shadcn.png" />
                  <AvatarFallback>U</AvatarFallback>
                </Avatar>
              </DropdownMenuTrigger>
              <DropdownMenuContent>
                <DropdownMenuItem>
                  <Link to={userRole === "ADMIN" ? "/admin" : "/dashboard"}>
                    Dashboard
                  </Link>
                </DropdownMenuItem>
                <DropdownMenuItem>Logout</DropdownMenuItem>
              </DropdownMenuContent>
            </DropdownMenu>
          )}
        </div>
      </div>
    </nav>
  );
}
