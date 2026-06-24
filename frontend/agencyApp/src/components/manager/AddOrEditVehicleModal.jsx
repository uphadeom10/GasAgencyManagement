import { useState, useEffect } from 'react';
import { Modal, Form, Button } from 'react-bootstrap';
import { toast } from 'react-toastify';
// Replace with your actual auth hook
import { useAuth } from '../../auth/AuthProvider';

const defaultFormData = {
  licensePlate: '',
  model: '',
  type: '',
  fuel: '',
  loadCapacity: '',
  lastServiced: '',
  serviceDue: '',
};

const AddOrEditVehicleModal = ({ show, onHide, vehicle, onSubmit, loading }) => {
  const { user } = useAuth(); // Assumes user.id is available
  const [formData, setFormData] = useState(defaultFormData);

  useEffect(() => {
    if (vehicle) {
      setFormData({
        licensePlate: vehicle.licensePlate || '',
        model: vehicle.model || '',
        type: vehicle.type || '',
        fuel: vehicle.fuel || '',
        loadCapacity: vehicle.loadCapacity || '',
        lastServiced: vehicle.lastServiced || '',
        serviceDue: vehicle.serviceDue || '',
      });
    } else {
      setFormData(defaultFormData);
    }
  }, [vehicle]);

  const handleChange = (e) => {
    const { name, value } = e.target;
    setFormData(prev => ({ ...prev, [name]: value }));
  };

  const handleSubmit = async () => {
    if (!formData.licensePlate || !formData.model) {
      toast.error('License plate and model are required');
      return;
    }
    if (!user?.userId) {
      toast.error('User not authenticated');
      return;
    }

    // Prepare payload as per API
    const payload = {
      ...formData,
      id: vehicle?.id, // Only include if editing
      loadCapacity: formData.loadCapacity ? parseFloat(formData.loadCapacity) : undefined,
      createdBy: user.userId,
      lastModifiedBy: user.userId,
    };

    // Remove empty fields (API may not accept undefined/null)
    Object.keys(payload).forEach(
      key => (payload[key] === '' || payload[key] === undefined) && delete payload[key]
    );

    try {
      await onSubmit(payload);
      onHide();
    } catch (err) {
      console.error(err);
      toast.error('Failed to save vehicle');
    }
  };

  return (
    <Modal show={show} onHide={onHide} centered>
      <Modal.Header closeButton>
        <Modal.Title>{vehicle ? 'Edit Vehicle' : 'Register New Vehicle'}</Modal.Title>
      </Modal.Header>
      <Modal.Body>
        <Form>
          <div className="space-y-4">
            {/* License Plate - full width */}
            <div>
              <Form.Label className="block text-sm font-medium mb-1">License Plate</Form.Label>
              <Form.Control
                type="text"
                name="licensePlate"
                value={formData.licensePlate}
                onChange={handleChange}
                className="w-full"
                required
              />
            </div>

            {/* Grid for two-column layout on md+ */}
            <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
              <div>
                <Form.Label className="block text-sm font-medium mb-1">Vehicle Type</Form.Label>
                <Form.Control
                  type="text"
                  name="type"
                  value={formData.type}
                  onChange={handleChange}
                  className="w-full"
                />
              </div>

              <div>
                <Form.Label className="block text-sm font-medium mb-1">Vehicle Model</Form.Label>
                <Form.Control
                  type="text"
                  name="model"
                  value={formData.model}
                  onChange={handleChange}
                  className="w-full"
                  required
                />
              </div>

              <div>
                <Form.Label className="block text-sm font-medium mb-1">Fuel Type</Form.Label>
                <Form.Select
                  name="fuel"
                  value={formData.fuel}
                  onChange={handleChange}
                  className="w-full"
                >
                  <option value="">Select fuel type</option>
                  <option value="PETROL">Petrol</option>
                  <option value="DIESEL">Diesel</option>
                  <option value="ELECTRIC">Electric</option>
                  <option value="CNG">CNG</option>
                </Form.Select>
              </div>

              <div>
                <Form.Label className="block text-sm font-medium mb-1">Load Capacity (kg)</Form.Label>
                <Form.Control
                  type="number"
                  step="1"
                  name="loadCapacity"
                  value={formData.loadCapacity}
                  onChange={handleChange}
                  className="w-full"
                />
              </div>

              <div>
                <Form.Label className="block text-sm font-medium mb-1">Last Service Date</Form.Label>
                <Form.Control
                  type="date"
                  name="lastServiced"
                  value={formData.lastServiced}
                  onChange={handleChange}
                  className="w-full"
                />
              </div>

              <div>
                <Form.Label className="block text-sm font-medium mb-1">Next Service Due</Form.Label>
                <Form.Control
                  type="date"
                  name="serviceDue"
                  value={formData.serviceDue}
                  onChange={handleChange}
                  className="w-full"
                />
              </div>
            </div>
          </div>
        </Form>

      </Modal.Body>
      <Modal.Footer>
        <Button variant="secondary" onClick={onHide}>
          Cancel
        </Button>
        <Button
          variant="primary"
          onClick={handleSubmit}
          disabled={loading}
          className="flex items-center gap-2"
        >
          {loading ? (
            <>
              <svg
                className="animate-spin h-4 w-4 text-white"
                xmlns="http://www.w3.org/2000/svg"
                fill="none"
                viewBox="0 0 24 24"
              >
                <circle
                  className="opacity-25"
                  cx="12"
                  cy="12"
                  r="10"
                  stroke="currentColor"
                  strokeWidth="4"
                ></circle>
                <path
                  className="opacity-75"
                  fill="currentColor"
                  d="M4 12a8 8 0 018-8v8H4z"
                ></path>
              </svg>
              Saving...
            </>
          ) : vehicle ? 'Update' : 'Register'}
        </Button>

      </Modal.Footer>
    </Modal>
  );
};

export default AddOrEditVehicleModal;