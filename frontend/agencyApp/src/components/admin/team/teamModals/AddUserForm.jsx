import React, { useState, useEffect } from "react";
import { motion } from "framer-motion";
import { useAuth } from "../../../../auth/AuthProvider";

const AddUserForm = ({ onAdd, onCancel }) => {
  const { user } = useAuth();
  const base_url = import.meta.env.VITE_API_URL || "";
  const token = sessionStorage.getItem("token");
  const [roles, setRoles] = useState([]);

  useEffect(() => {
    const fetchRole = async () => {
      try {
        const response = await fetch(`${base_url}/masters/getRoleList`, {
          method: "POST",
          headers: {
            "Content-Type": "application/json",
            Authorization: `Bearer ${token}`,
          },
          body: JSON.stringify({}),
        });
        if (!response.ok) {
          throw new Error("Network response was not ok");
        }
        const data = await response.json();
        setRoles(data.result);
      } catch (error) {
        console.error("Error fetching roles:", error);
      }
    };
    fetchRole();
  }, []);

  const loggedInUserId = user?.userId;
  const [newUser, setNewUser] = useState({
    firstName: "",
    lastName: "",
    mobileNumber: "",
    aadharCardNumber: "",
    userName: "",
    password: "",
    roleId: { id: "1" },
    isActive: true,
    createdBy: loggedInUserId,
    lastModifiedBy: loggedInUserId,
  });

  const handleChange = (e) => {
    const { name, value } = e.target;
    if (name === "roleId.id") {
      setNewUser((prev) => ({ ...prev, roleId: { id: value } }));
    } else {
      setNewUser((prev) => ({
        ...prev,
        [name]: name === "isActive" ? value === "true" : value,
      }));
    }
  };

  const handleSubmit = (e) => {
    e.preventDefault();
    onAdd(newUser);
  };

  const containerVariants = {
    hidden: { opacity: 0 },
    visible: {
      opacity: 1,
      transition: { staggerChildren: 0.1, delayChildren: 0.2 },
    },
  };

  const itemVariants = {
    hidden: { y: 20, opacity: 0 },
    visible: { y: 0, opacity: 1, transition: { type: "spring", stiffness: 100 } },
  };

  return (
    <div className="container-fluid p-0 form-container">
      <div className="row g-0">
        <div className="col-12">
          <div className="p-4">
            <motion.div
              initial={{ y: -20, opacity: 0 }}
              animate={{ y: 0, opacity: 1 }}
              transition={{ type: "spring", stiffness: 100 }}
            >
              <h3 className="mb-4 fw-bold gradient-text">Add New User</h3>
            </motion.div>

            <motion.form
              onSubmit={handleSubmit}
              autoComplete="off"
              className="bg-white p-4 rounded-4 shadow-lg"
              initial="hidden"
              animate="visible"
              variants={containerVariants}
            >
              <div className="row g-4 mb-3">
                {[
                  ["firstName", "First Name", "text", "col-md-6"],
                  ["lastName", "Last Name", "text", "col-md-6"],
                  ["mobileNumber", "Mobile Number", "tel", "col-md-6", "[0-9]{10}", "Please enter a 10-digit mobile number."],
                  ["aadharCardNumber", "Aadhar Card Number", "text", "col-md-6", "^[0-9]{12}$", "Please enter a valid 12-digit Aadhar number."],
                  ["userName", "Username", "text", "col-md-6"],
                  ["password", "Password", "password", "col-md-6"],
                ].map(([key, label, type, colClass, pattern, errorMessage]) => (
                  <motion.div className={colClass} key={key} variants={itemVariants}>
                    <label className="form-label fw-medium text-dark mb-1">{label}</label>
                    <input
                      type={type}
                      className="form-control border-1 border-dark rounded-3 py-2 focus-ring focus-ring-primary"
                      name={key}
                      value={newUser[key]}
                      onChange={handleChange}
                      autoComplete="off"
                      pattern={pattern}
                      title={errorMessage}
                      required
                    />
                  </motion.div>
                ))}

                <motion.div className="col-md-6" variants={itemVariants}>
                  <label className="form-label fw-medium text-dark mb-1">Role</label>
                  <select
                    className="form-select border-1 border-dark rounded-3 py-2 focus-ring focus-ring-primary"
                    name="roleId.id"
                    value={newUser.roleId.id}
                    onChange={handleChange}
                    required
                  >
                    {roles.map((role) => (
                      <option key={role.Id} value={role.Id}>{role.Role}</option>
                    ))}
                  </select>
                </motion.div>

                <motion.div className="col-md-6" variants={itemVariants}>
                  <label className="form-label fw-medium text-dark mb-1">User Status</label>
                  <select
                    name="isActive"
                    className="form-select border-1 border-dark rounded-3 py-2 focus-ring focus-ring-primary"
                    value={newUser.isActive}
                    onChange={handleChange}
                  >
                    <option value="true">Active</option>
                    <option value="false">Inactive</option>
                  </select>
                </motion.div>
              </div>

              <motion.div
                className="d-flex justify-content-end gap-3 border-top pt-4 mt-3"
                variants={itemVariants}
              >
                <motion.button
                  type="button"
                  className="btn btn-outline-primary px-4 py-2 rounded-3 fw-medium"
                  onClick={onCancel}
                  whileHover={{ scale: 1.03 }}
                  whileTap={{ scale: 0.98 }}
                >
                  Cancel
                </motion.button>
                <motion.button
                  type="submit"
                  className="btn btn-gradient px-4 py-2 rounded-3 fw-medium shadow-sm"
                  whileHover={{ scale: 1.03 }}
                  whileTap={{ scale: 0.98 }}
                >
                  Add User
                </motion.button>
              </motion.div>
            </motion.form>
          </div>
        </div>
      </div>

      <style>{`
        .gradient-text {
          background: linear-gradient(90deg, #3b82f6, #8b5cf6);
          -webkit-background-clip: text;
          background-clip: text;
          color: transparent;
          display: inline-block;
        }
        .btn-gradient {
          background: linear-gradient(135deg, #3b82f6, #8b5cf6);
          color: white;
          border: none;
          transition: all 0.3s ease;
        }
        .btn-gradient:hover {
          background: linear-gradient(135deg, #2563eb, #7c3aed);
          transform: translateY(-2px);
        }
        .focus-ring:focus {
          border-color: #8b5cf6;
          box-shadow: 0 0 0 0.25rem rgba(139, 92, 246, 0.25);
        }
        .form-control, .form-select {
          transition: all 0.3s ease;
        }
        .form-control:hover, .form-select:hover {
          border-color: #a78bfa;
        }
        .form-container {
          background-color: linear-gradient(180deg, #e8ecf1 0%, #dcd9e6 100%);
          color: rgb(26, 22, 22);
        }
        .form-input {
          border: 1px solid #F7F7F7;
        }
        .form-button {
          background-color: #2196F3;
          color: #ffffff;
        }
      `}</style>
    </div>
  );
};

export default AddUserForm;