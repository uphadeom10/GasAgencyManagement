import { useState, useEffect } from 'react';
import { Modal, Button, Form, Spinner } from 'react-bootstrap';
import { toast } from 'react-toastify';

const AssignOrderModal = ({ 
  show, 
  handleClose, 
  assignmentId,  
  currentUserId,
  refreshOrders 
}) => {
  const base_url = import.meta.env.VITE_API_URL || "";
  const [selectedDeliveryPerson, setSelectedDeliveryPerson] = useState('');
  const [statusId, setStatusId] = useState();
  const [isLoading, setIsLoading] = useState(false);
  const [isFetching, setIsFetching] = useState(false);
  const [deliveryPersons, setDeliveryPersons] = useState([]);
  const [status, setStatus] = useState([]);

  const token = sessionStorage.getItem('token');

  useEffect(() => {
    const fetchData = async () => {
      setIsFetching(true);
      try {
        const method = 'POST';
        const headers = {
          'Authorization': `Bearer ${token}`,
          'Content-Type': 'application/json'
        };
        const body = JSON.stringify({  });

        const [personsResponse, statusResponse] = await Promise.all([
          fetch(`${base_url}/getAllDeliveryBoys`, { headers, method, body }),
          fetch(`${base_url}/masters/getAllStatusList`, { headers, method, body })
        ]);
        
        if (!personsResponse.ok) throw new Error('Failed to fetch delivery persons');
        if (!statusResponse.ok) throw new Error('Failed to fetch status list');

        const [personsData, statusData] = await Promise.all([
          personsResponse.json(),
          statusResponse.json()
        ]);
        
        if (!Array.isArray(personsData.result)) throw new Error('Invalid delivery persons format');
        if (!Array.isArray(statusData.result)) throw new Error('Invalid status format');
        
        setDeliveryPersons(personsData.result);
        setStatus(statusData.result);
      } catch (err) {
        console.error('Fetch error:', err);
        toast.error('Failed to load data. Please try again.');
      } finally {
        setIsFetching(false);
      }
    };
    
    if (show) fetchData();
  }, [show, token, base_url]);

  const handleAssignOrder = async () => {
    if (!selectedDeliveryPerson) {
      toast.warning('Please select a delivery person.');
      return;
    }

    setIsLoading(true);

    try {
      const payload = {
        assignmentId,
        deliveryPersonId: parseInt(selectedDeliveryPerson),
        assignedById: currentUserId,
        statusId
      };
      const response = await fetch(`${base_url}/dailyAssignment/assignOrder`, {
        method: 'POST',
        headers: {
          'Authorization': `Bearer ${token}`,
          'Content-Type': 'application/json'
        },
        body: JSON.stringify(payload)
      });

      if (!response.ok) {
        throw new Error('Failed to assign order');
      }

      toast.success('Order assigned successfully!');
      handleClose();
      //refreshOrders();
    } catch (err) {
      console.error('Assignment error:', err);
      toast.error(err.message || 'Failed to assign order.');
    } finally {
      setIsLoading(false);
    }
  };

  return (
    <Modal 
      show={show} 
      onHide={handleClose} 
      centered 
      backdrop="static"
      className="modal-responsive"
    >
      <Modal.Header closeButton className="px-3 py-2 border-bottom">
        <Modal.Title className="fs-6 fw-bold">Assign Order #{assignmentId}</Modal.Title>
      </Modal.Header>

      <Modal.Body className="px-3 py-3">
        <Form>
          <Form.Group className="mb-3">
            <Form.Label className="fw-semibold small">Delivery Person</Form.Label>
            {isFetching ? (
              <div className="d-flex align-items-center">
                <Spinner animation="border" size="sm" className="me-2" />
                <span>Loading delivery persons...</span>
              </div>
            ) : (
              <Form.Select
                size="sm"
                value={selectedDeliveryPerson}
                onChange={(e) => setSelectedDeliveryPerson(e.target.value)}
                disabled={isLoading || isFetching}
                className="w-100"
              >
                <option value="">Select delivery person</option>
                {deliveryPersons.map((person) => (
                  <option key={person.Id} value={person.Id}>
                    {person.Employee_name} (#{person.Id || 'No vehicle'})
                  </option>
                ))}
              </Form.Select>
            )}
          </Form.Group>

          <Form.Group className="mb-3">
            <Form.Label className="fw-semibold small">Status</Form.Label>
            {isFetching ? (
              <div className="d-flex align-items-center">
                <Spinner animation="border" size="sm" className="me-2" />
                <span>Loading status options...</span>
              </div>
            ) : (
              <Form.Select
                size="sm"
                value={statusId}
                onChange={(e) => setStatusId(parseInt(e.target.value))}
                disabled={isLoading || isFetching}
                className="w-100"
              >
                {status.map((s) => (
                  <option key={s.id} value={s.id}>
                    {s.status}
                  </option>
                ))}
              </Form.Select>
            )}
          </Form.Group>
        </Form>
      </Modal.Body>

      <Modal.Footer className="px-3 py-2 border-top d-flex justify-content-between">
        <Button 
          variant="outline-secondary" 
          size="sm"
          onClick={handleClose} 
          disabled={isLoading}
        >
          Cancel
        </Button>
        <Button 
          variant="primary" 
          size="sm"
          onClick={handleAssignOrder}
          disabled={isLoading || isFetching || !selectedDeliveryPerson}
        >
          {isLoading ? (
            <>
              <Spinner as="span" animation="border" size="sm" className="me-1" />
              Assigning...
            </>
          ) : 'Assign Order'}
        </Button>
      </Modal.Footer>
    </Modal>
  );
};

export default AssignOrderModal;