import React, { useState, useEffect, useCallback } from "react";
import PropTypes from "prop-types";
import { toast, ToastContainer } from "react-toastify";
import { motion, AnimatePresence } from "framer-motion";
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
// Adjust the path as needed
import {
  faUserPlus,
  faCheck,
  faTimes,
  faEdit,
  faTrash,
  faAngleLeft,
  faAngleRight,
  faAnglesLeft,
  faAnglesRight,
  faSearch,
  faSpinner,
} from "@fortawesome/free-solid-svg-icons";

// Components
import EditForm from "./EditForm";
import AddUserForm from "./AddUserForm";

const PAGE_SIZE = 5;
const MAX_VISIBLE_PAGES = 5;
const base_Url = import.meta.env.VITE_API_URL || "";

const Team = () => {
  // State management
  const [teamMembers, setTeamMembers] = useState([]);
  const [searchTerm, setSearchTerm] = useState("");
  const [selectedMember, setSelectedMember] = useState(null);
  const [isEditing, setIsEditing] = useState(false);
  const [isAdding, setIsAdding] = useState(false);
  const [showDeleteModal, setShowDeleteModal] = useState(false);
  const [memberToDelete, setMemberToDelete] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [pagination, setPagination] = useState({
    currentPage: 0,
    totalPages: 0,
    totalElements: 0,
  });
  const path = "../src/assets/no-data.svg";

  // Fetch team members with pagination
  const fetchTeamMembers = useCallback(async (page = 0) => {
    const token = sessionStorage.getItem("token");
    setLoading(true);
    try {
      const response = await fetch(`${base_Url}/masters/getUsersList`, {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
          Authorization: `Bearer ${token}`,
        },
        body: JSON.stringify({
          page,
          size: PAGE_SIZE,
        }),
      });

      if (!response.ok)
        throw new Error(`HTTP error! status: ${response.status}`);

      const data = await response.json();

      setTeamMembers(
        data.result.map((member) => ({
          id: member.Id,
          SrNo: member.Sr_no,
          "Employee Name": member.Employee_name,
          Role: member.Role,
          "Mobile Number": member.Mobile_number,
          status: member.Status,
          userName: member.UserName,
        }))
      );

      setPagination({
        currentPage: page,
        totalPages: data.totalPages || Math.ceil(data.count / PAGE_SIZE),
        totalElements: data.count,
      });
    } catch (err) {
      setError(err.message);
    } finally {
      setLoading(false);
    }
  }, []);

  useEffect(() => {
    fetchTeamMembers();
  }, [fetchTeamMembers]);

  // Filter members based on search term
  const filteredMembers = teamMembers.filter((member) =>
    member["Employee Name"]?.toLowerCase().includes(searchTerm.toLowerCase())
  );

  const headers =
    teamMembers.length > 0
      ? Object.keys(teamMembers[0]).filter((key) => key !== "id")
      : [];

  // Status badge component
  const StatusBadge = ({ status }) => {
    return (
      <motion.span
        className={`badge d-flex align-items-center justify-content-center ${
          status
            ? "bg-success-light text-success"
            : "bg-danger-light text-danger"
        }`}
        style={{
          padding: "0.35em 0.85em",
          borderRadius: "50rem",
          fontSize: "0.875rem",
          fontWeight: 600,
          lineHeight: 1,
          whiteSpace: "nowrap",
          width: "fit-content",
          margin: "0 auto",
        }}
        whileHover={{ scale: 1.05 }}
      >
        <FontAwesomeIcon
          icon={status ? faCheck : faTimes}
          className="me-1"
          style={{ fontSize: "0.8em" }}
        />
        {status ? "Active" : "Inactive"}
      </motion.span>
    );
  };

  // Action handlers
  const handleEdit = (member) => {
    setSelectedMember(member);
    setIsEditing(true);
    setIsAdding(false);
  };

  const confirmDelete = (member) => {
    setMemberToDelete(member);
    setShowDeleteModal(true);
  };

  const handleDelete = async () => {
    if (!memberToDelete?.id) return;

    const token = sessionStorage.getItem("token");
    try {
      const response = await fetch(
        `${base_Url}/masters/deleteUserById/${memberToDelete.id}`,
        {
          method: "DELETE",
          headers: {
            Authorization: `Bearer ${token}`,
          },
        }
      );
      if (response.status === 200) {
        toast.warning("User deleted successfully! cannot be undone" );
      }else if (response.status === 400) {
        toast.error("Failed to delete user. Please try again.");
      }else  {  
        toast.error("something went wrong. Please try again later.");
      }
      if (!response.ok)
        throw new Error(`HTTP error! status: ${response.status}`);

      await fetchTeamMembers(pagination.currentPage);
      setShowDeleteModal(false);
      setMemberToDelete(null);
    } catch (err) {
      setError(err.message);
    }
  };

  const handleSave = async (updatedMember) => {
    const token = sessionStorage.getItem("token");
    try {
      const response = await fetch(`${base_Url}/masters/createAndUpdateUsers`, {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
          Authorization: `Bearer ${token}`,
        },
        body: JSON.stringify(updatedMember),
      });
      if (response.status === 200) {
        toast.success("User updated successfully!");
      }else if (response.status === 400) {
        toast.error("Failed to update user. Please try again.");
      }else  {  
        toast.error("something went wrong. Please try again later.");
      }
      if (!response.ok)
        throw new Error(`HTTP error! status: ${response.status}`);

      await fetchTeamMembers(pagination.currentPage);
      setIsEditing(false);
    } catch (err) {
      setError(err.message);
    }
  };

  const handleAdd = async (newMember) => {
    const token = sessionStorage.getItem("token");
    try {
      const response = await fetch(`${base_Url}/masters/createAndUpdateUsers`, {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
          Authorization: `Bearer ${token}`,
        },
        body: JSON.stringify(newMember),
      });
      if (response.status === 200) {
        toast.success("User added successfully!");
      }else if (response.status === 400) {
        toast.error("Failed to add user. Please try again.");
      }else if (response.status === 409) {
        toast.error("User mobile number or username or aadhar number already exists. Please try again.");        
      }
      else  {  
        toast.error("something went wrong. Please try again later.");
      }

      if (!response.ok)
        throw new Error(`HTTP error! status: ${response.status}`);

      await fetchTeamMembers(0);
      setSearchTerm("");
      setIsAdding(false);
    } catch (err) {
      setError(err.message);
    }
  };

  // Pagination handlers
  const handlePageChange = (newPage) => {
    if (newPage >= 0 && newPage < pagination.totalPages) {
      fetchTeamMembers(newPage);
    }
  };

  const renderPagination = () => {
    const { currentPage, totalPages } = pagination;
    const pages = [];

    let startPage = Math.max(
      0,
      currentPage - Math.floor(MAX_VISIBLE_PAGES / 2)
    );
    let endPage = Math.min(totalPages - 1, startPage + MAX_VISIBLE_PAGES - 1);

    if (endPage - startPage + 1 < MAX_VISIBLE_PAGES) {
      startPage = Math.max(0, endPage - MAX_VISIBLE_PAGES + 1);
    }

    for (let i = startPage; i <= endPage; i++) {
      pages.push(
        <motion.button
          key={i}
          onClick={() => handlePageChange(i)}
          className={`btn ${
            i === currentPage ? "btn-primary" : "btn-outline-primary"
          }`}
          style={{
            minWidth: "40px",
            margin: "0 2px",
            borderRadius: "6px",
          }}
          whileHover={{ scale: 1.05 }}
          whileTap={{ scale: 0.95 }}
        >
          {i + 1}
        </motion.button>
      );
    }

    return (
      <div className="d-flex flex-wrap justify-content-center align-items-center mt-4 gap-2">
        <motion.button
          onClick={() => handlePageChange(0)}
          disabled={currentPage === 0}
          className="btn btn-outline-primary"
          style={{
            minWidth: "40px",
            borderRadius: "6px",
          }}
          whileHover={{ scale: 1.05 }}
          whileTap={{ scale: 0.95 }}
        >
          <FontAwesomeIcon icon={faAnglesLeft} />
        </motion.button>
        <motion.button
          onClick={() => handlePageChange(currentPage - 1)}
          disabled={currentPage === 0}
          className="btn btn-outline-primary"
          style={{
            minWidth: "40px",
            borderRadius: "6px",
          }}
          whileHover={{ scale: 1.05 }}
          whileTap={{ scale: 0.95 }}
        >
          <FontAwesomeIcon icon={faAngleLeft} />
        </motion.button>

        {startPage > 0 && <span className="mx-1 text-muted">...</span>}

        {pages}

        {endPage < totalPages - 1 && (
          <span className="mx-1 text-muted">...</span>
        )}

        <motion.button
          onClick={() => handlePageChange(currentPage + 1)}
          disabled={currentPage >= totalPages - 1}
          className="btn btn-outline-primary"
          style={{
            minWidth: "40px",
            borderRadius: "6px",
          }}
          whileHover={{ scale: 1.05 }}
          whileTap={{ scale: 0.95 }}
        >
          <FontAwesomeIcon icon={faAngleRight} />
        </motion.button>
        <motion.button
          onClick={() => handlePageChange(totalPages - 1)}
          disabled={currentPage >= totalPages - 1}
          className="btn btn-outline-primary"
          style={{
            minWidth: "40px",
            borderRadius: "6px",
          }}
          whileHover={{ scale: 1.05 }}
          whileTap={{ scale: 0.95 }}
        >
          <FontAwesomeIcon icon={faAnglesRight} />
        </motion.button>

        <div className="d-flex flex-wrap justify-content-center gap-2 mt-2 mt-md-0">
          <span className="text-muted">
            Page {currentPage + 1} of {totalPages}
          </span>
          <span className="text-muted">
            Total Records: {pagination.totalElements}
          </span>
        </div>
      </div>
    );
  };

  // Loading state
  if (loading && !isEditing && !isAdding) {
    return (
      <div className="d-flex justify-content-center align-items-center min-vh-50">
        <motion.div
          animate={{ rotate: 360 }}
          transition={{ repeat: Infinity, duration: 1, ease: "linear" }}
        >
          <FontAwesomeIcon
            icon={faSpinner}
            size="2x"
            className="text-primary"
          />
        </motion.div>
      </div>
    );
  }

  // Error state
  if (error) {
    return (
      <motion.div
        className="alert alert-danger mt-4 mx-3 mx-md-5"
        initial={{ opacity: 0, y: -20 }}
        animate={{ opacity: 1, y: 0 }}
        style={{
          background: "rgba(220, 53, 69, 0.1)",
          border: "none",
          borderRadius: "8px",
          color: "#dc3545",
        }}
      >
        <div className="d-flex justify-content-between align-items-center">
          <span>Error: {error}</span>
          <button
            className="btn btn-sm"
            onClick={() => setError(null)}
            style={{
              background: "rgba(220, 53, 69, 0.2)",
              color: "#dc3545",
              borderRadius: "20px",
            }}
          >
            Dismiss
          </button>
        </div>
      </motion.div>
    );
  }

  return (
    <motion.div
      initial={{ opacity: 0 }}
      animate={{ opacity: 1 }}
      className="container-fluid p-3 p-md-4"
      style={{
        background: "linear-gradient(180deg, #e8ecf1 0%, #dcd9e6 100%)",
        borderRadius: "12px",
        boxShadow: "0 4px 20px rgba(0,0,0,0.05)",
        minHeight: "calc(100vh - 66px)",
      }}
    >
      {!isEditing && !isAdding && (
        <div className="d-flex flex-column flex-md-row align-items-center justify-content-between mb-4 gap-3">
          <div className="position-relative w-100 w-md-auto">
            <FontAwesomeIcon
              icon={faSearch}
              className="position-absolute top-50 translate-middle-y text-muted"
              style={{ left: "10px" }}
            />
            <input
              type="text"
              value={searchTerm}
              onChange={(e) => setSearchTerm(e.target.value)}
              className="form-control ps-5"
              style={{
                borderRadius: "8px",
                border: "1px solid rgba(0,0,0,0.1)",
                padding: "10px 10px",
                background: "rgba(0,0,0,0.02)",
                maxWidth: "400px",
              }}
              placeholder="Search team members..."
            />
          </div>
          <motion.button
            className="btn d-flex align-items-center ms-md-auto"
            style={{
              padding: "10px 20px",
              borderRadius: "8px",
              background: "linear-gradient(90deg, #2b5876 0%, #4e4376 100%)",
              color: "white",
              border: "none",
              whiteSpace: "nowrap",
            }}
            onClick={() => {
              setSelectedMember(null);
              setIsEditing(false);
              setIsAdding(true);
            }}
            whileHover={{ scale: 1.03, opacity: 0.9 }}
            whileTap={{ scale: 0.98 }}
          >
            <FontAwesomeIcon icon={faUserPlus} className="me-2" />
            Add User
          </motion.button>
        </div>
      )}

      <AnimatePresence mode="wait">
        {isEditing && selectedMember ? (
          <motion.div
            key="edit-form"
            initial={{ opacity: 0, y: 20 }}
            animate={{ opacity: 1, y: 0 }}
            exit={{ opacity: 0, y: -20 }}
            transition={{ duration: 0.2 }}
          >
            <EditForm
              member={selectedMember}
              onSave={handleSave}
              onCancel={() => setIsEditing(false)}
            />
          </motion.div>
        ) : isAdding ? (
          <motion.div
            key="add-form"
            initial={{ opacity: 0, y: 20 }}
            animate={{ opacity: 1, y: 0 }}
            exit={{ opacity: 0, y: -20 }}
            transition={{ duration: 0.2 }}
          >
            <AddUserForm
              onAdd={handleAdd}
              onCancel={() => setIsAdding(false)}
            />
          </motion.div>
        ) : (
          <motion.div
            key="table-view"
            initial={{ opacity: 0 }}
            animate={{ opacity: 1 }}
            exit={{ opacity: 0 }}
          >
            <div className="table-responsive rounded-3 overflow-y">
              <table className="table table-hover align-middle mb-0">
                <thead className="sticky-top text-center">
                  <tr
                    style={{
                      background:
                        "linear-gradient(90deg, #2b5876 0%, #4e4376 100%)",
                      color: "white",
                    }}
                  >
                    {headers.map((header) => (
                      <th
                        key={header}
                        className="fw-medium py-3 px-3"
                        style={{ whiteSpace: "nowrap" }}
                      >
                        {header.charAt(0).toUpperCase() +
                          header.slice(1).replace(/([A-Z])/g, " $1")}
                      </th>
                    ))}
                    <th className="fw-medium py-3 px-3 text-center">Actions</th>
                  </tr>
                </thead>

                <tbody>
                  {filteredMembers.length > 0 ? (
                    filteredMembers.map((member, index) => (
                      <motion.tr
                        key={index}
                        initial={{ opacity: 0, y: 10 }}
                        animate={{ opacity: 1, y: 0 }}
                        transition={{ duration: 0.2, delay: index * 0.05 }}
                        className="border-bottom border-light"
                        whileHover={{
                          backgroundColor: "rgba(43, 88, 118, 0.03)",
                        }}
                      >
                        {headers.map((header) => (
                          <td key={header} className="py-3 px-3 text-center">
                            {header === "status" ? (
                              <StatusBadge status={member[header]} />
                            ) : (
                              member[header]
                            )}
                          </td>
                        ))}
                        <td className="py-3 px-3">
                          <div className="d-flex justify-content-center gap-2">
                            <motion.button
                              className="btn btn-sm"
                              onClick={() => handleEdit(member)}
                              style={{
                                background: "rgba(43, 88, 118, 0.1)",
                                color: "#2b5876",
                                borderRadius: "6px",
                                padding: "5px 12px",
                                border: "none",
                                whiteSpace: "nowrap",
                              }}
                              whileHover={{
                                backgroundColor: "rgba(43, 88, 118, 0.2)",
                              }}
                              whileTap={{ scale: 0.95 }}
                            >
                              <FontAwesomeIcon icon={faEdit} className="me-1" />
                              Edit
                            </motion.button>
                            <motion.button
                              className="btn btn-sm"
                              onClick={() => confirmDelete(member)}
                              style={{
                                background: "rgba(220, 53, 69, 0.1)",
                                color: "#dc3545",
                                borderRadius: "6px",
                                padding: "5px 12px",
                                border: "none",
                                whiteSpace: "nowrap",
                              }}
                              whileHover={{
                                backgroundColor: "rgba(220, 53, 69, 0.2)",
                              }}
                              whileTap={{ scale: 0.95 }}
                            >
                              <FontAwesomeIcon
                                icon={faTrash}
                                className="me-1"
                              />
                              Delete
                            </motion.button>
                          </div>
                        </td>
                      </motion.tr>
                    ))
                  ) : (
                    <tr>
                      <td
                        colSpan={headers.length + 1}
                        className="text-center py-5 text-muted"
                      >
                        <div
                          className="position-relative text-center"
                          style={{ width: "300px", margin: "0 auto" }}
                        >
                          <img
                            src={path}
                            alt="team"
                            className="img-fluid rounded"
                            style={{ opacity: 1 }}
                          />
                          <div
                            className="position-absolute top-50 start-50 translate-middle text-dark fw-bold"
                            style={{ fontSize: "1rem", marginLeft: "45px" }}
                          >
                            No team members found
                          </div>
                        </div>
                      </td>
                    </tr>
                  )}
                </tbody>
              </table>
            </div>

            {/* Pagination controls */}
            {pagination.totalPages > 1 && renderPagination()}
          </motion.div>
        )}
      </AnimatePresence>

      {/* Delete confirmation modal */}
      <AnimatePresence>
        {showDeleteModal && (
          <motion.div
            className="modal-backdrop show d-flex justify-content-center align-items-center"
            style={{
              position: "fixed",
              top: 0,
              left: 0,
              right: 0,
              bottom: 0,
              zIndex: 1050,
              backgroundColor: "rgba(255, 255, 255, 0.1)", // frosted background
              backdropFilter: "blur(8px)", // frosted effect,
            }}
            initial={{ opacity: 0 }}
            animate={{ opacity: 1 }}
            exit={{ opacity: 0 }}
          >
            <motion.div
              className="modal-dialog modal-dialog-centered"
              initial={{ y: -50, opacity: 0 }}
              animate={{ y: 0, opacity: 1 }}
              exit={{ y: 50, opacity: 0 }}
              transition={{ type: "spring", damping: 25 }}
            >
              <div
                className="modal-content border-0 shadow-lg"
                style={{ borderRadius: "12px" }}
              >
                <div
                  className="modal-header border-0 pb-0"
                  style={{ padding: "1.5rem" }}
                >
                  <h5 className="modal-title fw-bold">Confirm Deletion</h5>
                  <button
                    type="button"
                    className="btn-close"
                    onClick={() => setShowDeleteModal(false)}
                  />
                </div>
                <div className="modal-body pt-0 px-4">
                  <p>
                    Are you sure you want to delete{" "}
                    <strong>{memberToDelete?.["Employee Name"]}</strong>?
                  </p>
                  <p className="text-muted small">
                    This action cannot be undone.
                  </p>
                </div>
                <div
                  className="modal-footer border-0 pt-0"
                  style={{ padding: "1.5rem" }}
                >
                  <motion.button
                    type="button"
                    className="btn btn-light"
                    onClick={() => setShowDeleteModal(false)}
                    style={{ borderRadius: "8px" }}
                    whileHover={{ backgroundColor: "rgba(0,0,0,0.05)" }}
                    whileTap={{ scale: 0.98 }}
                  >
                    Cancel
                  </motion.button>
                  <motion.button
                    type="button"
                    className="btn btn-danger"
                    onClick={handleDelete}
                    style={{
                      borderRadius: "8px",
                      background:
                        "linear-gradient(90deg, #dc3545 0%, #c82333 100%)",
                      border: "none",
                    }}
                    whileHover={{ opacity: 0.9 }}
                    whileTap={{ scale: 0.98 }}
                  >
                    Delete
                  </motion.button>
                </div>
              </div>
            </motion.div>
          </motion.div>
        )}
      </AnimatePresence>
    </motion.div>
  );
};

Team.propTypes = {
  // Add prop types if this component receives any props
};

export default Team;
