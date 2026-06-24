import React, { useState, useEffect } from 'react';
import { Modal, Button, Spinner, Table, Badge, Accordion } from 'react-bootstrap';
import { toast } from 'react-toastify';

const CustomerDetailsModal = ({ customerId, show, onHide }) => {
  const [customerData, setCustomerData] = useState(null);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);
  const base_Url = import.meta.env.VITE_API_URL || "";

  useEffect(() => {
    if (show && customerId) {
      fetchCustomerDetails();
    }
  }, [show, customerId]);

  const fetchCustomerDetails = async () => {
    try {
      setLoading(true);
      setError(null);
      
      const token = sessionStorage.getItem('token');
      if (!token) {
        throw new Error('Authentication token not found');
      }

      const response = await fetch(`${base_Url}/inventory/getCustomerDetails/${customerId}`, {
        method: 'GET',
        headers: {
          'Content-Type': 'application/json',
          'Authorization': `Bearer ${token}`
        }
      });

      if (!response.ok) {
        const errorData = await response.json().catch(() => ({}));
        throw new Error(errorData.message || `HTTP error! status: ${response.status}`);
      }

      const data = await response.json();
      setCustomerData(data.result);
      toast.success('Customer details loaded successfully!');

    } catch (err) {
      console.error('Error fetching customer details:', err);
      setError(err.message);
      
      if (err.message.includes('Authentication token not found')) {
        toast.error('Please login to view customer details');
      } else if (err.message.includes('HTTP error! status: 401')) {
        toast.error('Session expired. Please login again');
      } else if (err.message.includes('HTTP error! status: 404')) {
        toast.error('Customer not found');
      } else {
        toast.error(err.message || 'Failed to load customer details');
      }
    } finally {
      setLoading(false);
    }
  };

  const renderConnectionType = (connection) => {
    if (connection.isNewConnection) return <Badge bg="primary">New Connection</Badge>;
    if (connection.isDBC) return <Badge bg="warning">DBC</Badge>;
    if (connection.isInventoryBuy) return <Badge bg="info">Inventory Buy</Badge>;
    return null;
  };

  const renderStatusBadge = (status) => {
    switch (status) {
      case 'IN_PROGRESS':
        return <Badge bg="warning">In Progress</Badge>;
      case 'COMPLETED':
        return <Badge bg="success">Completed</Badge>;
      case 'INITIATED':
        return <Badge bg="primary">Initiated</Badge>;
      default:
        return <Badge bg="secondary">{status}</Badge>;
    }
  };

  return (
    <Modal show={show} onHide={onHide} size="lg" centered scrollable>
      <Modal.Header closeButton>
        <Modal.Title>Customer Details</Modal.Title>
      </Modal.Header>
      <Modal.Body>
        {loading ? (
          <div className="text-center py-4">
            <Spinner animation="border" role="status">
              <span className="visually-hidden">Loading...</span>
            </Spinner>
            <p className="mt-2">Loading customer details...</p>
          </div>
        ) : error ? (
          <div className="alert alert-danger">{error}</div>
        ) : customerData ? (
          <div>
            <div className="mb-4">
              <h5 className="mb-3">Basic Information</h5>
              <div className="row">
                <div className="col-md-6">
                  <p><strong>Customer ID:</strong> {customerData.customer_id}</p>
                  <p><strong>Name:</strong> {customerData.customer_name}</p>
                </div>
                <div className="col-md-6">
                  <p><strong>Mobile:</strong> {customerData.mobile_number}</p>
                </div>
              </div>
            </div>

            <h5 className="mb-3">Connection Details</h5>
            {customerData.newConnectionOfCustomerResponseDto?.length === 0 ? (
              <p>No connection details available</p>
            ) : (
              <Accordion defaultActiveKey="0" flush className="mb-4">
                {customerData.newConnectionOfCustomerResponseDto.map((connection, index) => (
                  <Accordion.Item eventKey={`connection-${index}`} key={`connection-${index}`}>
                    <Accordion.Header>
                      {renderConnectionType(connection)} - Order #{index + 1}
                    </Accordion.Header>
                    <Accordion.Body>
                      <Table striped bordered responsive className="mt-2">
                        <thead>
                          <tr>
                            <th>Product</th>
                            <th>Price</th>
                            <th>Quantity</th>
                            <th>Total</th>
                          </tr>
                        </thead>
                        <tbody>
                          {connection.newConnectionsProductDetails.map((product, pIndex) => (
                            <tr key={`product-${pIndex}`}>
                              <td>{product.product_name} (ID: {product.product_id})</td>
                              <td>₹{product.product_price}</td>
                              <td>{product.product_quantity}</td>
                              <td>₹{product.total_price}</td>
                            </tr>
                          ))}
                        </tbody>
                      </Table>
                    </Accordion.Body>
                  </Accordion.Item>
                ))}
              </Accordion>
            )}

            <h5 className="mb-3">Assignments</h5>
            {customerData.assignments?.length === 0 ? (
              <p>No assignments available</p>
            ) : (
              <Accordion defaultActiveKey="0" flush>
                {customerData.assignments.map((assignment, index) => (
                  <Accordion.Item eventKey={`assignment-${index}`} key={`assignment-${index}`}>
                    <Accordion.Header>
                      Assignment #{assignment.assignment_id} - {renderStatusBadge(assignment.status)}
                    </Accordion.Header>
                    <Accordion.Body>
                      <div className="mb-3">
                        <p><strong>Assigned By:</strong> {assignment.assigned_by}</p>
                        <p><strong>Status:</strong> {renderStatusBadge(assignment.status)}</p>
                        <p><strong>Completed:</strong> {assignment.is_completed ? 'Yes' : 'No'}</p>
                      </div>
                      <h6>Products</h6>
                      <Table striped bordered responsive>
                        <thead>
                          <tr>
                            <th>Product</th>
                            <th>Category</th>
                            <th>Unit Price</th>
                            <th>Quantity</th>
                          </tr>
                        </thead>
                        <tbody>
                          {assignment.assignmentProducts.map((product, pIndex) => (
                            <tr key={`assignment-product-${pIndex}`}>
                              <td>{product.product_name} (ID: {product.product_id})</td>
                              <td>{product.product_category_name}</td>
                              <td>₹{product.unit_price}</td>
                              <td>{product.quantity}</td>
                            </tr>
                          ))}
                        </tbody>
                      </Table>
                    </Accordion.Body>
                  </Accordion.Item>
                ))}
              </Accordion>
            )}
          </div>
        ) : (
          <p>No customer data available</p>
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

export default CustomerDetailsModal;