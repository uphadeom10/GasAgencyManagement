
import { 
  FaTools, 
  FaUser, 
  FaHistory, 
  FaTrash, 
  FaEdit,
  FaGasPump,
  FaTruck
} from 'react-icons/fa';
import { GiWeight } from 'react-icons/gi';

const VehicleCard = ({ 
  vehicle, 
  onEdit, 
  onAssign, 
  onService, 
  onHistory, 
  onDelete 
}) => {
  const isServiceDue = new Date(vehicle.serviceDue) < new Date();
  
  return (
    <div className={`card h-100 ${isServiceDue ? 'border-danger' : ''}`}>
      <div className={`card-header ${isServiceDue ? 'bg-danger text-white' : 'bg-primary text-white'}`}>
        <div className="d-flex justify-content-between align-items-center">
          <h5 className="mb-0"><FaTruck className="me-2" /> {vehicle.licensePlate}</h5>
          {isServiceDue && (
            <span className="badge bg-warning text-dark">Due for Service</span>
          )}
        </div>
      </div>
      <div className="card-body">
        <p className="card-text"><strong>Model:</strong> {vehicle.model}</p>
        <p className="card-text"><strong>Assigned:</strong> {vehicle.assignedUser || 'Unassigned'}</p>
        <p className="card-text"><strong>Last Serviced:</strong> {vehicle.lastServiced}</p>
      </div>
      {/* Fuel and Load Info */}
        <div className="flex gap-3 flex-wrap">
          <div className="flex items-center gap-1 bg-gray-100 text-gray-800 text-xs font-semibold px-3 py-1 rounded-full">
            <FaGasPump className="text-sm" /> {vehicle.fuel}
          </div>
          <div className="flex items-center gap-1 bg-gray-100 text-gray-800 text-xs font-semibold px-3 py-1 rounded-full">
            <GiWeight className="text-sm" /> {vehicle.loadCapacity} kg
          </div>
        </div>
      <div className="card-footer bg-light">
        <div className="d-flex justify-content-between">
          <button 
            className="btn btn-sm btn-outline-secondary" 
            onClick={onService}
            title="Service"
          >
            <FaTools />
          </button>
          <button 
            className="btn btn-sm btn-outline-info" 
            onClick={onAssign}
            title="Assign User"
          >
            <FaUser />
          </button>
          <button 
            className="btn btn-sm btn-outline-dark" 
            onClick={onHistory}
            title="Service History"
          >
            <FaHistory />
          </button>
          <button 
            className="btn btn-sm btn-outline-primary" 
            onClick={onEdit}
            title="Edit"
          >
            <FaEdit />
          </button>
          <button 
            className="btn btn-sm btn-outline-danger" 
            onClick={onDelete}
            title="Delete"
          >
            <FaTrash />
          </button>
        </div>
      </div>
    </div>
  );
};

export default VehicleCard;