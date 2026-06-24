import { useEffect, useState } from "react";
import { motion } from "framer-motion";
import { faPlus } from "@fortawesome/free-solid-svg-icons";
import ActionButton from "../common/ActionButton";
import ConnectionTabs from "./ConnectionTabs";
import ConnectionSearch from "./ConnectionSearch";
import ConnectionCard from "./ConnectionCard";
import ConnectionEmptyState from "./ConnectionEmptyState";
import AddNewConnectionForm from "./AddNewConnectionForm";
import AddNewPointForm from "./AddNewPointForm";
import AddNewCustomerForm from "./AddNewCustomerForm";
import CustomerDetailsModal from "../manager/GetCustomerDetailsModal";
import EditCustomerForm from "./EditCustomerForm";
import EditPointForm from "./EditPointForm";
import ConfirmationModal from "../common/ConfirmationModal";
import { toast, ToastContainer } from "react-toastify";

const GasAgencyConnections = () => {
  const base_Url = import.meta.env.VITE_API_URL || "";
  const [activeTab, setActiveTab] = useState("distributors");
  const [searchQuery, setSearchQuery] = useState("");
  const [showConnectionForm, setShowConnectionForm] = useState(false);
  const [showCustomerForm, setShowCustomerForm] = useState(false);
  const [showPointForm, setShowPointForm] = useState(false);
  const [showEditForm, setShowEditForm] = useState(false);
  const [showDeleteModal, setShowDeleteModal] = useState(false);
  const [connectionToDelete, setConnectionToDelete] = useState(null);
  const [customerToEdit, setCustomerToEdit] = useState(null);
  const [pointToEdit, setPointToEdit] = useState(null);
  const [showEditPointForm, setShowEditPointForm] = useState(false);
  const [showModal, setShowModal] = useState(false);
  const [showDeletePointModal, setShowDeletePointModal] = useState(false);
  const [pointToDelete, setPointToDelete] = useState(null);
  const [selectedCustomerId, setSelectedCustomerId] = useState(null);
  const [connectionsData, setConnectionsData] = useState({
    suppliers: [
      {
        Id: "SUP-001",
        customer_name: "Bharat Gas Distributors",
        contact: "Ramesh Kumar",
        phone: "+91 9876543210",
        email: "ramesh@bharatgas.com",
        address: "12 Industrial Area, Delhi",
        status: "active",
        since: "2020-05-15",
        cylinders: ["14.2kg", "19kg", "5kg"],
      },
      {
        Id: "SUP-002",
        customer_name: "HP Gas Supply Co.",
        contact: "Priya Sharma",
        phone: "+91 8765432109",
        email: "priya@hpgas.com",
        address: "34 Trade Center, Mumbai",
        status: "active",
        since: "2019-11-22",
        cylinders: ["14.2kg", "5kg", "2kg"],
      },
      {
        Id: "SUP-003",
        customer_name: "Indane Gas Partners",
        contact: "Amit Patel",
        phone: "+91 7654321098",
        email: "amit@indane.com",
        address: "78 Energy Park, Bangalore",
        status: "inactive",
        since: "2021-02-10",
        cylinders: ["19kg", "14.2kg"],
      },
    ],
    distributors: [
      
    ],
    delivery: [
      
    ],
    customers: [
      
    ],
  });

  const [filters, setFilters] = useState({
    status: "",
    type: "",
    dateFrom: "",
    dateTo: "",
  });

  const statusOptions = [
    { value: "Active", label: "Active" },
    { value: "Inactive", label: "Inactive" },
    { value: "pending", label: "Pending" },
  ];

  const typeOptions = [
    { value: "residential", label: "Residential" },
    { value: "commercial", label: "Commercial" },
    { value: "industrial", label: "Industrial" },
  ];

  useEffect(() => {
    async function fetchPointsData() {
      const token = sessionStorage.getItem("token");
      try {
        const response = await fetch(`${base_Url}/masters/getAgencyPointsList`, {
          method: "GET",
          headers: {
            Authorization: `Bearer ${token}`,
            "Content-Type": "application/json",
          },
        });
        const data = await response.json();
        if (response.status === 200) {
          setConnectionsData((prevState) => ({
            ...prevState,
            distributors: data.result,
          }));
        }
      } catch (error) {
        console.error("Error fetching points data:", error);
        toast.error("Failed to fetch points data");
      }
    }
    fetchPointsData();
  }, [showEditPointForm, showDeletePointModal, showPointForm]);

  useEffect(() => {
    async function fetchData() {
      const token = sessionStorage.getItem("token");
      try {
        const [customersResponse, connectionsResponse] = await Promise.all([
          fetch(`${base_Url}/masters/getCustomersList`, {
            method: "GET",
            headers: {
              Authorization: `Bearer ${token}`,
              "Content-Type": "application/json",
            },
          }),
          fetch(`${base_Url}/inventory/connectionList`, {
            method: "POST",
            headers: {
              Authorization: `Bearer ${token}`,
              "Content-Type": "application/json",
            },
            body: JSON.stringify({ page: 0, size: 10 }),
          }),
        ]);
        const customersData = await customersResponse.json();
        const connectionsDatas = await connectionsResponse.json();

        const combinedData = customersData.result.map((customer) => {
          const customerConnections = connectionsDatas.result.filter(
            (connection) => connection.customer_id === customer.Id
          );

          return {
            ...customer,
            connections: customerConnections,
            connectionCount: customerConnections.length,
          };
        });
        setConnectionsData((prevState) => ({
          ...prevState,
          customers: combinedData,
        }));
      } catch (error) {
        console.error("Error fetching data:", error);
      }
    }
    fetchData();
  }, [showEditForm,showDeleteModal, showCustomerForm]); 
  //console.log("new Connections Data:", connectionsData);

  const isActiveFilter = filters.status === "Active";
  const isActiveFilterApplied = filters.status && filters.status.trim() !== "";

  const filteredConnections = connectionsData[activeTab].filter((connection) => {
    const matchesStatus = !isActiveFilterApplied || connection.is_active === isActiveFilter;
    const matchesSearch = !searchQuery || connection.customer_name?.toLowerCase().includes(searchQuery.toLowerCase());
  
    return matchesStatus && matchesSearch;
  });

  const containerVariants = {
    hidden: { opacity: 0 },
    visible: {
      opacity: 1,
      transition: {
        staggerChildren: 0.1,
      },
    },
  };

  const itemVariants = {
    hidden: { y: 20, opacity: 0 },
    visible: {
      y: 0,
      opacity: 1,
      transition: {
        duration: 0.5,
      },
    },
  };

  const handleEditCustomer = (customer) => {
    setCustomerToEdit(customer); // Store the selected connection
    setShowEditForm(true);
    //console.log("Editing:", connection);         // Show the form
  };

  const handleEditPoint = (point) => {
    setPointToEdit(point); // Store the selected connection
    setShowEditPointForm(true);
  }
 const confirmDelete = (connectionId) => {
    // Show confirmation dialog
    setShowDeleteModal(true); 
    setConnectionToDelete(connectionId); // Store the connection ID to delete
  };
const confirmPointDelete = (pointId) => {
    // Show confirmation dialog
    setShowDeletePointModal(true);
    setPointToDelete(pointId); // Store the point ID to delete
  };
  const viewCustomerDetails = (customerId) => {
    setSelectedCustomerId(customerId);
    setShowModal(true);
  };
  const handleDeleteConnection = async (connectionId) => {
    try {
      const token = sessionStorage.getItem("token");
      const response = await fetch(`${base_Url}/masters/deleteCustomersById/${connectionId}`, {
        method: "DELETE",
        headers: {
          "Content-Type": "application/json",
          Authorization: `Bearer ${token}`,
        },
      });
      //console.log("response", response);
      if (response.status === 200) {
        toast.success("Customer deleted successfully!");
      }
      if (response.status === 400 || response.status === 404){
        toast.error("something went wrong while deleting customer");
      }
      if (!response.status === 200)
        throw new Error(`HTTP error! status: ${response}`);
    } catch (error) {
      console.error("Error deleting customer:", error);
      toast.error("something went wrong while deleting customer");
    }
    
    setShowDeleteModal(false); // Close the form after deletion
  };
  const handleDeletePoint = async (pointId) => {
    try {
      const token = sessionStorage.getItem("token");
      const response = await fetch(`${base_Url}/masters/deletePointsById/${pointId}`, {
        method: "DELETE",
        headers: {
          "Content-Type": "application/json",
          Authorization: `Bearer ${token}`,
        },
      });
      //console.log("response", response);
      if (response.status === 200) {
        toast.success("Connection deleted successfully!");
      }
      if (response.status === 400 || response.status === 404){
        toast.error("something went wrong while deleting connection");
      }
      if (!response.status === 200)
        throw new Error(`HTTP error! status: ${response}`);
    } catch (error) {
      console.error("Error deleting connection:", error);
      toast.error("something went wrong while deleting connection");
    }
    setShowDeletePointModal(false); // Close the form after deletion
  };

  const handleAddConnectionSuccess = async (newConnection) => {
    // Handle the new connection data (add to state or API call)
    const token = sessionStorage.getItem("token");
    try {
      const response = await fetch(`${base_Url}/inventory/newConnection`, {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
          Authorization: `Bearer ${token}`,
        },
        body: JSON.stringify(newConnection),
      });
      const data = await response.json();
      if (response.status === 200) {
        toast.success("Connection added successfully!");
      }
      if (response.status === 400){
        toast.error(data.message);
      }
      if (!response.status === 200)
        throw new Error(`HTTP error! status: ${response}`);
    }
    catch (error) {
      console.error("Error adding connection:", error);
      // Handle error (e.g., show a notification)
    }
    setShowConnectionForm(false);
  };

  const handleAddCustomerSuccess = async (newCustomer) => {
    // Handle the new customer data (add to state or API call)
    const token = sessionStorage.getItem("token");
    try {
      const response = await fetch(`${base_Url}/masters/saveAndUpdateCustomers`, {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
          Authorization: `Bearer ${token}`,
        },
        body: JSON.stringify(newCustomer),
      });
      console.log("response", response);
      if (response.status === 201) {
        toast.success("Customer added successfully!");
      }
      if (response.status === 401){
        toast.error("something went wrong while adding customer");
      }
      if (!response.statusCode === 201)
        throw new Error(`HTTP error! status: ${response}`);
        
    } catch (error) {
      console.error("Error adding customer:", error);
      toast.error("something went wrong while adding customer");
      // Handle error (e.g., show a notification)
    }
    //console.log("New customer added:", newCustomer);
    setShowCustomerForm(false);
  };

  const handleEditCustomerSuccess = async (newCustomer) => {
    // Handle the new customer data (add to state or API call)
    
    const token = sessionStorage.getItem("token");
    try {
      const response = await fetch(`${base_Url}/masters/saveAndUpdateCustomers`, {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
          Authorization: `Bearer ${token}`,
        },
        body: JSON.stringify(newCustomer),
      });
      
      if (response.status === 200){
        toast.success("Customer updated successfully!");
      }else if (response.status === 400){
        toast.error("something went wrong while updating customer");
      }else{
        toast.error("something went wrong while updating customer");
      }
    } catch (error) {
      console.error("Error adding customer:", error);
      // Handle error (e.g., show a notification)
    }
    //console.log("New customer added:", newCustomer);
    setShowEditForm(false);
    
  };

  const handleEditPointSuccess = async (newPoint) => {
    // Handle the new customer data (add to state or API call)
    
    const token = sessionStorage.getItem("token");
    try {
      console.log("newPoint", newPoint);
      const response = await fetch(`${base_Url}/masters/createAndUpdatePoints`, {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
          Authorization: `Bearer ${token}`,
        },
        body: JSON.stringify(newPoint),
      });
      
      if (response.status === 200){
        toast.success("Point updated successfully!");
      }else if (response.status === 400){
        toast.error("something went wrong while updating point");
      }else{
        toast.error("something went wrong while updating point");
      }
    } catch (error) {
      console.error("Error adding point:", error);
      // Handle error (e.g., show a notification)
    }
    //console.log("New customer added:", newCustomer);
    setShowEditPointForm(false);
    setShowPointForm(false);
    
  }

  return (
    <div className="container-fluid py-4">
      {/* Forms Modal Overlays */}
      {showConnectionForm && (
        <div className="modal-overlay">
          <AddNewConnectionForm
            onSuccess={handleAddConnectionSuccess}
            onClose={() => setShowConnectionForm(false)}
          />
        </div>
      )}
      {showCustomerForm && (
        <div className="modal-overlay">
          <AddNewCustomerForm
            onSuccess={handleAddCustomerSuccess}
            onClose={() => setShowCustomerForm(false)}
          />
        </div>
      )}
      {showEditForm && (
        <div className="modal-overlay">
          <EditCustomerForm
            onSave={handleEditCustomerSuccess}
            onCancel={() => setShowEditForm(false)}
            customer={customerToEdit}
          />
        </div>
      )}
      {showEditPointForm && (
        <div className="modal-overlay">
          <EditPointForm
            onSave={handleEditPointSuccess}
            onCancel={() => setShowEditPointForm(false)}
            point={pointToEdit}
            />
        </div>
      )}
      {showPointForm && (
        <div className="modal-overlay">
          <AddNewPointForm
            onSave={handleEditPointSuccess}
            onCancel={() => setShowPointForm(false)}
          />
        </div>
      )}
      <motion.div
        initial="hidden"
        animate="visible"
        variants={containerVariants}
        className="row"
      >
        {/* Header */}
        <motion.div variants={itemVariants} className="col-12 mb-4">
          <div className="d-flex flex-column flex-md-row align-items-center justify-content-between mb-4 gap-3">
            <h3 className="h3 mb-0">Gas Agency Connections</h3>
            <div className="d-flex flex-column flex-md-row align-items-center justify-content-between gap-3">
              <ActionButton
                icon={faPlus}
                label="Add New Connection"
                onClick={() => setShowConnectionForm(true)}
                bgColor="linear-gradient(90deg, #2b5876 0%, #4e4376 100%)"
                textColor="white"
              />
              <ActionButton
                icon={faPlus}
                label="Register New Customer"
                onClick={() => setShowCustomerForm(true)}
                bgColor="linear-gradient(90deg, #2b5876 0%, #4e4376 100%)"
                textColor="white"
              />
              <ActionButton
                icon={faPlus}
                label="Add New Point"
                onClick={() => setShowPointForm(true)}
                bgColor="linear-gradient(90deg, #2b5876 0%, #4e4376 100%)"
                textColor="white"
              />
            </div>
          </div>
        </motion.div>
        {/* Tabs and Search */}
        <motion.div variants={itemVariants} className="col-12 mb-4">
          <div className="card shadow-sm">
            <div className="card-body p-2">
              <div className="d-flex flex-column flex-md-row justify-content-between align-items-md-center">
                <ConnectionTabs
                  activeTab={activeTab}
                  setActiveTab={setActiveTab}
                />
                <ConnectionSearch
                  searchQuery={searchQuery}
                  setSearchQuery={setSearchQuery}
                  filters={filters}
                  setFilters={setFilters}
                  statusOptions={statusOptions}
                  typeOptions={typeOptions}
                  //onEdit={handleEditConnection}
                />
              </div>
            </div>
          </div>
        </motion.div>
        {/* Connection Cards */}
        <motion.div variants={itemVariants} className="col-12">
          {filteredConnections.length > 0 ? (
            <div className="row row-cols-1 row-cols-md-2 row-cols-lg-3 row-cols-xl-4 g-4">
              {filteredConnections.map((connection) => (
                <motion.div
                  key={connection.Id}
                  variants={itemVariants}
                  className="col"
                >
                  <ConnectionCard
                    key={connection.Id}
                    connection={connection}
                    type={activeTab}
                    onEdit={handleEditCustomer}
                    onPointEdit={handleEditPoint}
                    onDelete={() => confirmDelete(connection.Id || connection.id)}
                    onPointDelete={() => confirmPointDelete(connection.Id || connection.id)}
                    onViewDetails={() => {
                      viewCustomerDetails(connection.Id);
                    }}
                  />
                </motion.div>
              ))}
            </div>
          ) : (
            <ConnectionEmptyState onResetSearch={() => setSearchQuery("")} />
          )}
        </motion.div>
      </motion.div>
      <ConfirmationModal
        isOpen={showDeleteModal}
        onClose={() => setShowDeleteModal(false)}
        onConfirm={() => handleDeleteConnection(connectionToDelete)}
        title="Confirm Deletion"
        message={`Are you sure you want to delete this customer ${connectionToDelete}?`}
      />
      <ConfirmationModal
        isOpen={showDeletePointModal}
        onClose={() => setShowDeletePointModal(false)}
        onConfirm={() => handleDeletePoint(pointToDelete)}
        title="Confirm Deletion"
        message={`Are you sure you want to delete this point ${pointToDelete}?`}
      />
      <CustomerDetailsModal
        customerId={selectedCustomerId}
        show={showModal}
        onHide={() => setShowModal(false)}
      />
    </div>
  );
};
export default GasAgencyConnections;