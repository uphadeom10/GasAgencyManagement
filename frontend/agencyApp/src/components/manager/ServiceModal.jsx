import { useState } from 'react';
import { Modal, Form, Button } from 'react-bootstrap';
import { toast } from 'react-toastify';

const ServiceModal = ({ show, onHide, vehicle, onService }) => {
  const base_url = import.meta.env.VITE_API_URL || '';
  const [loading, setLoading] = useState(false);
  const [serviceData, setServiceData] = useState({
    serviceDate: new Date().toISOString().split('T')[0],
    nextServiceDate: '',
    description: '',
    serviceCost: '',
    servicedBy: '',
    location: '',
    odometerReading: '',
  });

  const handleChange = (e) => {
    const { name, value } = e.target;
    setServiceData((prev) => ({ ...prev, [name]: value }));
  };

  const handleSubmit = async () => {
    if (!serviceData.serviceDate || !vehicle?.id) {
      toast.error('Vehicle and Service Date are required');
      return;
    }

    const token = sessionStorage.getItem('token');
    if (!token) {
      toast.error('Authorization token not found');
      return;
    }

    setLoading(true);

    const payload = {
      
      vehicleId: vehicle.id,
      serviceDate: serviceData.serviceDate,

      description: serviceData.description,
      servicedBy: serviceData.servicedBy,
      location: serviceData.location,
      odometerReading: parseInt(serviceData.odometerReading) || 0,
      serviceCost: parseFloat(serviceData.serviceCost) || 0,
    };

    try {
      const res = await fetch(
        `${base_url}/vehicle/addOrUpdateServicingDetails`,
        {
          method: 'POST',
          headers: {
            'Content-Type': 'application/json',
            Authorization: `Bearer ${token}`,
          },
          body: JSON.stringify(payload),
        }
      );

      if (!res.ok) {
        const errorData = await res.json();
        throw new Error(errorData.message || 'Failed to save service data');
      }

      toast.success('Service record saved successfully');
      //onService(payload);
      onHide();
    } catch (error) {
      console.error(error);
      toast.error(error.message || 'Something went wrong');
    } finally {
      setLoading(false);
    }
  };

  return (
    <Modal show={show} onHide={onHide} centered size="lg">
      <Modal.Header closeButton>
        <Modal.Title>Service Record for {vehicle?.licensePlate || 'Vehicle'}</Modal.Title>
      </Modal.Header>
      <Modal.Body>
        <Form className="grid grid-cols-1 md:grid-cols-2 gap-4">
          <Form.Group>
            <Form.Label>Service Date</Form.Label>
            <Form.Control
              type="date"
              name="serviceDate"
              value={serviceData.serviceDate}
              onChange={handleChange}
              required
            />
          </Form.Group>

          

          <Form.Group>
            <Form.Label>Serviced By</Form.Label>
            <Form.Control
              type="text"
              name="servicedBy"
              value={serviceData.servicedBy}
              onChange={handleChange}
              placeholder="Technician name"
            />
          </Form.Group>

          <Form.Group>
            <Form.Label>Location</Form.Label>
            <Form.Control
              type="text"
              name="location"
              value={serviceData.location}
              onChange={handleChange}
              placeholder="Service location"
            />
          </Form.Group>

          <Form.Group>
            <Form.Label>Odometer Reading (km)</Form.Label>
            <Form.Control
              type="number"
              name="odometerReading"
              value={serviceData.odometerReading}
              onChange={handleChange}
              placeholder="e.g., 10500"
            />
          </Form.Group>

          <Form.Group>
            <Form.Label>Service Cost (₹)</Form.Label>
            <Form.Control
              type="number"
              name="serviceCost"
              value={serviceData.serviceCost}
              onChange={handleChange}
              placeholder="0.00"
            />
          </Form.Group>

          <Form.Group className="md:col-span-2">
            <Form.Label>Service Details</Form.Label>
            <Form.Control
              as="textarea"
              rows={3}
              name="description"
              value={serviceData.description}
              onChange={handleChange}
              placeholder="Describe the service performed"
            />
          </Form.Group>
        </Form>
      </Modal.Body>
      <Modal.Footer className="flex justify-between">
        <Button variant="secondary" onClick={onHide} disabled={loading}>
          Cancel
        </Button>
        <Button variant="primary" onClick={handleSubmit} disabled={loading}>
          {loading ? 'Saving...' : 'Record Service'}
        </Button>
      </Modal.Footer>
    </Modal>
  );
};

export default ServiceModal;
