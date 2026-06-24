
import React from 'react';
import { FiUser } from 'react-icons/fi';

const ConnectionEmptyState = ({ onResetSearch }) => {
  return (
    <div className="card shadow-sm">
      <div className="card-body text-center py-5">
        <FiUser size={48} className="text-muted mb-3" />
        <h5>No connections found</h5>
        <p className="text-muted">Try adjusting your search or filters</p>
        <button 
          className="btn btn-primary mt-3"
          onClick={onResetSearch}
        >
          Clear search
        </button>
      </div>
    </div>
  );
};

export default ConnectionEmptyState;