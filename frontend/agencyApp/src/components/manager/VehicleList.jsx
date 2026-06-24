
import VehicleCard from './VehicleCard';

const VehicleList = ({ 
  vehicles, 
  onEdit, 
  onAssign, 
  onService, 
  onHistory, 
  onDelete 
}) => {
  return (
    <div className="row">
      {vehicles.length === 0 ? (
        <div className="col-12">
          <div className="alert alert-info">No vehicles found</div>
        </div>
      ) : (
        vehicles.map(vehicle => (
          <div key={vehicle.id} className="col-md-6 col-lg-4 mb-3">
            <VehicleCard
              vehicle={vehicle}
              onEdit={() => onEdit(vehicle)}
              onAssign={() => onAssign(vehicle)}
              onService={() => onService(vehicle)}
              onHistory={() => onHistory(vehicle)}
              onDelete={() => onDelete(vehicle)}
            />
          </div>
        ))
      )}
    </div>
  );
};

export default VehicleList;