import { useState, useEffect } from 'react';
import { Modal, Form, Button, Spinner } from 'react-bootstrap';
import axios from 'axios';

const AssignUserModal = ({ show, onHide, vehicle, onAssign }) => {
  const [selectedUserId, setSelectedUserId] = useState(vehicle?.assignedUserId || '');
  const [users, setUsers] = useState([]);
  const [loading, setLoading] = useState(false);
  const [assigning, setAssigning] = useState(false);

  const token = sessionStorage.getItem('token');
  const base_url = import.meta.env.VITE_API_URL || '';



 const fetchDeliveryBoys = async () => {
    setLoading(true);
    try {
      const response = await fetch(`${base_url}/getAllDeliveryBoys`, {
        method: 'POST',
        headers: {
          'Authorization': `Bearer ${token}`,
          'Content-Type': 'application/json'
        },
        body: JSON.stringify({})
      });
      
      const data = await response.json();
      //console.log('responseeeeeee', data.result);
      setUsers(data.result || []);
    } catch (error) {
      console.error('Error fetching delivery boys:', error);
    } finally {
      setLoading(false);
    }
  };

    useEffect(() => {
    
      fetchDeliveryBoys();
  
  }, [show]);

  const handleAssign = async () => {
    if (!selectedUserId || !vehicle?.id) return;

    setAssigning(true);
    try {
      await axios.get(
        `${base_url}/vehicle/assignVehicleToUser`,
        {
          params: {
            userId: selectedUserId,
            vehicleId: vehicle.id,
          },
          headers: {
            Authorization: `Bearer ${token}`,
          },
        }
      );
      onAssign(); // Notify parent component
      onHide();   // Close modal
    } catch (error) {
      console.error('Error assigning user:', error);
    } finally {
      setAssigning(false);
    }
  };

  return (
    <Modal show={show} onHide={onHide} centered>
      <Modal.Header closeButton>
        <Modal.Title>Assign User to {vehicle?.licensePlate}</Modal.Title>
      </Modal.Header>
      <Modal.Body>
        {loading ? (
          <div className="text-center py-3">
            <Spinner animation="border" size="sm" /> Loading users...
          </div>
        ) : (
          <Form.Group>
            <Form.Label>Select User</Form.Label>
            <Form.Select
              value={selectedUserId}
              onChange={(e) => setSelectedUserId(e.target.value)}
            >
              <option value="">Unassign</option>
              {users.map(user => (
                <option key={user.Id} value={user.Id}>
                  {user.Employee_name}#{user.Id}
                </option>
              ))}
            </Form.Select>
          </Form.Group>
        )}
      </Modal.Body>
      <Modal.Footer>
        <Button variant="secondary" onClick={onHide} disabled={assigning}>
          Cancel
        </Button>
        <Button variant="primary" onClick={handleAssign} disabled={assigning || loading}>
          {assigning ? (
            <>
              <Spinner animation="border" size="sm" className="me-2" />
              Assigning...
            </>
          ) : (
            'Assign'
          )}
        </Button>
      </Modal.Footer>
    </Modal>
  );
};

export default AssignUserModal;
