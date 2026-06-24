import React, { useState, useEffect } from 'react';
import { Modal, Button, Form } from 'react-bootstrap';
import { FiX } from 'react-icons/fi';
import { useAuth } from '../../auth/AuthProvider'; // Adjust the import path as needed

const AddNewPointForm = ({ onSave, onCancel }) => {
  const { user } = useAuth(); // Get current user from auth context
  const [formData, setFormData] = useState({
    pointHolderName: '',
    mobileNumber: '',
    address: '',
    pointName: '',
    isActive: true,
   
  });
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');

  // Pre-fill form when point prop changes
//   useEffect(() => {
//     if (point) {
//       setFormData({
//         pointHolderName: point.point_holder_name || '',
//         mobileNumber: point.mobile_number || '',
//         address: point.address || '',
//         pointName: point.point_name || '',
//         isActive: point.is_active !== false, // default to true if undefined
//         id: point.id || null
//       });
//     }
//   }, [point]);

  const handleChange = (e) => {
    const { name, value } = e.target;
    setFormData(prev => ({ 
      ...prev, 
      [name]: value,
      lastModifiedBy: user.userId,
      createdBy: user.userId
    }));
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError('');
    setLoading(true);
    
    try {
      // Prepare the updated point data
      const updatedPoint = {
        ...formData,  // overwrite with updated form data
        lastModifiedBy: user.userId,
        createdBy: user.userId
      };
      
      await onSave(updatedPoint);
    } catch (err) {
      setError(err.message || 'Failed to update point');
    } finally {
      setLoading(false);
    }
  };

  return (
    <Modal show={true} onHide={onCancel} size="lg" centered>
      <Modal.Header closeButton>
        <Modal.Title>Add New Point</Modal.Title>
      </Modal.Header>
      <Modal.Body>
        {error && (
          <div className="alert alert-danger" role="alert">
            {error}
          </div>
        )}
        
        <Form onSubmit={handleSubmit}>
          <Form.Group className="mb-3">
            <Form.Label>Point Holder Name *</Form.Label>
            <Form.Control
              type="text"
              name="pointHolderName"
              value={formData.pointHolderName}
              onChange={handleChange}
              required
              minLength={2}
              maxLength={100}
            />
            <Form.Text className="text-muted">
              Full name of the point holder
            </Form.Text>
          </Form.Group>

          <Form.Group className="mb-3">
            <Form.Label>Point Name *</Form.Label>
            <Form.Control
              type="text"
              name="pointName"
              value={formData.pointName}
              onChange={handleChange}
              required
              minLength={2}
              maxLength={100}
            />
            <Form.Text className="text-muted">
              Name of the point/shop
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
              label="Active Point"
              checked={formData.isActive}
              onChange={(e) => setFormData(prev => ({
                ...prev,
                isActive: e.target.checked
              }))}
            />
            <Form.Text className="text-muted">
              Active points can process orders
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

export default AddNewPointForm;