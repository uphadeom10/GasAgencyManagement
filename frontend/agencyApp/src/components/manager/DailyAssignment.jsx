import { useState, useEffect } from "react";
import { Container, Tabs, Tab, Button, Toast, Spinner } from "react-bootstrap";
import AssignmentCard from "./AssignmentCard";
import { toast, ToastContainer } from "react-toastify";
import CreateAssignmentModal from "./CreateAssignmentModal";
import AssignOrderModal from "./AssignOrderModal";
import AssignmentFilterBar from "./AssignmentFilterBar";
import ConfirmationModal from "../common/ConfirmationModal";
import AssignmentSearchBar from "./AssignmentSearchBar";
import { useAuth } from "../../auth/AuthProvider";
import { FiInbox } from "react-icons/fi";
import nodata from "../../assets/undraw_empty_4zx0.svg";
const base_url = import.meta.env.VITE_API_URL || "";

const DailyAssignmentPage = () => {
  function toTitleCase(str) {
    return str.replace(
      /\w\S*/g,
      (txt) => txt.charAt(0).toUpperCase() + txt.slice(1).toLowerCase()
    );
  }

  const { user } = useAuth();
  const [activeTab, setActiveTab] = useState("today");
  const [showCreateModal, setShowCreateModal] = useState(false);
  const [assignments, setAssignments] = useState([]);
  const [allAssignments, setAllAssignments] = useState([]);
  const [showAssignModal, setShowAssignModal] = useState(false);
  const [assignmentToClose, setAssignmentToClose] = useState(null);
  const [showDailyClosureModal, setShowDailyClosureModal] = useState(false);
  const [selectedOrderId, setSelectedOrderId] = useState(null);
  const [showDeleteModal, setShowDeleteModal] = useState(false);
  const [productToDelete, setProductToDelete] = useState(null);
  const [searchTermToday, setSearchTermToday] = useState("");
  const [searchTermAll, setSearchTermAll] = useState("");
  const [isLoading, setIsLoading] = useState({
    today: true,
    all: true,
  });
  const [toasts, setToast] = useState({
    show: false,
    message: "",
    variant: "success",
  });
  const [filters, setFilters] = useState({
    startDate: "",
    endDate: "",
    managerId: "",
    deliveryBoyId: "",
  });

  // Mock data - replace with API calls if available
  const managers = [
    { id: 1, name: "John Doe" },
    { id: 2, name: "Jane Smith" },
  ];
  const deliveryBoys = [
    { id: 1, name: "Mike" },
    { id: 2, name: "Alex" },
  ];
  const deliveryPersons = [
    { id: 1, name: "John Doe", vehicleType: "Bike" },
    { id: 2, name: "Jane Smith", vehicleType: "Car" },
  ];
  const fetchTodayAssignments = async () => {
    setIsLoading((prev) => ({ ...prev, today: true }));
    try {
      const token = sessionStorage.getItem("token");

      const today = new Date().toISOString().split("T")[0];
      const response = await fetch(
        `${base_url}/dailyAssignment/assignments/by_date?startDate=${today}&endDate=${today}`,
        {
          headers: {
            Authorization: `Bearer ${token}`,
            Accept: "application/json",
          },
        }
      );

      if (!response.ok) {
        throw new Error(`HTTP error! status: ${response.status}`);
      }
      const data = await response.json();
      if (data.result) {
        setAssignments(data.result);
      } else {
        setAssignments([]);
      }
    } catch (error) {
      console.error("Error fetching today assignments:", error);

      toast.error("Failed to load today's assignments. Please try again.");
    } finally {
      setIsLoading((prev) => ({ ...prev, today: false }));
    }
  };

  const fetchAllAssignments = async () => {
    setIsLoading((prev) => ({ ...prev, all: true }));
    try {
      let url = `${base_url}/dailyAssignment/assignments/by_date`;
      const queryParams = new URLSearchParams();

      if (filters.startDate) queryParams.append("startDate", filters.startDate);
      if (filters.endDate) queryParams.append("endDate", filters.endDate);
      if (filters.managerId) queryParams.append("managerId", filters.managerId);
      if (filters.deliveryBoyId)
        queryParams.append("deliveryBoyId", filters.deliveryBoyId);

      if (queryParams.toString()) {
        url += `?${queryParams.toString()}`;
      }

      const response = await fetch(url, {
        headers: {
          Authorization: `Bearer ${sessionStorage.getItem("token")}`,
          Accept: "application/json",
        },
      });

      if (!response.ok) {
        throw new Error(`HTTP error! status: ${response.status}`);
      }

      const data = await response.json();
      if (data.result) {
        setAllAssignments(data.result);
      } else {
        setAllAssignments([]);
      }
    } catch (error) {
      console.error("Error fetching all assignments:", error);
      //showToast("Failed to load all assignments. Please try again.", "danger");
      toast.error("Failed to load all assignments. Please try again.");
    } finally {
      setIsLoading((prev) => ({ ...prev, all: false }));
    }
  };

  useEffect(() => {
    fetchTodayAssignments();
  }, [showAssignModal, activeTab]);

  useEffect(() => {
    if (activeTab === "all") {
      fetchAllAssignments();
    }
  }, [activeTab, filters, showAssignModal]);

  const showToast = (message, variant = "success") => {
    setToast({ show: true, message, variant });
  };
  const filterAssignmentsById = (assignments, searchTerm) => {
    if (!searchTerm) return assignments;
    return assignments.filter((assignment) =>
      assignment.assignmentId.toString().includes(searchTerm.toLowerCase())||
      assignment.deliveryFirstName.toLowerCase().includes(searchTerm.toLowerCase())
    );
  };
  const filteredTodayAssignments = filterAssignmentsById(
    assignments,
    searchTermToday
  );
  const filteredAllAssignments = filterAssignmentsById(
    allAssignments,
    searchTermAll
  );
  const handleCreateAssignment = async (formData) => {
    try {
      const response = await fetch(`${base_url}/dailyAssignment/create`, {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
          Authorization: `Bearer ${sessionStorage.getItem("token")}`,
        },
        body: JSON.stringify(formData),
      });

      if (!response.ok) {
        throw new Error(`HTTP error! status: ${response.status}`);
      }

      fetchTodayAssignments();
      setShowCreateModal(false);

      //showToast("Assignment created successfully");
      toast.success("Assignment created successfully");
    } catch (error) {
      console.error("Error creating assignment:", error);
      //showToast("Failed to create assignment. Please try again.", "danger");
      toast.error("Failed to create assignment. Please try again.");
    }
  };

  const confirmDelete = (assignment) => {
    setProductToDelete(assignment.assignmentId);
    setShowDeleteModal(true);
  };

  const confirmDailyClosure = (assignment) => {
    setAssignmentToClose(assignment);
    setShowDailyClosureModal(true);
  };
  const handleDelete = async (id) => {
    try {
      const response = await fetch(
        `${base_url}/dailyAssignment/deleteDailyAssignment/${id}`,
        {
          method: "DELETE",
          headers: {
            Authorization: `Bearer ${sessionStorage.getItem("token")}`,
          },
        }
      );

      if (!response.ok) {
        throw new Error(`HTTP error! status: ${response.status}`);
      }

      setAssignments((prev) => prev.filter((a) => a.assignment_id !== id));
      setAllAssignments((prev) => prev.filter((a) => a.assignment_id !== id));
      //showToast("Assignment deleted successfully");
      toast.success("Assignment deleted successfully");
      setProductToDelete(null);
      setShowDeleteModal(false);
      fetchAllAssignments();
      fetchTodayAssignments();
    } catch (error) {
      console.error("Error deleting assignment:", error);
      //showToast("Failed to delete assignment. Please try again.", "danger");
      toast.error("Failed to delete assignment. Please try again.");
    }
  };

  const handleDailyClosure = async (assignment) => {
    const assignmentData = {
      dailyAssignmentId: { id: assignment.assignmentId },
      statusId: { id: assignment.statusId },
      confirmedById: { id: user.userId },
      deliveredById: { id: assignment.deliveryPersonIdOut },
      isCompletedByDeliveryPerson: assignment.statusId === 3 ? true : false,
      lastModifiedBy: user.userId,
    };

    try {
      const response = await fetch(
        `${base_url}/dailyAssignment/dailyClosureByManager`,
        {
          method: "POST",
          headers: {
            Authorization: `Bearer ${sessionStorage.getItem("token")}`,
            "Content-Type": "application/json",
          },
          body: JSON.stringify(assignmentData),
        }
      );
      const responseData = await response.json();
      if (responseData.statusCode === 500) {
        toast.error(toTitleCase(responseData.message));
      } else if (responseData.statusCode === 200) {
        toast.success(toTitleCase(responseData.message));
        fetchTodayAssignments();
        fetchAllAssignments();
      } else if (responseData.statusCode === 400) {
        toast.error(toTitleCase(responseData.message));
      } else {
        toast.error("Failed to complete daily closure. Please try again.");
      }
    } catch (error) {
      console.error("Error completing daily closure:", error);

      toast.error("Failed to complete daily closure. Please try again.");
    }
  };

  const handleFilterChange = (newFilters) => {
    setFilters(newFilters);
  };

  const handleAssignOrder = async (assignment_id) => {
    setSelectedOrderId(assignment_id);
    setShowAssignModal(true);
  };

  return (
    <Container className="py-3">
      <div className="d-flex justify-content-between align-items-center mb-3">
        <h2>Daily Assignment Management</h2>
        <Button variant="primary" onClick={() => setShowCreateModal(true)}>
          + Create New Assignment
        </Button>
      </div>

      <Tabs activeKey={activeTab} onSelect={setActiveTab} className="mb-3">
        <Tab eventKey="today" title="Today's Assignments">
          <AssignmentSearchBar
            onSearch={setSearchTermToday}
            placeholder="Search today's assignments by ID..."
          />
          {isLoading.today ? (
            <div className="text-center py-4">
              <Spinner animation="border" role="status">
                <span className="visually-hidden">Loading...</span>
              </Spinner>
              <p className="mt-2">Loading today's assignments...</p>
            </div>
          ) : (
            <>
              {filteredTodayAssignments.length === 0 ? (
                <div className="d-flex flex-column align-items-center justify-content-center py-4 text-center text-muted">
                  <img
                    src={nodata}
                    alt="No assignments"
                    className="mb-3"
                    style={{ width: "340px", height: "340px", opacity: 0.9 }}
                  />
                  <h2 className="h5 fw-semibold">
                    {searchTermToday
                      ? "No matching assignments found"
                      : "No assignments found for today"}
                  </h2>
                </div>
              ) : (
                filteredTodayAssignments.map((assignment) => (
                  <AssignmentCard
                    key={assignment.assignmentId}
                    assignment={assignment}
                    onDelete={() => confirmDelete(assignment)}
                    onDailyClosure={() => handleDailyClosure(assignment)}
                    onAssignOrder={handleAssignOrder}
                  />
                ))
              )}
            </>
          )}
        </Tab>
        <Tab eventKey="all" title="All Assignments">
          <AssignmentSearchBar
            onSearch={setSearchTermAll}
            placeholder="Search all assignments by ID..."
          />
          <AssignmentFilterBar
            managers={managers}
            deliveryBoys={deliveryBoys}
            onFilterChange={handleFilterChange}
          />
          
          {isLoading.all ? (
            <div className="text-center py-4">
              <Spinner animation="border" role="status">
                <span className="visually-hidden">Loading...</span>
              </Spinner>
              <p className="mt-2">Loading all assignments...</p>
            </div>
          ) : (
            <>
              {filteredAllAssignments.length === 0 ? (
                <div className="alert alert-info">
                  {searchTermAll
                    ? "No matching assignments found"
                    : "No assignments found with the current filters"}
                </div>
              ) : (
                filteredAllAssignments.map((assignment) => (
                  <AssignmentCard
                    key={assignment.assignmentId}
                    assignment={assignment}
                    onDelete={() => confirmDelete(assignment)}
                    onAssignOrder={(assignmentId) =>
                      handleAssignOrder(assignmentId)
                    }
                    onDailyClosure={() => handleDailyClosure(assignment)}
                  />
                ))
              )}
            </>
          )}
        </Tab>
      </Tabs>

      <CreateAssignmentModal
        show={showCreateModal}
        onHide={() => setShowCreateModal(false)}
        managers={managers}
        deliveryBoys={deliveryBoys}
        onSubmit={handleCreateAssignment}
      />

      <AssignOrderModal
        show={showAssignModal}
        handleClose={() => setShowAssignModal(false)}
        assignmentId={selectedOrderId}
        deliveryPersons={deliveryPersons}
        currentUserId={user?.userId} // Get this from your auth context
        //refreshOrders={fetchOrders} // Your function to refresh orders
      />
      {/* <Toast
        show={toast.show}
        onClose={() => setToast({ ...toast, show: false })}
        bg={toast.variant}
        autohide
        delay={5000}
        position="bottom-end"
        style={{ position: "fixed", top: "60px", right: "20px", zIndex: 9999 }}
      >
        <Toast.Header closeButton={false}>
          <strong className="me-auto">
            {toast.variant === "success" ? "Success" : "Error"}
          </strong>
        </Toast.Header>
        <Toast.Body
          className={toast.variant === "success" ? "text-white" : "text-white"}
        >
          {toast.message}
        </Toast.Body>
      </Toast> */}
      <ConfirmationModal
        isOpen={showDeleteModal}
        onClose={() => setShowDeleteModal(false)}
        onConfirm={() => handleDelete(productToDelete)}
        title="Confirm Deletion"
        message={`Are you sure you want to delete #${productToDelete}?`}
      />
    </Container>
  );
};
export default DailyAssignmentPage;
