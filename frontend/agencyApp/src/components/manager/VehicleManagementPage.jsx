import { useEffect, useRef, useState } from 'react';
import { ToastContainer, toast } from 'react-toastify';
import 'react-toastify/dist/ReactToastify.css';
import VehicleToolbar from './VehicleToolbar';
import VehicleTabs from './VehicleTabs';
import VehicleList from './VehicleList';
import AddOrEditVehicleModal from './AddOrEditVehicleModal';
import AssignUserModal from './AssignUserModal';
import ServiceModal from './ServiceModal';
import ServiceHistoryModal from './ServiceHistoryModal';
import DeleteConfirmationModal from './DeleteConfirmationModal';
import { useAuth } from "../../auth/AuthProvider"

const base_url = import.meta.env.VITE_API_URL || '';

const VehicleManagementPage = () => {
  const token = sessionStorage.getItem('token');
  const base_url = import.meta.env.VITE_API_URL || '';
  const { user } = useAuth(); // Ensure this is available in your component scope
  const [activeTab, setActiveTab] = useState('all');
  const [searchQuery, setSearchQuery] = useState('');
  const [vehicles, setVehicles] = useState([]);
  const [selectedVehicle, setSelectedVehicle] = useState(null);
  const [showAddModal, setShowAddModal] = useState(false);
  const [showAssignModal, setShowAssignModal] = useState(false);
  const [showServiceModal, setShowServiceModal] = useState(false);
  const [showHistoryModal, setShowHistoryModal] = useState(false);
  const [showDeleteModal, setShowDeleteModal] = useState(false);
  const [loading, setLoading] = useState(false);

  const notifiedRef = useRef(false);

  const fetchAllVehicles = async () => {
    setLoading(true);
    try {
      const token = sessionStorage.getItem('token');
      const response = await fetch(`${base_url}/vehicle/getAllVehiclesList`, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
          Authorization: `Bearer ${token}`,
        },
        body: JSON.stringify({ page: 0, size: 100 }),
      });

      const data = await response.json();

      if (data.statusCode === 200) {
        const transformed = data.result.map((v) => ({
          id: v.vehicle_id,
          licensePlate: v.vehicle_number,
          model: v.vehicle_model,
          type: v.vehicle_type,
          fuel: v.fuel_type,
          loadCapacity: v.load_capacity,
          lastServiced: v.last_service_date,
          serviceDue: v.next_service_due,
          assignedUser: v.users_name || 'Not Assigned',
          userRole: v.user_role || 'N/A',
        }));

        setVehicles(transformed);
        if (!notifiedRef.current) {
          toast.success('🚗 Vehicles loaded successfully');
          notifiedRef.current = true;
        }
      } else {
        toast.error(`❌ Failed to fetch vehicles: ${data.message}`);
      }
    } catch (err) {
      toast.error('⚠️ Something went wrong while fetching vehicle data.');
      console.error(err);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchAllVehicles();
  }, [showAddModal, showAssignModal, showDeleteModal]);

  useEffect(() => {
    const checkServiceDue = () => {
      const today = new Date().toISOString().split('T')[0];
      const dueTodayVehicles = vehicles.filter(
        (v) => v.serviceDue && v.serviceDue <= today
      );

      if (dueTodayVehicles.length > 0) {
        toast.warning(
          `⚠️ ${dueTodayVehicles.length} vehicle(s) are due or overdue for service.`,
          {
            toastId: 'service-due-warning',
            onClick: () => setActiveTab('due'),
          }
        );
      }
    };

    checkServiceDue();
  }, [vehicles]);

  const filteredVehicles = vehicles.filter((vehicle) => {
    const matchesSearch =
      vehicle.licensePlate?.toLowerCase().includes(searchQuery.toLowerCase()) ||
      vehicle.model?.toLowerCase().includes(searchQuery.toLowerCase());

    if (activeTab === 'all') return matchesSearch;
    if (activeTab === 'due')
      return matchesSearch && vehicle.serviceDue < new Date().toISOString().split('T')[0];

    return matchesSearch;
  });

const handleAddOrEditVehicle = async (vehicleData) => {
  console.log('vehicleData', vehicleData);
  setLoading(true);
 // console.log('vehicleData', vehicleData);
  
  

  const payload = {
    id: vehicleData.id , // Use 0 or omit if creating a new vehicle
    vehicleNumber: vehicleData.licensePlate,
    vehicleType: vehicleData.type,
    vehicleModel: vehicleData.model,
    fuelType: vehicleData.fuel,
    loadCapacity: parseFloat(vehicleData.loadCapacity),
    lastServiceDate: vehicleData.lastServiced || null,
    nextServiceDue: vehicleData.serviceDue || null,
    assignedTo: vehicleData.assignedTo ? { id: vehicleData.assignedTo } : undefined, // Optional
    createdBy: user.userId,
    lastModifiedBy: user.userId,
  };

 // Remove undefined/null keys (optional)
  Object.keys(payload).forEach(
    key => (payload[key] === undefined || payload[key] === null) && delete payload[key]
  );
  console.log('payload', payload);
  try {
    const response = await fetch(`${base_url}/vehicle/updateAndRegister`, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
        Authorization: `Bearer ${token}`,
      },
      body: JSON.stringify(payload),
    });

    if (!response.ok) {
      const errorData = await response.json();
      throw new Error(errorData.message || 'Failed to save vehicle');
    }

    const result = await response.json();
    console.log('Vehicle saved:', result);
    toast.success('Vehicle saved successfully!');
    setLoading(false);
    setShowAddModal(false);
    // Optionally refresh vehicle list here
  } catch (error) {
    console.error('Error saving vehicle:', error);
    toast.error(error.message || 'Something went wrong');
  }finally {
    setLoading(false);
  }
};


  const handleDeleteVehicle = async (id) => {
    setLoading(true);
    try {
      const response = await fetch(`${base_url}/vehicle/deleteVehicleById/${id}`, {
        method: 'DELETE',
        headers: {
          'Authorization': `Bearer ${token}`,
        },
      });
      if (!response.ok) {
        throw new Error('Failed to delete vehicle');
      }
      toast.success('Vehicle deleted successfully!');
      setLoading(false);
      setShowDeleteModal(false);
      // Optionally refresh vehicle list here
      fetchAllVehicles();
      
    } catch (error) {
      console.error('Error deleting vehicle:', error);
      toast.error(error.message || 'Something went wrong');

      
    }

  };

  return (
    <div className="container-fluid p-3">
      <h1 className="mb-4">Vehicle Management</h1>

      <VehicleTabs activeTab={activeTab} setActiveTab={setActiveTab} />
      <VehicleToolbar
        searchQuery={searchQuery}
        setSearchQuery={setSearchQuery}
        onRefresh={() => fetchAllVehicles()}
        onAddVehicle={() => {
          setSelectedVehicle(null);
          setShowAddModal(true);
        }}
      />

      <VehicleList
        loading={loading}
        vehicles={filteredVehicles}
        onEdit={(vehicle) => {
          setSelectedVehicle(vehicle);
          setShowAddModal(true);
        }}
        onAssign={(vehicle) => {
          setSelectedVehicle(vehicle);
          setShowAssignModal(true);
        }}
        onService={(vehicle) => {
          
          setSelectedVehicle(vehicle);
          setShowServiceModal(true);
        }}
        onHistory={(vehicle) => {
          setSelectedVehicle(vehicle);
          setShowHistoryModal(true);
        }}
        onDelete={(vehicle) => {
          setSelectedVehicle(vehicle);
          setShowDeleteModal(true);
        }}
      />

      <AddOrEditVehicleModal
        show={showAddModal}
        onHide={() => setShowAddModal(false)}
        vehicle={selectedVehicle}
        onSubmit={handleAddOrEditVehicle}
        isLoading={loading}
      />

      <AssignUserModal
        show={showAssignModal}
        onHide={() => setShowAssignModal(false)}
        vehicle={selectedVehicle}
        onAssign={(user) => {
          setVehicles(
            vehicles.map((v) =>
              v.id === selectedVehicle.id ? { ...v, assignedUser: user } : v
            )
          );
          setShowAssignModal(false);
        }}
      />

      <ServiceModal
        show={showServiceModal}
        onHide={() => setShowServiceModal(false)}
        vehicle={selectedVehicle}
        // onService={(serviceData) => {
        //   setVehicles(
        //     vehicles.map((v) =>
        //       v.id === selectedVehicle.id
        //         ? {
        //             ...v,
        //             lastServiced: serviceData.date,
        //             serviceDue: serviceData.nextServiceDate,
        //           }
        //         : v
        //     )
        //   );
        //   setShowServiceModal(false);
        // }}
      />

      <ServiceHistoryModal
        show={showHistoryModal}
        onHide={() => setShowHistoryModal(false)}
        vehicle={selectedVehicle}
      />

      <DeleteConfirmationModal
        show={showDeleteModal}
        isLoading={loading}
        onHide={() => setShowDeleteModal(false)}
        onConfirm={() => handleDeleteVehicle(selectedVehicle.id)}
        itemName={`vehicle ${selectedVehicle?.licensePlate}`}
      />

      {/* <ToastContainer position="bottom-right" autoClose={3000} /> */}
    </div>
  );
};

export default VehicleManagementPage;
