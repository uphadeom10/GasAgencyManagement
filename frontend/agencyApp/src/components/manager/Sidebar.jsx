import React from "react";
import { Link, useLocation } from "react-router-dom";
import { motion, AnimatePresence } from "framer-motion";
import "bootstrap/dist/css/bootstrap.min.css";
import "bootstrap-icons/font/bootstrap-icons.css";
import { useAuth } from "../../auth/AuthProvider";
const Sidebar = ({ collapsed, isMobile, onItemClick }) => {
  const location = useLocation();
  const { token, user } = useAuth();
  const role = user?.role || "Guest"; // Default to 'Guest' if no role is found
  const username = user?.name || "Guest"; // Default to 'Guest' if no username is found
  const menuItems = [
    { id: 1, label: "Dashboard", icon: "bi bi-bar-chart-line", path: "/manager/dashboard" },
    { id: 2, label: "Inventory", icon: "bi-box-seam", path: "/manager/inventory" },
    { id: 3, label: "Manage Assignments", icon: "bi bi-clipboard-check", path: "/manager/orders" },
    { id: 5, label: "Products", icon: "bi-box", path: "/manager/products" },
    { id: 6, label: "Connections", icon: "bi-plus-circle", path: "/manager/connections" },
    { id: 7, label: "Vehicle Management", icon: "bi bi-car-front", path: "/manager/vehicle-management" },
  ];

  const itemVariants = {
    hidden: { opacity: 0, x: -20 },
    visible: { opacity: 1, x: 0 },
    hover: { backgroundColor: "rgba(255, 255, 255, 0.1)" },
    active: {
      backgroundColor: "rgba(255, 255, 255, 0.2)",
      borderLeft: "4px solid white",
    },
  };

  return (
    <div
      className={`d-flex flex-column ${collapsed ? "px-2" : "px-3"}`}
      style={{
        background: "linear-gradient(180deg, #2b5876 0%, #4e4376 100%)",
        color: "white",
        width: "100%",
        height: isMobile ? "calc(100vh - 56px)" : "100vh", // Account for navbar height
        position: "sticky",
        top: "56px", // Stick below navbar
        overflowY: "auto",
        overflowX: "hidden",
      }}
    >
      {/* Menu Items - with flex-grow to take available space */}
      <div className="flex-grow-1" style={{ overflowY: "auto" }}>
        <ul className="nav flex-column mt-3">
          {menuItems.map((item) => (
            <motion.li
              key={item.id}
              className="nav-item mb-2"
              initial="hidden"
              animate="visible"
              variants={itemVariants}
              whileHover={location.pathname !== item.path ? "hover" : {}}
              custom={location.pathname === item.path}
            >
              <Link
                to={item.path}
                className={`nav-link d-flex align-items-center ${
                  collapsed ? "justify-content-center" : ""
                }`}
                style={{
                  color: "white",
                  borderRadius: "8px",
                  padding: collapsed ? "0.75rem" : "0.75rem 1rem",
                  textDecoration: "none",
                  backgroundColor:
                    location.pathname === item.path
                      ? "rgba(255,255,255,0.2)"
                      : "transparent",
                  borderLeft:
                    location.pathname === item.path
                      ? "4px solid white"
                      : "none",
                }}
                onClick={onItemClick}
              >
                <i
                  className={`bi ${item.icon}`}
                  style={{
                    fontSize: "1.1rem",
                    minWidth: "24px",
                  }}
                ></i>

                <AnimatePresence>
                  {!collapsed && (
                    <motion.span
                      initial={{ opacity: 0, x: -10 }}
                      animate={{ opacity: 1, x: 0 }}
                      exit={{ opacity: 0, x: -10 }}
                      transition={{ duration: 0.2 }}
                      className="ms-2"
                    >
                      {item.label}
                    </motion.span>
                  )}
                </AnimatePresence>

                {!collapsed && location.pathname === item.path && (
                  <i className="bi bi-chevron-right ms-auto"></i>
                )}
              </Link>
            </motion.li>
          ))}
        </ul>
      </div>

      {/* User Profile & Footer - will stay at bottom */}
      <div
        className="border-top border-white border-opacity-10 pt-3 pb-2"
        style={{ flexShrink: 0 }} // Prevent footer from growing
      >
        <motion.div
          className="d-flex align-items-center mb-3 p-2 rounded"
          whileHover={{ backgroundColor: "rgba(255,255,255,0.1)" }}
          style={{ cursor: "pointer" }}
        >
          <div
            className="rounded-circle bg-white bg-opacity-20 d-flex align-items-center justify-content-center"
            style={{
              width: "40px",
              height: "40px",
              minWidth: "40px",
            }}
          >
            <i className="bi bi-person-fill"></i>
          </div>

          <AnimatePresence>
            {!collapsed && (
              <motion.div
                initial={{ opacity: 0, x: -10 }}
                animate={{ opacity: 1, x: 0 }}
                exit={{ opacity: 0, x: -10 }}
                transition={{ duration: 0.2 }}
                className="ms-3"
              >
                <div className="fw-medium">{username}</div>
                <div className="small opacity-75">{role}@example.com</div>
              </motion.div>
            )}
          </AnimatePresence>
        </motion.div>

        <div className="small text-center opacity-75 border-top border-white border-opacity-10 pt-2">
          &copy; {new Date().getFullYear()} Gas Agency
        </div>
      </div>
    </div>
  );
};

export default Sidebar;
