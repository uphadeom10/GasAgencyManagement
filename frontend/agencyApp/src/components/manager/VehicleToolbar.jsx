
import { FiSearch, FiFilter, FiRefreshCw } from 'react-icons/fi';

const VehicleToolbar = ({ searchQuery, setSearchQuery, onAddVehicle, onRefresh }) => {
  return (
    <div className="row mb-3">
      <div className="col-md-8 col-12 mb-2 mb-md-0">
        <div className="input-group">
          <span className="input-group-text">
            <FiSearch />
          </span>
          <input
            type="text"
            className="form-control"
            placeholder="Search vehicles..."
            value={searchQuery}
            onChange={(e) => setSearchQuery(e.target.value)}
          />
          <button className="btn btn-outline-secondary" type="button">
            <FiFilter className="me-1" /> Filter
          </button>
          <button className="btn btn-outline-secondary" type="button" onClick={onRefresh}>
            <FiRefreshCw />
          </button>
        </div>
      </div>
      <div className="col-md-4 col-12 text-md-end">
        <button className="btn btn-primary w-100 w-md-auto" onClick={onAddVehicle}>
          Register New Vehicle
        </button>
      </div>
    </div>
  );
};

export default VehicleToolbar;