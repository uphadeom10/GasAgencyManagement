
import React from 'react';
import { FiDatabase, FiTruck, FiHome } from 'react-icons/fi';

const ConnectionTabs = ({ activeTab, setActiveTab }) => {
  return (
    <ul className="nav nav-pills mb-3 mb-md-0">
      <li className="nav-item">
        <button 
          className={`nav-link ${activeTab === 'suppliers' ? 'active' : ''}`}
          onClick={() => setActiveTab('suppliers')}
        >
          <FiDatabase className="me-1" /> Suppliers
        </button>
      </li>
      <li className="nav-item">
        <button 
          className={`nav-link ${activeTab === 'distributors' ? 'active' : ''}`}
          onClick={() => setActiveTab('distributors')}
        >
          <FiTruck className="me-1" /> AgencyPoints
        </button>
      </li>
      <li className="nav-item">
        <button 
          className={`nav-link ${activeTab === 'customers' ? 'active' : ''}`}
          onClick={() => setActiveTab('customers')}
        >
          <FiHome className="me-1" /> Customers
        </button>
      </li>
    </ul>
  );
};

export default ConnectionTabs;