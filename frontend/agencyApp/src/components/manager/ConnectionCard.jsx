import { FiUser, FiPhone, FiMail, FiMapPin, FiEdit2, FiTrash2, FiClock, FiEye } from 'react-icons/fi';
import StatusBadge from './StatusBadge';
const ConnectionCard = ({ connection, type, onEdit, onDelete, onViewDetails, onPointEdit, onPointDelete }) => {
  
  const handleCustomerClick = (customerId) => {
    setSelectedCustomerId(customerId);
    setShowModal(true);
  };
  const renderSupplierCard = () => (
    <div className="card h-100">
      <div className="card-body">
        <div className="d-flex justify-content-between align-items-start mb-3">
          <h5 className="card-title mb-0">{connection.name}</h5>
          <StatusBadge status={connection.status} />
        </div>
        <p className="text-muted mb-3">
          <FiUser className="me-2" />
          {connection.contact}
        </p>
        
        <div className="mb-3">
          <p className="mb-1">
            <FiPhone className="me-2" />
            {connection.phone}
          </p>
          <p className="mb-1">
            <FiMail className="me-2" />
            {connection.email}
          </p>
          <p className="mb-0">
            <FiMapPin className="me-2" />
            {connection.address}
          </p>
        </div>
        
        <div className="mb-3">
          <h6 className="small text-muted mb-2">Cylinder Types</h6>
          <div className="d-flex flex-wrap gap-2">
            {connection.cylinders.map((type, index) => (
              <span key={index} className="badge bg-primary-soft text-primary">
                {type}
              </span>
            ))}
          </div>
        </div>
        
        <div className="d-flex justify-content-between align-items-center">
          <small className="text-muted">
            <FiClock className="me-1" />
            Since {connection.since}
          </small>
          <div className="d-flex gap-2">
            <button className="btn btn-sm btn-outline-primary" onClick={() => onEdit(connection)}>
              <FiEdit2 size={14} />
            </button>
            <button className="btn btn-sm btn-outline-danger" onClick={() => onDelete(connection.id)}>
              <FiTrash2 size={14} />
            </button>
          </div>
        </div>
      </div>
    </div>
  );

const renderDistributorCard = () => (
  <div className="card h-100">
    <div className="card-body">
      <div className="d-flex justify-content-between align-items-start mb-3">
        <h5 className="card-title mb-0">{connection.point_name}</h5>
        <StatusBadge status={connection.is_active ? "active" : "inactive"} />
      </div>

      <div className="mb-3">
        <p className="mb-1">
          <FiUser className="me-2" />
          {connection.point_holder_name}
        </p>
        <p className="mb-1">
          <FiPhone className="me-2" />
          {connection.mobile_number}
        </p>
        <p className="mb-0">
          <FiMapPin className="me-2" />
          {connection.address}
        </p>
      </div>

      <div className="d-flex justify-content-between align-items-center">
        <small className="text-muted">
          <FiClock className="me-1" />
          ID: {connection.id}
        </small>
        <div className="d-flex gap-2">
          <button className="btn btn-sm btn-outline-primary" onClick={() => onPointEdit(connection)}>
            <FiEdit2 size={14} />
          </button>
          <button className="btn btn-sm btn-outline-danger" onClick={() => onPointDelete(connection.id)}>
            <FiTrash2 size={14} />
          </button>
        </div>
      </div>
    </div>
  </div>
);


  const renderCustomerCard = () => (
    <div className="card h-100">
      <div className="card-body">
        <div className="d-flex justify-content-between align-items-start mb-3">
          <h5 className="card-title mb-0">{connection.customer_name}</h5>
          <StatusBadge status={connection.is_active} />
        </div>
        
        <div className="mb-3">
          <p className="mb-1">
            <FiPhone className="me-2" />
            {connection.mobile_number}
          </p>
          <p className="mb-1">
            <FiMapPin className="me-2" />
            {connection.address}
          </p>
        </div>
        
        <div className="mb-3">
          <div className="d-flex justify-content-between mb-1">
            <span className="text-muted">Type</span>
            <span className="fw-semibold">{connection.type}</span>
          </div>

          <div className="d-flex justify-content-between">
            <span className="text-muted">Customer ID</span>
            <span className="fw-semibold">{connection.Id}</span>
          </div>
        </div>
        
        <div className="d-flex justify-content-between align-items-center">
          <small className="text-muted">
            <FiClock className="me-1" />
            Since {connection.since}
          </small>
          <div className="d-flex gap-2">
            <button className="btn btn-sm btn-outline-primary" onClick={() => onViewDetails(connection.Id)}>
              <FiEye size={14} />
            </button>
            <button className="btn btn-sm btn-outline-primary" onClick={() => onEdit(connection)}>
              <FiEdit2 size={14} />
            </button>
            <button className="btn btn-sm btn-outline-danger" onClick={() => onDelete(connection.Id)}>
              <FiTrash2 size={14} />
            </button>
          </div>
        </div>
      </div>
    </div>
  );

  switch(type) {
    case 'suppliers':
      return renderSupplierCard();
    case 'distributors':
      return renderDistributorCard();
    case 'customers':
      return renderCustomerCard();
    default:
      return null;
  }
};

export default ConnectionCard;