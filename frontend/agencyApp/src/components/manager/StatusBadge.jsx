import React from 'react';
import { FiCheckCircle, FiAlertCircle, FiClock } from 'react-icons/fi';

const StatusBadge = ({ status }) => {
  const getStatusConfig = () => {
    switch(status) {
      case 'active':
        return {
          className: 'badge bg-success-soft text-success',
          icon: <FiCheckCircle className="me-1" />,
          text: 'Active'
        };
      case 'inactive':
        return {
          className: 'badge bg-danger-soft text-danger',
          icon: <FiAlertCircle className="me-1" />,
          text: 'Inactive'
        };
      case 'on leave':
        return {
          className: 'badge bg-warning-soft text-warning',
          icon: <FiClock className="me-1" />,
          text: 'On Leave'
        };
        case true:
        return {
          className: 'badge bg-success-soft text-success',
          icon: <FiCheckCircle className="me-1" />,
          text: 'Active'
        };
        case false:
        return {
          className: 'badge bg-danger-soft text-danger',
          icon: <FiAlertCircle className="me-1" />,
          text: 'Inactive'
        };
      default:
        return {
          className: 'badge bg-light text-dark',
          icon: null,
          text: status
        };
    }
  };

  const config = getStatusConfig();

  return (
    <span className={config.className}>
      {config.icon}
      {config.text}
    </span>
  );
};

export default StatusBadge;