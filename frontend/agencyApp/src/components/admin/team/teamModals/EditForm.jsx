import { useState, useEffect } from "react";
import { motion } from "framer-motion";
import { useAuth } from "../../../../auth/AuthProvider"; // Adjust the import path as necessary

const base_url = import.meta.env.VITE_API_URL || ""; // Ensure this is set in your .env file
const EditUserForm = ({ onSave, onCancel, member }) => {
  const { user } = useAuth();
  const loggedInUserId = user?.userId;
  const id = member.id || "";

  const [newUser, setNewUser] = useState({
    id: "",
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

  const fetchMember = async () => {
    try {
      const token = sessionStorage.getItem("token");

      const response = await fetch(
        `${base_url}/masters/getUserById/${id}`,
        {
          method: "GET",
          headers: {
            "Content-Type": "application/json",
            Authorization: `Bearer ${token}`,
          },
        }
      );

      if (!response.ok) {
        throw new Error(`HTTP error! status: ${response.status}`);
      }

      const data = await response.json();
      const result = data.result;

      setNewUser({
        id: result.Id,
        firstName: result.firstName,
        lastName: result.lastName,
        mobileNumber: result.mobileNumber,
        aadharCardNumber: result.aadharCardNumber,
        userName: result.userName,
        password: "",
        roleId: { id: JSON.parse(result.rolee).id },
        isActive: result.isActive,
        createdBy: loggedInUserId,
        lastModifiedBy: loggedInUserId,
      });
    } catch (error) {
      console.error("Error fetching member data:", error);
    }
  };

  useEffect(() => {
    fetchMember();
  }, []);

  const handleChange = (e) => {
    const { name, value } = e.target;

    if (name === "roleId.id") {
      setNewUser((prev) => ({
        ...prev,
        roleId: {
          id: value,
        },
      }));
    } else {
      setNewUser((prev) => ({
        ...prev,
        [name]: name === "isActive" ? value === "true" : value,
      }));
    }
  };

  const handleSubmit = (e) => {
    e.preventDefault();
    onSave(newUser);
  };

  // Animation variants
  const containerVariants = {
    hidden: { opacity: 0 },
    visible: {
      opacity: 1,
      transition: {
        staggerChildren: 0.1,
        delayChildren: 0.2,
      },
    },
  };

  const itemVariants = {
    hidden: { y: 20, opacity: 0 },
    visible: {
      y: 0,
      opacity: 1,
      transition: {
        type: "spring",
        stiffness: 100,
      },
    },
  };

  return (
    <div className="container-fluid p-0">
      <div className="row g-0">
        <div className="col-12">
          <div className="p-4">
            <motion.div
              initial={{ y: -20, opacity: 0 }}
              animate={{ y: 0, opacity: 1 }}
              transition={{ type: "spring", stiffness: 100 }}
            >
              <h3 className="mb-4 fw-bold gradient-text">Edit User</h3>
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
                  [
                    "mobileNumber",
                    "Mobile Number",
                    "tel",
                    "col-md-6",
                    "[0-9]{10}",
                    "Please enter a 10-digit mobile number.",
                  ],
                  [
                    "aadharCardNumber",
                    "Aadhar Card Number",
                    "text",
                    "col-md-6",
                    "^[0-9]{12}$",
                    "Please enter a valid 12-digit Aadhar number.",
                  ],
                  ["userName", "Username", "text", "col-md-6"],
                  ["password", "Password", "password", "col-md-6"],
                ].map(([key, label, type, colClass, pattern, errorMessage]) => (
                  <motion.div
                    className={colClass}
                    key={key}
                    variants={itemVariants}
                  >
                    <label className="form-label fw-medium text-dark mb-1">
                      {label}
                    </label>
                    <input
                      type={type}
                      className="form-control border-1 border-dark rounded-3 py-2 focus-ring focus-ring-primary"
                      name={key}
                      value={newUser[key]}
                      onChange={handleChange}
                      autoComplete="off"
                      pattern={pattern}
                      title={errorMessage}
                    />
                  </motion.div>
                ))}

                <motion.div className="col-md-6" variants={itemVariants}>
                  <label className="form-label fw-medium text-dark mb-1">
                    Role
                  </label>
                  <select
                    className="form-select border- border-dark rounded-3 py-2 focus-ring focus-ring-primary"
                    name="roleId.id"
                    value={newUser.roleId.id}
                    onChange={handleChange}
                    required
                  >
                    <option value="1">Admin</option>
                    <option value="2">Manager</option>
                    <option value="3">User</option>
                  </select>
                </motion.div>

                <motion.div className="col-md-6" variants={itemVariants}>
                  <label className="form-label fw-medium text-dark mb-1">
                    User Status
                  </label>
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
                  Save Changes
                </motion.button>
              </motion.div>
            </motion.form>
          </div>
        </div>
      </div>

      {/* Add this to your global CSS */}
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
      `}</style>
    </div>
  );
};

export default EditUserForm;
