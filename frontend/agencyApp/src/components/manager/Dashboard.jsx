import React from 'react';
import { motion } from 'framer-motion';
import { 
  FiTruck, FiUsers, FiDollarSign, FiPackage, 
  FiActivity, FiCalendar, FiAlertCircle, 
  FiCheckCircle, FiClock, FiPieChart 
} from 'react-icons/fi';
import { Bar, Pie } from 'react-chartjs-2';
import 'chart.js/auto';
import { useEffect, useState } from 'react';

const Dashboard = () => {
  const base_url = import.meta.env.VITE_API_URL || '';
  const [inventory, setInventory] = useState([]);
  const [lowStock, setLowStock] = useState(null);
  // Sample data
  const stats = [
    { title: "Total Orders", value: "1,248", icon: <FiTruck />, change: "+12%", trend: "up" },
    { title: "Active Customers", value: "856", icon: <FiUsers />, change: "+5%", trend: "up" },
    { title: "Revenue", value: "₹2,84,500", icon: <FiDollarSign />, change: "+18%", trend: "up" },
    { title: "Low Stock", value: "3 Items", icon: <FiAlertCircle />, change: "-2", trend: "down" }
  ];

  const recentOrders = [
    { id: "#GA-1256", customer: "Rahul Sharma", type: "LPG 14.2kg", status: "Delivered", date: "10 May 2023" },
    { id: "#GA-1255", customer: "Priya Patel", type: "LPG 19kg", status: "In Transit", date: "10 May 2023" },
    { id: "#GA-1254", customer: "Amit Singh", type: "LPG 14.2kg", status: "Processing", date: "9 May 2023" },
    { id: "#GA-1253", customer: "Neha Gupta", type: "LPG 5kg", status: "Delivered", date: "9 May 2023" },
    { id: "#GA-1252", customer: "Vikram Joshi", type: "LPG 14.2kg", status: "Cancelled", date: "8 May 2023" }
  ];

  // const inventory = [
  //   { name: "LPG 14.2kg", stock: 85, color: "bg-primary" },
  //   { name: "LPG 19kg", stock: 45, color: "bg-info" },
  //   { name: "LPG 5kg", stock: 15, color: "bg-warning" },
  //   { name: "LPG 2kg", stock: 5, color: "bg-danger" }
  // ];

  useEffect(() => {
    const fetchInventory = async () => {
      const token = sessionStorage.getItem('token');
      try {
        const res = await fetch(`${base_url}/inventory/liveInventoryList`, {
          method: 'POST',
          headers: {
            'Content-Type': 'application/json',
            Authorization: `Bearer ${token}`,
            Accept: '*/*',
          },
          body: JSON.stringify({}),
        });

        const data = await res.json();

        const mapping = {
          'Gas Cylinder - 14 KG': { name: 'LPG 14.2kg', color: 'bg-primary' },
          'Gas Cylinder - 29 KG': { name: 'LPG 19kg', color: 'bg-info' },
          'Gas Cylinder - 5 KG': { name: 'LPG 5kg', color: 'bg-warning' },
          'Gas Cylinder - 2 KG': { name: 'LPG 2kg', color: 'bg-danger' }, // if available
        };

        const formatted = data.result
          .filter(item => mapping[item.product_name])
          .map(item => ({
            name: mapping[item.product_name].name,
            stock: item.filled_tank,
            color: mapping[item.product_name].color,
          }));

        setInventory(formatted);

        const low = formatted.find(item => item.stock <= 10);
        if (low) setLowStock(low);
      } catch (err) {
        console.error('Failed to fetch inventory:', err);
      }
    };

    fetchInventory();
  }, []);

  const salesData = {
    labels: ['Jan', 'Feb', 'Mar', 'Apr', 'May', 'Jun'],
    datasets: [
      {
        label: 'LPG 14.2kg',
        data: [120, 190, 170, 220, 180, 200],
        backgroundColor: 'rgba(54, 162, 235, 0.7)',
      },
      {
        label: 'LPG 19kg',
        data: [80, 100, 120, 110, 90, 95],
        backgroundColor: 'rgba(75, 192, 192, 0.7)',
      },
      {
        label: 'LPG 5kg',
        data: [60, 70, 65, 75, 55, 60],
        backgroundColor: 'rgba(255, 206, 86, 0.7)',
      },
    ],
  };

  const deliveryStatusData = {
    labels: ['Delivered', 'In Transit', 'Processing', 'Cancelled'],
    datasets: [
      {
        data: [65, 15, 10, 10],
        backgroundColor: [
          'rgba(75, 192, 192, 0.7)',
          'rgba(54, 162, 235, 0.7)',
          'rgba(255, 206, 86, 0.7)',
          'rgba(255, 99, 132, 0.7)',
        ],
      },
    ],
  };

  const containerVariants = {
    hidden: { opacity: 0 },
    visible: {
      opacity: 1,
      transition: {
        staggerChildren: 0.1
      }
    }
  };

  const itemVariants = {
    hidden: { y: 20, opacity: 0 },
    visible: {
      y: 0,
      opacity: 1,
      transition: {
        duration: 0.5
      }
    }
  };

  return (
    <div className="container-fluid py-4">
      <motion.div
        initial="hidden"
        animate="visible"
        variants={containerVariants}
        className="row"
      >
        {/* Header */}
        <motion.div variants={itemVariants} className="col-12 mb-4">
          <div className="d-flex justify-content-between align-items-center">
            <h1 className="h3 mb-0">Dashboard</h1>
            <div>
              <button className="btn btn-primary me-2">
                <FiCalendar className="me-1" /> Today:{" "}
                {new Date().toLocaleDateString()}
              </button>
            </div>
          </div>
        </motion.div>

        {/* Stats Cards */}
        {stats.map((stat, index) => (
          <motion.div
            key={index}
            variants={itemVariants}
            className="col-md-6 col-xl-3 mb-4"
          >
            <div className="card shadow-sm h-100">
              <div className="card-body">
                <div className="d-flex justify-content-between align-items-center">
                  <div>
                    <h6 className="text-muted mb-2">{stat.title}</h6>
                    <h3 className="mb-0">{stat.value}</h3>
                  </div>
                  <div
                    className={`icon-shape icon-lg rounded-3 bg-${
                      stat.trend === "up" ? "success" : "danger"
                    }-soft`}
                  >
                    {stat.icon}
                  </div>
                </div>
                <div
                  className={`mt-3 text-${
                    stat.trend === "up" ? "success" : "danger"
                  }`}
                >
                  <span className={`me-1`}>
                    {stat.change} {stat.trend === "up" ? "↑" : "↓"}
                  </span>
                  <span className="text-muted">vs last month</span>
                </div>
              </div>
            </div>
          </motion.div>
        ))}

        {/* Sales Chart */}
        <motion.div variants={itemVariants} className="col-xl-8 mb-4">
          <div className="card shadow-sm h-100">
            <div className="card-header d-flex justify-content-between align-items-center">
              <h6 className="mb-0">Monthly Sales</h6>
              <div>
                <button className="btn btn-sm btn-outline-secondary me-1">
                  LPG 14.2kg
                </button>
                <button className="btn btn-sm btn-outline-secondary me-1">
                  LPG 19kg
                </button>
                <button className="btn btn-sm btn-outline-secondary">
                  LPG 5kg
                </button>
              </div>
            </div>
            <div className="card-body">
              <Bar
                data={salesData}
                options={{
                  responsive: true,
                  plugins: {
                    legend: {
                      position: "top",
                    },
                  },
                  scales: {
                    y: {
                      beginAtZero: true,
                    },
                  },
                }}
              />
            </div>
          </div>
        </motion.div>

        {/* Delivery Status */}
        <motion.div variants={itemVariants} className="col-xl-4 mb-4">
          <div className="card shadow-sm h-100">
            <div className="card-header">
              <h6 className="mb-0">Delivery Status</h6>
            </div>
            <div className="card-body d-flex flex-column">
              <div style={{ height: "250px" }}>
                <Pie
                  data={deliveryStatusData}
                  options={{
                    responsive: true,
                    plugins: {
                      legend: {
                        position: "bottom",
                      },
                    },
                  }}
                />
              </div>
              <div className="mt-auto pt-3">
                <div className="d-flex align-items-center mb-2">
                  <span className="dot bg-success me-2"></span>
                  <span>Delivered: 65%</span>
                </div>
                <div className="d-flex align-items-center mb-2">
                  <span className="dot bg-primary me-2"></span>
                  <span>In Transit: 15%</span>
                </div>
                <div className="d-flex align-items-center mb-2">
                  <span className="dot bg-warning me-2"></span>
                  <span>Processing: 10%</span>
                </div>
                <div className="d-flex align-items-center">
                  <span className="dot bg-danger me-2"></span>
                  <span>Cancelled: 10%</span>
                </div>
              </div>
            </div>
          </div>
        </motion.div>

        {/* Recent Orders */}
        <motion.div variants={itemVariants} className="col-lg-8 mb-4">
          <div className="card shadow-sm h-100">
            <div className="card-header d-flex justify-content-between align-items-center">
              <h6 className="mb-0">Recent Orders</h6>
              <button className="btn btn-sm btn-outline-primary">
                View All
              </button>
            </div>
            <div className="card-body p-0">
              <div className="table-responsive">
                <table className="table table-hover mb-0">
                  <thead className="bg-light">
                    <tr>
                      <th>Order ID</th>
                      <th>Customer</th>
                      <th>Type</th>
                      <th>Status</th>
                      <th>Date</th>
                      <th>Action</th>
                    </tr>
                  </thead>
                  <tbody>
                    {recentOrders.map((order, index) => (
                      <tr key={index}>
                        <td>{order.id}</td>
                        <td>{order.customer}</td>
                        <td>{order.type}</td>
                        <td>
                          <span
                            className={`badge bg-${getStatusColor(
                              order.status
                            )}-soft`}
                          >
                            {order.status}
                          </span>
                        </td>
                        <td>{order.date}</td>
                        <td>
                          <button className="btn btn-sm btn-outline-secondary">
                            Details
                          </button>
                        </td>
                      </tr>
                    ))}
                  </tbody>
                </table>
              </div>
            </div>
          </div>
        </motion.div>

        {/* Inventory Status */}
        <motion.div variants={itemVariants} className="col-lg-4 mb-4">
          <div className="card shadow-sm h-100">
            <div className="card-header d-flex justify-content-between align-items-center">
              <h6 className="mb-0">Inventory Status</h6>
              <button className="btn btn-sm btn-outline-primary">Manage</button>
            </div>
            <div className="card-body">
              {inventory.map((item, index) => (
                <div key={index} className="mb-3">
                  <div className="d-flex justify-content-between mb-1">
                    <span>{item.name}</span>
                    <span>{item.stock} Cylinders</span>
                  </div>
                  <div className="progress" style={{ height: "8px" }}>
                    <div
                      className={`progress-bar ${item.color}`}
                      role="progressbar"
                      style={{ width: `${Math.min(item.stock, 100)}%` }}
                      aria-valuenow={item.stock}
                      aria-valuemin="0"
                      aria-valuemax="100"
                    ></div>
                  </div>
                </div>
              ))}
              {lowStock && (
                <div className="alert alert-warning mt-4 d-flex align-items-center">
                  <FiAlertCircle className="me-2" size={18} />
                  <strong>{lowStock.name}</strong> is running low. Consider
                  reordering soon.
                </div>
              )}
            </div>
          </div>
        </motion.div>
      </motion.div>
    </div>
  );
};

// Helper function to get status color
function getStatusColor(status) {
  switch(status) {
    case 'Delivered': return 'success';
    case 'In Transit': return 'primary';
    case 'Processing': return 'warning';
    case 'Cancelled': return 'danger';
    default: return 'secondary';
  }
}

export default Dashboard;