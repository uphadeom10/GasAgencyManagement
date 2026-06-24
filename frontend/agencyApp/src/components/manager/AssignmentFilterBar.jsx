import { Form, Row, Col, Button } from 'react-bootstrap';
import { useState } from 'react';

const AssignmentFilterBar = ({ managers, deliveryBoys, onFilterChange }) => {
  const [filters, setFilters] = useState({
    startDate: '',
    endDate: '',
    managerId: '',
    deliveryBoyId: ''
  });

  const handleInputChange = (e) => {
    const { name, value } = e.target;
    setFilters(prev => ({ ...prev, [name]: value }));
  };

  const handleSubmit = (e) => {
    e.preventDefault();
    onFilterChange(filters);
  };

  const handleReset = () => {
    const resetFilters = {
      startDate: '',
      endDate: '',
      managerId: '',
      deliveryBoyId: ''
    };
    setFilters(resetFilters);
    onFilterChange(resetFilters);
  };

  return (
    <Form onSubmit={handleSubmit} className="mb-2 p-2 bg-light rounded">
      <Row>
        <Col md={3}>
          <Form.Group controlId="startDate">
            <Form.Label>Start Date</Form.Label>
            <Form.Control
              type="date"
              name="startDate"
              value={filters.startDate}
              onChange={handleInputChange}
            />
          </Form.Group>
        </Col>
        <Col md={3}>
          <Form.Group controlId="endDate">
            <Form.Label>End Date</Form.Label>
            <Form.Control
              type="date"
              name="endDate"
              value={filters.endDate}
              onChange={handleInputChange}
              min={filters.startDate}
            />
          </Form.Group>
        </Col>
        <Col md={3} className="d-flex align-items-end">
          <Button variant="primary" type="submit" className="me-2">
            Apply Filters
          </Button>
          <Button variant="outline-secondary" onClick={handleReset}>
            Reset
          </Button>
        </Col>
      </Row>
    </Form>
  );
};

export default AssignmentFilterBar;