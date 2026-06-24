
const VehicleTabs = ({ activeTab, setActiveTab }) => {
  return (
    <ul className="nav nav-tabs mb-3">
      <li className="nav-item">
        <button
          className={`nav-link ${activeTab === 'all' ? 'active' : ''}`}
          onClick={() => setActiveTab('all')}
        >
          All Vehicles
        </button>
      </li>
      <li className="nav-item">
        <button
          className={`nav-link ${activeTab === 'due' ? 'active' : ''}`}
          onClick={() => setActiveTab('due')}
        >
          Due for Service
        </button>
      </li>
    </ul>
  );
};

export default VehicleTabs;