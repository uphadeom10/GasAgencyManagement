import {
  BrowserRouter as Router,
  Routes,
  Route,
  Navigate,
} from "react-router-dom";
import Page from "./pages/page";
import Dashboard from "./components/admin/Dashboard";
import "bootstrap/dist/css/bootstrap.min.css";
import Inventory from "./components/admin/Inventory/Inventory";
import DailyAssignmentPage from "./components/manager/DailyAssignment";
import VehicleManagementPage from "./components/manager/VehicleManagementPage";
//import Team from "./components/admin/Team";
import Team from "./components/admin/team/Team";
import Products from "./components/admin/product/Product";
import Login from "./pages/Login";
import ProfilePage from "./components/common/Profile";
import RoleBasedRoute from "./auth/RoleBasedRoute";
import { useAuth } from "./auth/AuthProvider";
import ManagerMainLayout from "./pages/managerpage";
import GasAgencyConnections from "./components/manager/Connections";
import Unauthorized from "./pages/Unauthorized";

const App = () => {
  return (
    <Routes>
      <Route path="/login" element={<Login />} />
      <Route path="/unauthorized" element={<Unauthorized />} />
      <Route path="/" element={<Navigate to="/login" replace />} />

      {/* Admin Routes */}
      <Route element={<RoleBasedRoute allowedRoles={["Admin"]} />}>
        <Route
          path="/dashboard"
          element={
            <Page>
              <Dashboard />
            </Page>
          }
        />
        <Route
          path="/inventory"
          element={
            <Page>
              <Inventory />
            </Page>
          }
        />
        <Route
          path="/orders"
          element={
            <Page>
              <DailyAssignmentPage />
            </Page>
          }
        />
        <Route
          path="/team"
          element={
            <Page>
              <Team />
            </Page>
          }
        />
        <Route
          path="/products"
          element={
            <Page>
              <Products />
            </Page>
          }
        />
        <Route
          path="/profile"
          element={
            <Page>
              <ProfilePage />
            </Page>
          }
        />
      </Route>

      {/* Manager Routes */}
      <Route element={<RoleBasedRoute allowedRoles={["Manager"]} />}>
        <Route
          path="/manager/dashboard"
          element={
            <ManagerMainLayout>
              <Dashboard />
            </ManagerMainLayout>
          }
        />
        <Route
          path="/manager/connections"
          element={
            <ManagerMainLayout>
              <GasAgencyConnections />
            </ManagerMainLayout>
          }
        />
        <Route
          path="/manager/orders"
          element={
            <ManagerMainLayout>
              <DailyAssignmentPage />
            </ManagerMainLayout>
          }
        />
        <Route
          path="/manager/inventory"
          element={
            <ManagerMainLayout>
              <Inventory />
            </ManagerMainLayout>
          }
        />
        <Route
          path="/manager/products"
          element={
            <ManagerMainLayout>
              <Products />
            </ManagerMainLayout>
          }
        />
        <Route
          path="/manager/vehicle-management"
          element={
            <ManagerMainLayout>
              <VehicleManagementPage />
            </ManagerMainLayout>
          }
        />
        <Route
          path="/manager/profile"
          element={
            <ManagerMainLayout>
              <ProfilePage />
            </ManagerMainLayout>
          }
        />
      </Route>
    </Routes>
  );
};

export default App;