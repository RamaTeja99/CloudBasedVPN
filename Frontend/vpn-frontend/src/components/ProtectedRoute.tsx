import { Navigate, Outlet } from 'react-router-dom';

interface ProtectedRouteProps {
  isAllowed: boolean;
  redirectPath?: string;
}

export default function ProtectedRoute({
  isAllowed,
  redirectPath = '/login',
}: ProtectedRouteProps) {
  // If not allowed, redirect to the specified path (default is '/login')
  if (!isAllowed) {
    return <Navigate to={redirectPath} replace />;
  }

  // If allowed, render the child routes
  return <Outlet />;
}