import React, { useState } from 'react';
import { FiSearch, FiFilter, FiX } from 'react-icons/fi';
import { Button, Dropdown, Form, Modal } from 'react-bootstrap';

const ConnectionSearch = ({ 
  searchQuery, 
  setSearchQuery, 
  filters, 
  setFilters,
  statusOptions = [],
  typeOptions = []
}) => {
  const [showFilters, setShowFilters] = useState(false);
  const [localFilters, setLocalFilters] = useState(filters);

  const handleFilterChange = (key, value) => {
    setLocalFilters(prev => ({
      ...prev,
      [key]: value
    }));
  };

  const applyFilters = () => {
    setFilters(localFilters);
    setShowFilters(false);
  };
  

  const resetFilters = () => {
    const resetFilters = {
      status: '',
      type: '',
      dateFrom: '',
      dateTo: ''
    };
    setLocalFilters(resetFilters);
    setFilters(resetFilters);
    setShowFilters(false);
  };

  const hasActiveFilters = Object.values(filters).some(val => val !== '');

  return (
    <div className="d-flex gap-2 align-items-center">
      {/* Search Input */}
      <div className="input-group" style={{ width: '250px' }}>
        <span className="input-group-text">
          <FiSearch />
        </span>
        <input 
          type="text" 
          className="form-control" 
          placeholder="Search connections..." 
          value={searchQuery}
          onChange={(e) => setSearchQuery(e.target.value)}
        />
      </div>

      {/* Filter Button with Badge */}
      <Button 
        variant={hasActiveFilters ? "primary" : "outline-secondary"} 
        onClick={() => setShowFilters(true)}
        className="d-flex align-items-center"
      >
        <FiFilter className="me-1" />
        Filters
        {hasActiveFilters && (
          <span className="ms-1 badge bg-light text-dark">
            {Object.values(filters).filter(val => val !== '').length}
          </span>
        )}
      </Button>

      {/* Active Filters Display */}
      {hasActiveFilters && (
        <div className="d-flex gap-2 ms-2">
          {Object.entries(filters).map(([key, value]) => {
            if (!value) return null;
            
            let displayValue = value;
            if (key === 'status') displayValue = statusOptions.find(opt => opt.value === value)?.label || value;
            if (key === 'type') displayValue = typeOptions.find(opt => opt.value === value)?.label || value;

            return (
              <span key={key} className="badge bg-light text-dark d-flex align-items-center">
                {`${key}: ${displayValue}`}
                <button 
                  className="ms-1 btn btn-sm p-0 border-0"
                  onClick={() => {
                    handleFilterChange(key, '');
                    setFilters(prev => ({ ...prev, [key]: '' }));
                  }}
                >
                  <FiX size={14} />
                </button>
              </span>
            );
          })}
        </div>
      )}

      {/* Filters Modal */}
      <Modal show={showFilters} onHide={() => setShowFilters(false)}>
        <Modal.Header closeButton>
          <Modal.Title>Filter Connections</Modal.Title>
        </Modal.Header>
        <Modal.Body>
          <Form>
            {/* Status Filter */}
            <Form.Group className="mb-3">
              <Form.Label>Status</Form.Label>
              <Form.Select
                value={localFilters.status}
                onChange={(e) => handleFilterChange('status', e.target.value)}
              >
                <option value="">All Statuses</option>
                {statusOptions.map(option => (
                  <option key={option.value} value={option.value}>
                    {option.label}
                  </option>
                ))}
              </Form.Select>
            </Form.Group>

            {/* Type Filter */}
            <Form.Group className="mb-3">
              <Form.Label>Type</Form.Label>
              <Form.Select
                value={localFilters.type}
                onChange={(e) => handleFilterChange('type', e.target.value)}
              >
                <option value="">All Types</option>
                {typeOptions.map(option => (
                  <option key={option.value} value={option.value}>
                    {option.label}
                  </option>
                ))}
              </Form.Select>
            </Form.Group>

            {/* Date Range Filter */}

          </Form>
        </Modal.Body>
        <Modal.Footer>
          <Button variant="outline-secondary" onClick={resetFilters}>
            Reset
          </Button>
          <Button variant="primary" onClick={applyFilters}>
            Apply Filters
          </Button>
        </Modal.Footer>
      </Modal>
    </div>
  );
};

export default ConnectionSearch;