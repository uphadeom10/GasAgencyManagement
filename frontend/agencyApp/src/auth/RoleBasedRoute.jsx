// auth/RoleBasedRoute.js
import { useAuth } from "./AuthProvider";
import { Navigate, Outlet } from "react-router-dom";

const RoleBasedRoute = ({ allowedRoles, children }) => {
  const { role, token } = useAuth();

  if (!token) {
    return <Navigate to="/login" replace />;
  }

  if (!allowedRoles.includes(role)) {
    return <Navigate to="/unauthorized" replace />;
  }

  return children ? children : <Outlet />;
};

export default RoleBasedRoute;