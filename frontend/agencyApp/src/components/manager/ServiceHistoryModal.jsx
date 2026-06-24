import { useEffect, useState } from 'react';
import { Modal, Table, Button, Spinner, Alert } from 'react-bootstrap';

const ServiceHistoryModal = ({ show, onHide, vehicle }) => {
  const base_url = import.meta.env.VITE_API_URL || '';
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');
  const [serviceData, setServiceData] = useState(null);

  useEffect(() => {
    const fetchServiceHistory = async () => {
      if (!vehicle?.id) return;

      const token = sessionStorage.getItem('token');
      if (!token) {
        setError('Authorization token not found');
        return;
      }

      setLoading(true);
      setError('');
      setServiceData(null);

      try {
        const res = await fetch(
          `${base_url}/vehicle/getServiceDetailsOfVehicle/${vehicle.id}`,
          {
            headers: {
              Accept: '*/*',
              Authorization: `Bearer ${token}`,
            },
          }
        );

        if (!res.ok) {
          const errorData = await res.json();
          throw new Error(errorData.message || 'Failed to fetch service history');
        }

        const data = await res.json();
        setServiceData(data.result || {});
      } catch (err) {
        console.error(err);
        setError(err.message);
      } finally {
        setLoading(false);
      }
    };

    if (show) {
      fetchServiceHistory();
    }
  }, [show, vehicle]);

  const serviceRecords = serviceData?.serviceRecordsListResponseDto || [];

  return (
    <Modal show={show} onHide={onHide} centered size="lg">
      <Modal.Header closeButton>
        <Modal.Title>
          Service History for {serviceData?.vehicleNumber || vehicle?.licensePlate || 'Vehicle'}
        </Modal.Title>
      </Modal.Header>
      <Modal.Body>
        {loading ? (
          <div className="text-center my-4">
            <Spinner animation="border" variant="primary" />
          </div>
        ) : error ? (
          <Alert variant="danger">{error}</Alert>
        ) : serviceRecords.length === 0 ? (
          <p>No service history found for this vehicle.</p>
        ) : (
          <Table striped bordered hover responsive>
            <thead>
              <tr>
                <th>Date</th>
                <th>Details</th>
                <th>Cost</th>
                <th>Serviced By</th>
                <th>Location</th>
                <th>Odometer</th>
              </tr>
            </thead>
            <tbody>
              {serviceRecords.map((record) => (
                <tr key={record.service_id}>
                  <td>{record.serviceDate}</td>
                  <td>{record.description}</td>
                  <td>₹{record.serviceCost}</td>
                  <td>{record.servicedBy}</td>
                  <td>{record.location}</td>
                  <td>{record.odometerReading} km</td>
                </tr>
              ))}
            </tbody>
          </Table>
        )}
      </Modal.Body>
      <Modal.Footer>
        <Button variant="secondary" onClick={onHide}>
          Close
        </Button>
      </Modal.Footer>
    </Modal>
  );
};

export default ServiceHistoryModal;
