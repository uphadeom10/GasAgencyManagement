import React from 'react';
import { motion } from 'framer-motion';
import { 
  FiTruck, FiUsers, FiDollarSign, FiPackage, 
  FiActivity, FiCalendar, FiAlertCircle, 
  FiCheckCircle, FiClock, FiPieChart, FiHash 
} from 'react-icons/fi';
import { Bar, Pie } from 'react-chartjs-2';
import 'chart.js/auto';
import { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { Card, Button, Badge, ListGroup, Row, Col, Accordion } from 'react-bootstrap';
import { format } from 'date-fns';
import OrderDetailsModal from './OrderDetailsModal';


const Dashboard = () => {
  const base_url = import.meta.env.VITE_API_URL || '';
  const navigate = useNavigate();
  const [inventory, setInventory] = useState([]);
  const [lowStock, setLowStock] = useState([]);
  const [ recentOrders, setRecentOrders ] = useState([]);
  const [visibleCount, setVisibleCount] = useState(5);
  const [selectedOrder, setSelectedOrder] = useState(null);
  const [showDetailsModal, setShowDetailsModal] = useState(false);

const handleToggleShowMore = () => {
  setVisibleCount((prev) =>
    prev === recentOrders.length ? 5 : recentOrders.length
  );
};

const handleViewDetails = (order) => {
  setSelectedOrder(order);
  setShowDetailsModal(true);
};

const handleCloseDetailsModal = () => {
  setShowDetailsModal(false);
  setSelectedOrder(null);
};

  const stats = [
    { title: "Total Orders", value: "1,248", icon: <FiTruck />, change: "+12%", trend: "up" },
    { title: "Active Customers", value: "856", icon: <FiUsers />, change: "+5%", trend: "up" },
    { title: "Revenue", value: "₹2,84,500", icon: <FiDollarSign />, change: "+18%", trend: "up" },
    { title: "Low Stock", value: "3 Items", icon: <FiAlertCircle />, change: "-2", trend: "down" }
  ];

const fetchTodayAssignments = async () => {
    try {
      const token = sessionStorage.getItem("token");
      const today = new Date().toISOString().split("T")[0];
      const response = await fetch(
        `${base_url}/dailyAssignment/assignments/by_date`,
        {
          headers: {
            Authorization: `Bearer ${token}`,
            Accept: "application/json",
          },
        }
      );
      if (!response.ok) {
        throw new Error(`HTTP error! status: ${response.status}`);
      }
      const data = await response.json();
      console.log(data);
      if (data.result) {
        const sortedOrders = data.result.sort((a, b) =>
          new Date(b.assignmentCreatedDate) - new Date(a.assignmentCreatedDate)
        );
        setRecentOrders(sortedOrders)
      } else {
        setRecentOrders([]);
      }
    } catch (error) {
      console.error("Error fetching today assignments:", error);
    }
  };

  useEffect(() => {
    fetchTodayAssignments();
  }, []);

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
          1: { name: "Gas Cylinder - 5 KG", color: "bg-warning", max: 1500, threshold: 100 },
          2: { name: "Gas Cylinder - 14 KG", color: "bg-primary", max: 1500, threshold: 1000 },
          3: { name: "Gas Cylinder - 29 KG", color: "bg-info", max: 600, threshold: 60 },
          4: { name: "Small Lighter", color: "bg-danger", max: 250, threshold: 30 },
          5: { name: "Medium size Lighter", color: "bg-warning", max: 350, threshold: 40 },
          6: { name: "Large Lighter", color: "bg-secondary", max: 250, threshold: 30 },
          7: { name: "Small Burner", color: "bg-info", max: 500, threshold: 40 },
          8: { name: "Medium Burner", color: "bg-primary", max: 400, threshold: 40 },
          9: { name: "Larger Burner", color: "bg-success", max: 300, threshold: 30 },
        };

        const formatted = data.result
          .filter(item => mapping[item.product_id])
          .map(item => ({
            name: mapping[item.product_id].name,
            stock: item.filled_tank,
            color: mapping[item.product_id].color,
            threshold: mapping[item.product_id].threshold,
            max: mapping[item.product_id].max,
          }));

        console.log('formatted', formatted);
        const reversed = [...formatted].reverse();
        setInventory(reversed);
        console.log('reversed', reversed);
        const low = formatted.filter(item => item.stock <= item.threshold);
        console.log('low', low);
        setLowStock(low);

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

  const statusLabelsMap = {
    3: 'Delivered',
    4: 'Not Delivered',
    5: 'Pending',
    6: 'In Progress',
    7: 'Done',
    8: 'Initiated',
    9: 'Cancelled',
    10: 'Done and Closed',
    11: 'Filled',
    12: 'Unfilled',
  };

  const calculateAllStatusCounts = (assignments) => {
    const counts = {};
    assignments.forEach(item => {
      const label = statusLabelsMap[item.statusId] || 'Unknown';
      counts[label] = (counts[label] || 0) + 1;
    });
    return counts;
  };

  const getStatusPercentages = (assignments) => {
    const statusMap = {
      3: { label: 'Delivered', color: 'bg-success' },
      4: { label: 'Not Delivered', color: 'bg-danger' },
      5: { label: 'Pending', color: 'bg-info' },
      6: { label: 'In Progress', color: 'bg-warning' },
      7: { label: 'Done', color: 'bg-success' },
      8: { label: 'Initiated', color: 'bg-primary' },
      9: { label: 'Cancelled', color: 'bg-danger' },
      10: { label: 'Done and Closed', color: 'bg-success' },
      11: { label: 'Filled', color: 'bg-success' },
      12: { label: 'Unfilled', color: 'bg-secondary' },
    };

    const total = assignments.length;
    const statusCounts = {};

    assignments.forEach(item => {
      const { statusId } = item;
      const status = statusMap[statusId] || { label: 'Unknown', color: 'bg-secondary' };
      const label = status.label;
      if (!statusCounts[label]) {
        statusCounts[label] = { count: 0, color: status.color };
      }
      statusCounts[label].count++;
    });

    return Object.entries(statusCounts).map(([label, { count, color }]) => ({
      label,
      percentage: ((count / total) * 100).toFixed(0),
      color,
    }));
  };

  const getStatusBadge = (statusId) => {
    switch (statusId) {
      case 6: return { variant: 'warning', text: 'In Progress' };
      case 3: return { variant: 'success', text: 'Delivered' };
      case 4: return { variant: 'danger', text: 'Not Delivered' };
      case 5: return { variant: 'info', text: 'Pending' };
      case 7: return { variant: 'success', text: 'Done' };
      case 8: return { variant: 'primary', text: 'Initiated' };
      case 9: return { variant: 'danger', text: 'Cancelled' };
      case 10: return { variant: 'success', text: 'Done and Closed' };
      case 11: return { variant: 'success', text: 'Filled' };
      case 12: return { variant: 'secondary', text: 'Unfilled' };
      default: return { variant: 'secondary', text: 'Unknown' };
    }
  };

  const formatDate = (dateString) => {
    if (!dateString) return 'N/A';
    try {
      return format(new Date(dateString), 'dd MMM yyyy');
    } catch {
      return dateString;
    }
  };

  const statusCounts = calculateAllStatusCounts(recentOrders);
  const chartLabels = Object.keys(statusCounts);
  const chartData = chartLabels.map(label => statusCounts[label]);
  const statusPercentages = getStatusPercentages(recentOrders);

  const deliveryStatusData = {
    labels: chartLabels,
    datasets: [
      {
        data: chartData,
        backgroundColor: [
          '#4bc0c0', '#ff6384', '#36a2eb', '#ffcd56', '#81c784',
          '#9575cd', '#e57373', '#4caf50', '#7986cb', '#b0bec5', '#cfd8dc',
        ].slice(0, chartLabels.length),
      },
    ],
  };

  const containerVariants = {
    hidden: { opacity: 0 },
    visible: { opacity: 1, transition: { staggerChildren: 0.1 } }
  };

  const itemVariants = {
    hidden: { y: 20, opacity: 0 },
    visible: { y: 0, opacity: 1, transition: { duration: 0.5 } }
  };

  return (
    <div className="container-fluid py-4">
      <motion.div initial="hidden" animate="visible" variants={containerVariants} className="row">

        {/* Header */}
        <motion.div variants={itemVariants} className="col-12 mb-4">
          <div className="d-flex justify-content-between align-items-center">
            <h1 className="h3 mb-0">Dashboard</h1>
            <div>
              <button className="btn btn-primary me-2">
                <FiCalendar className="me-1" /> Today: {new Date().toLocaleDateString()}
              </button>
            </div>
          </div>
        </motion.div>

        {/* Stats Cards */}
        {stats.map((stat, index) => (
          <motion.div key={index} variants={itemVariants} className="col-md-6 col-xl-3 mb-4">
            <div className="card shadow-sm h-100">
              <div className="card-body">
                <div className="d-flex justify-content-between align-items-center">
                  <div>
                    <h6 className="text-muted mb-2">{stat.title}</h6>
                    <h3 className="mb-0">{stat.value}</h3>
                  </div>
                  <div className={`icon-shape icon-lg rounded-3 bg-${stat.trend === "up" ? "success" : "danger"}-soft`}>
                    {stat.icon}
                  </div>
                </div>
                <div className={`mt-3 text-${stat.trend === "up" ? "success" : "danger"}`}>
                  <span className="me-1">{stat.change} {stat.trend === "up" ? "↑" : "↓"}</span>
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
                <button className="btn btn-sm btn-outline-secondary me-1">LPG 14.2kg</button>
                <button className="btn btn-sm btn-outline-secondary me-1">LPG 19kg</button>
                <button className="btn btn-sm btn-outline-secondary">LPG 5kg</button>
              </div>
            </div>
            <div className="card-body">
              <Bar data={salesData} options={{ responsive: true, plugins: { legend: { position: "top" } }, scales: { y: { beginAtZero: true } } }} />
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
                <Pie data={deliveryStatusData} options={{ responsive: true, plugins: { legend: { position: "bottom" } } }} />
              </div>
              <div className="mt-auto pt-3">
                {statusPercentages.map((item, index) => (
                  <div className="d-flex align-items-center mb-2" key={index}>
                    <span className={`dot ${item.color} me-2`}></span>
                    <span>{item.label}: {item.percentage}%</span>
                  </div>
                ))}
              </div>
            </div>
          </div>
        </motion.div>

        {/* Recent Orders */}
        <motion.div variants={itemVariants} className="col-lg-8 mb-4">
          <div className="card shadow-sm h-100">
            <div className="card-header d-flex justify-content-between align-items-center">
              <h6 className="mb-0">Recent Orders</h6>
              <button
                className="btn btn-sm btn-outline-primary"
                onClick={() => navigate('/orders')}
              >
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
                      <th>Status</th>
                      <th>Date</th>
                      <th>Action</th>
                    </tr>
                  </thead>
                  <tbody>
                    {recentOrders.slice(0, visibleCount).map((order, index) => (
                      <tr key={index}>
                        <td><Badge bg="primary"><FiHash className="me-1" />{order.assignmentId}</Badge></td>
                        <td>{order.customerName || order.pointHolderName}</td>
                        <td><Badge bg={getStatusBadge(order.statusId).variant}>{getStatusBadge(order.statusId).text}</Badge></td>
                        <td>{formatDate(order.assignmentCreatedDate)}</td>
                        <td>
                          <button className="btn btn-sm btn-outline-secondary" onClick={() => handleViewDetails(order)}>
                            Details
                          </button>
                        </td>
                      </tr>
                    ))}
                  </tbody>
                </table>
              </div>
              {recentOrders.length > 5 && (
                <div className="text-center py-2">
                  <button className="btn btn-sm btn-link" onClick={handleToggleShowMore}>
                    {visibleCount === recentOrders.length ? 'Show Less' : 'Show More'}
                  </button>
                </div>
              )}
            </div>
          </div>
        </motion.div>

        {/* Inventory Status */}
        <motion.div variants={itemVariants} className="col-lg-4 mb-4">
          <div className="card shadow-sm h-100">
            <div className="card-header d-flex justify-content-between align-items-center">
              <h6 className="mb-0">Inventory Status</h6>
              <button
                className="btn btn-sm btn-outline-primary"
                onClick={() => navigate('/inventory')}
              >
                Manage
              </button>
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
                      style={{ width: `${(item.stock / item.max) * 100}%` }}
                      aria-valuenow={item.stock}
                      aria-valuemin="0"
                      aria-valuemax={item.max}
                    ></div>
                  </div>
                </div>
              ))}
              {lowStock.length > 0 && (
                <div className="alert alert-warning mt-4">
                  <FiAlertCircle className="me-2" size={18} />
                  <strong>Low Stock Alert:</strong>
                  <ul className="mb-0 mt-2">
                    {lowStock.map((item, idx) => (
                      <li key={idx}><strong>{item.name}</strong>: Only {item.stock} left</li>
                    ))}
                  </ul>
                </div>
              )}
            </div>
          </div>
        </motion.div>

      </motion.div>

      <OrderDetailsModal
        show={showDetailsModal}
        handleClose={handleCloseDetailsModal}
        order={selectedOrder}
      />
    </div>
  );
};

export default Dashboard;