import React from "react";
import { Link, useLocation } from "react-router-dom";
import { motion, AnimatePresence } from "framer-motion";
import "bootstrap/dist/css/bootstrap.min.css";
import "bootstrap-icons/font/bootstrap-icons.css";
import { useAuth } from "../../auth/AuthProvider";

const Sidebar = ({ collapsed, isMobile, onItemClick }) => {
  const location = useLocation();
  const { token, user } = useAuth();
  const role = user?.role || "Guest";
  const username = user?.name || "Guest";

  const menuItems = [
    { id: 1, label: "Dashboard", icon: "bi bi-bar-chart-line", path: "/dashboard" },
    { id: 2, label: "Inventory", icon: "bi-box-seam", path: "/inventory" },
    { id: 3, label: "Orders", icon: "bi-cart", path: "/orders" },
    { id: 4, label: "Team", icon: "bi-people", path: "/team" },
    { id: 5, label: "Products", icon: "bi-box", path: "/products" },
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
        height: isMobile ? "calc(100vh - 56px)" : "100vh",
        position: "sticky",
        top: "56px",
        overflowY: "auto",
        overflowX: "hidden",
      }}
    >
      {/* Menu Items */}
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

      {/* User Profile & Footer */}
      <div
        className="border-top border-white border-opacity-10 pt-3 pb-2"
        style={{ flexShrink: 0 }}
      >
        <motion.div
          className="d-flex align-items-center mb-3 p-2 rounded"
          whileHover={{ backgroundColor: "rgba(255,255,255,0.1)" }}
          style={{ cursor: "pointer" }}
        >
          {/* SVG Profile Avatar */}
          <div
            className="rounded-circle overflow-hidden d-flex align-items-center justify-content-center"
            style={{ width: "40px", height: "40px", minWidth: "40px" }}
          >
            <svg viewBox="0 0 40 40" xmlns="http://www.w3.org/2000/svg" width="40" height="40">
              <circle cx="20" cy="20" r="20" fill="rgba(255,255,255,0.2)" />
              <circle cx="20" cy="15" r="8" fill="white" opacity="0.9" />
              <ellipse cx="20" cy="35" rx="13" ry="10" fill="white" opacity="0.9" />
            </svg>
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
                <div className="small opacity-75">{role}</div>
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