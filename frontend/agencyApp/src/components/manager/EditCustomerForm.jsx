import React, { useState, useEffect } from 'react';
import { Modal, Button, Form } from 'react-bootstrap';
import { FiX } from 'react-icons/fi';
import { useAuth } from '../../auth/AuthProvider'; // Adjust the import path as needed

const EditCustomerForm = ({ customer, onSave, onCancel }) => {
  const { user } = useAuth(); // Get current user from auth context
  const [formData, setFormData] = useState({
    customerName: '',
    mobileNumber: '',
    address: '',
    isActive: true,
    id: customer.id 
  });
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');

  // Pre-fill form when customer prop changes
  useEffect(() => {
    if (customer) {
      setFormData({
        customerName: customer.customer_name || '',
        mobileNumber: customer.mobile_number || '',
        address: customer.address || '',
        isActive: customer.is_active !== false, // default to true if undefined
        id: customer.Id || null
      });
    }
  }, [customer]);

  const handleChange = (e) => {
    const { name, value } = e.target;
    setFormData(prev => ({ 
      ...prev, 
      [name]: value,
      lastModifiedBy: user.userId 
    }));
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError('');
    setLoading(true);
    console.log(loading);
    
    try {
      // Prepare the updated customer data
      const updatedCustomer = {
         // include all original customer data
        ...formData,  // overwrite with updated form data
        lastModifiedBy: user.userId,
        //lastModifiedDate: new Date().toISOString()
      };
      
      await onSave(updatedCustomer);
    } catch (err) {
      setError(err.message || 'Failed to update customer');
    } finally {
      setLoading(false);
    }
  };

  return (
    <Modal show={!!customer} onHide={onCancel} size="lg" centered>
      <Modal.Header closeButton>
        <Modal.Title>Edit Customer</Modal.Title>
      </Modal.Header>
      <Modal.Body>
        {error && (
          <div className="alert alert-danger" role="alert">
            {error}
          </div>
        )}
        
        <Form onSubmit={handleSubmit}>
          <Form.Group className="mb-3">
            <Form.Label>Customer Name *</Form.Label>
            <Form.Control
              type="text"
              name="customerName"
              value={formData.customerName}
              onChange={handleChange}
              required
              minLength={2}
              maxLength={100}
            />
            <Form.Text className="text-muted">
              Full name of the customer
            </Form.Text>
          </Form.Group>

          <Form.Group className="mb-3">
            <Form.Label>Mobile Number *</Form.Label>
            <Form.Control
              type="tel"
              name="mobileNumber"
              value={formData.mobileNumber}
              onChange={handleChange}
              required
              pattern="[0-9]{10}"
              title="Please enter a 10-digit mobile number"
            />
            <Form.Text className="text-muted">
              10-digit mobile number without country code
            </Form.Text>
          </Form.Group>

          <Form.Group className="mb-3">
            <Form.Label>Address *</Form.Label>
            <Form.Control
              as="textarea"
              rows={3}
              name="address"
              value={formData.address}
              onChange={handleChange}
              required
              minLength={10}
            />
            <Form.Text className="text-muted">
              Complete address including city and PIN code
            </Form.Text>
          </Form.Group>

          <Form.Group className="mb-3">
            <Form.Check
              type="switch"
              id="isActive"
              name="isActive"
              label="Active Customer"
              checked={formData.isActive}
              onChange={(e) => setFormData(prev => ({
                ...prev,
                isActive: e.target.checked
              }))}
            />
            <Form.Text className="text-muted">
              Active customers can place orders
            </Form.Text>
          </Form.Group>

          <div className="d-flex justify-content-end gap-2">
            <Button 
              variant="secondary" 
              onClick={onCancel}
              disabled={loading}
            >
              Cancel
            </Button>
            <Button 
              variant="primary" 
              type="submit"
              disabled={loading}
            >
              {loading ? 'Saving...' : 'Save Changes'}
            </Button>
          </div>
        </Form>
      </Modal.Body>
    </Modal>
  );
};

export default EditCustomerForm;