import React, { useState, useEffect } from 'react';
import { motion } from 'framer-motion';
import { useSearchParams } from 'react-router-dom';
import { 
  FiUser, FiMail, FiPhone, FiMapPin, 
  FiLock, FiCreditCard, FiBell, FiShield,
  FiClock, FiTruck, FiCheckCircle, FiEdit, FiPackage, FiCalendar
} from 'react-icons/fi';
import { useAuth } from '../../auth/AuthProvider'; 

const ProfilePage = () => {
  const [searchParams] = useSearchParams();
  const [activeTab, setActiveTab] = useState('profile');
  const [editMode, setEditMode] = useState(false);
  const [passwordMode, setPasswordMode] = useState(false);
  const { token, user } = useAuth();

  const [formData, setFormData] = useState({
    firstName: '',
    lastName: '',
    mobileNumber: '',
    aadharCardNumber: '',
    userName: '',
    roleId: null,
    isActive: true,
    password: '',
  });

  const [passwordData, setPasswordData] = useState({
    newPassword: '',
    confirmPassword: '',
  });

  const activities = [
    { id: 1, action: "Processed Order #GA-1287", time: "10 mins ago", icon: <FiTruck />, color: "text-primary" },
    { id: 2, action: "Updated inventory levels", time: "45 mins ago", icon: <FiPackage />, color: "text-info" },
    { id: 3, action: "Approved new customer", time: "2 hours ago", icon: <FiUser />, color: "text-success" },
    { id: 4, action: "Changed system settings", time: "Yesterday", icon: <FiShield />, color: "text-warning" },
    { id: 5, action: "Processed 15 orders", time: "2 days ago", icon: <FiCheckCircle />, color: "text-primary" }
  ];

  useEffect(() => {
    const tabFromUrl = searchParams.get('tab');
    if (tabFromUrl === 'settings') {
      setActiveTab('settings');
    }
  }, [searchParams]);

  useEffect(() => {
    const fetchUserData = async () => {
      try {
        const token = sessionStorage.getItem('token');
        const response = await fetch(`/masters/getUserById/${user?.userId}`, {
          headers: {
            Authorization: `Bearer ${token}`,
          },
        });
        const data = await response.json();
        if (data.result) {
          const u = data.result;
          setFormData({
            firstName: u.firstName || '',
            lastName: u.lastName || '',
            mobileNumber: u.mobileNumber || '',
            aadharCardNumber: u.aadharCardNumber || '',
            userName: u.userName || '',
            roleId: u.rolee || null,
            isActive: u.isActive ?? true,
            password: u.password || '',
          });
        }
      } catch (err) {
        console.error('Error fetching user data:', err);
      }
    };
    if (user?.userId) fetchUserData();
  }, [user?.userId]);

  const handleChange = (e) => {
    setFormData({ ...formData, [e.target.name]: e.target.value });
  };

  const handlePasswordChange = (e) => {
    setPasswordData({ ...passwordData, [e.target.name]: e.target.value });
  };

  const handleSaveChanges = async () => {
    try {
      const token = sessionStorage.getItem('token');
      const response = await fetch('/masters/createAndUpdateUsers', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
          Authorization: `Bearer ${token}`,
        },
        body: JSON.stringify({
          id: user?.userId,
          firstName: formData.firstName,
          lastName: formData.lastName,
          mobileNumber: formData.mobileNumber,
          aadharCardNumber: formData.aadharCardNumber,
          userName: formData.userName,
          password: formData.password,
          roleId: { id: formData.roleId?.id || 1 },
          isActive: formData.isActive,
          lastModifiedBy: user?.userId,
        }),
      });

      if (response.ok) {
        alert('Profile updated successfully!');
        setEditMode(false);
      } else {
        alert('Failed to update. Please try again.');
      }
    } catch (err) {
      console.error('Error saving profile:', err);
      alert('Something went wrong!');
    }
  };

  const handleChangePassword = async () => {
    if (passwordData.newPassword !== passwordData.confirmPassword) {
      alert('Passwords do not match!');
      return;
    }
    if (passwordData.newPassword.length < 6) {
      alert('Password must be at least 6 characters!');
      return;
    }
    try {
      const response = await fetch('/api/auth/forgot-password', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({
          username: formData.userName,
          newPassword: passwordData.newPassword,
        }),
      });
      if (response.ok) {
        alert('Password changed successfully!');
        setPasswordMode(false);
        setPasswordData({ newPassword: '', confirmPassword: '' });
      } else {
        alert('Failed to change password.');
      }
    } catch (err) {
      console.error('Error changing password:', err);
      alert('Something went wrong!');
    }
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
      <motion.div 
        initial="hidden"
        animate="visible"
        variants={containerVariants}
        className="row"
      >
        <motion.div variants={itemVariants} className="col-12 mb-4">
          <div className="d-flex justify-content-between align-items-center">
            <h1 className="h3 mb-0">My Profile</h1>
          </div>
        </motion.div>

        <motion.div variants={itemVariants} className="col-lg-4 mb-4">
          <div className="card shadow-sm h-100">
            <div className="card-body text-center p-4">
              <div className="position-relative mx-auto bg-primary rounded-circle d-flex align-items-center justify-content-center" style={{ width: '120px', height: '120px' }}>
                <FiUser size={50} color="white" />
              </div>
              
              <h3 className="mt-4 mb-1">{formData.firstName} {formData.lastName}</h3>
              <span className="badge bg-primary">{user?.role}</span>
              
              <div className="d-flex justify-content-center gap-3 mt-4">
                <button className="btn btn-primary" onClick={() => { setActiveTab('profile'); setEditMode(true); setPasswordMode(false); }}>
                  <FiEdit className="me-1" /> Edit Profile
                </button>
                <button className="btn btn-outline-secondary" onClick={() => { setActiveTab('profile'); setPasswordMode(true); setEditMode(false); }}>
                  <FiLock className="me-1" /> Change Password
                </button>
              </div>
              
              <hr className="my-4" />
              
              <div className="text-start">
                <h6 className="mb-3 text-uppercase text-muted">Account Information</h6>
                <div className="d-flex align-items-start mb-3">
                  <FiUser className="mt-1 me-2 text-muted" />
                  <div>
                    <small className="text-muted">Username</small>
                    <p className="mb-0">{formData.userName}</p>
                  </div>
                </div>
                <div className="d-flex align-items-start mb-3">
                  <FiPhone className="mt-1 me-2 text-muted" />
                  <div>
                    <small className="text-muted">Mobile</small>
                    <p className="mb-0">{formData.mobileNumber}</p>
                  </div>
                </div>
                <div className="d-flex align-items-start mb-3">
                  <FiCreditCard className="mt-1 me-2 text-muted" />
                  <div>
                    <small className="text-muted">Aadhar</small>
                    <p className="mb-0">{formData.aadharCardNumber}</p>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </motion.div>

        <motion.div variants={itemVariants} className="col-lg-8">
          <div className="card shadow-sm mb-4">
            <div className="card-body p-2">
              <ul className="nav nav-pills">
                <li className="nav-item">
                  <button className={`nav-link ${activeTab === 'profile' ? 'active' : ''}`} onClick={() => setActiveTab('profile')}>
                    <FiUser className="me-1" /> Profile
                  </button>
                </li>
                <li className="nav-item">
                  <button className={`nav-link ${activeTab === 'settings' ? 'active' : ''}`} onClick={() => setActiveTab('settings')}>
                    <FiShield className="me-1" /> Account Settings
                  </button>
                </li>
                <li className="nav-item">
                  <button className={`nav-link ${activeTab === 'activity' ? 'active' : ''}`} onClick={() => setActiveTab('activity')}>
                    <FiClock className="me-1" /> Activity Log
                  </button>
                </li>
              </ul>
            </div>
          </div>

          {activeTab === 'profile' && (
            <motion.div initial={{ opacity: 0 }} animate={{ opacity: 1 }} transition={{ duration: 0.3 }} className="card shadow-sm mb-4">
              <div className="card-header d-flex justify-content-between align-items-center">
                <h6 className="mb-0">Personal Information</h6>
                {!passwordMode && (
                  <button className="btn btn-sm btn-outline-primary" onClick={() => setEditMode(!editMode)}>
                    {editMode ? 'Cancel' : 'Edit Information'}
                  </button>
                )}
              </div>
              <div className="card-body">
                {passwordMode ? (
                  <div>
                    <h6 className="mb-3">Change Password</h6>
                    <div className="row mb-3">
                      <div className="col-md-6">
                        <label className="form-label">New Password</label>
                        <input type="password" name="newPassword" className="form-control" value={passwordData.newPassword} onChange={handlePasswordChange} />
                      </div>
                      <div className="col-md-6">
                        <label className="form-label">Confirm Password</label>
                        <input type="password" name="confirmPassword" className="form-control" value={passwordData.confirmPassword} onChange={handlePasswordChange} />
                      </div>
                    </div>
                    <div className="text-end">
                      <button className="btn btn-outline-secondary me-2" onClick={() => setPasswordMode(false)}>Cancel</button>
                      <button className="btn btn-primary" onClick={handleChangePassword}>Change Password</button>
                    </div>
                  </div>
                ) : editMode ? (
                  <form>
                    <div className="row mb-3">
                      <div className="col-md-6">
                        <label className="form-label">First Name</label>
                        <input type="text" name="firstName" className="form-control" value={formData.firstName} onChange={handleChange} />
                      </div>
                      <div className="col-md-6">
                        <label className="form-label">Last Name</label>
                        <input type="text" name="lastName" className="form-control" value={formData.lastName} onChange={handleChange} />
                      </div>
                    </div>
                    <div className="row mb-3">
                      <div className="col-md-6">
                        <label className="form-label">Mobile Number</label>
                        <input type="tel" name="mobileNumber" className="form-control" value={formData.mobileNumber} onChange={handleChange} />
                      </div>
                      <div className="col-md-6">
                        <label className="form-label">Aadhar Card Number</label>
                        <input type="text" name="aadharCardNumber" className="form-control" value={formData.aadharCardNumber} onChange={handleChange} />
                      </div>
                    </div>
                    <div className="text-end">
                      <button type="button" className="btn btn-outline-secondary me-2" onClick={() => setEditMode(false)}>Cancel</button>
                      <button type="button" className="btn btn-primary" onClick={handleSaveChanges}>Save Changes</button>
                    </div>
                  </form>
                ) : (
                  <div className="row">
                    <div className="col-md-6 mb-3">
                      <label className="form-label text-muted">First Name</label>
                      <p className="fw-semibold">{formData.firstName}</p>
                    </div>
                    <div className="col-md-6 mb-3">
                      <label className="form-label text-muted">Last Name</label>
                      <p className="fw-semibold">{formData.lastName}</p>
                    </div>
                    <div className="col-md-6 mb-3">
                      <label className="form-label text-muted">Mobile Number</label>
                      <p className="fw-semibold">{formData.mobileNumber}</p>
                    </div>
                    <div className="col-md-6 mb-3">
                      <label className="form-label text-muted">Aadhar Card Number</label>
                      <p className="fw-semibold">{formData.aadharCardNumber}</p>
                    </div>
                    <div className="col-md-6 mb-3">
                      <label className="form-label text-muted">Username</label>
                      <p className="fw-semibold">{formData.userName}</p>
                    </div>
                    <div className="col-md-6 mb-3">
                      <label className="form-label text-muted">Role</label>
                      <p className="fw-semibold">{user?.role}</p>
                    </div>
                  </div>
                )}
              </div>
            </motion.div>
          )}

          {activeTab === 'settings' && (
            <motion.div initial={{ opacity: 0 }} animate={{ opacity: 1 }} transition={{ duration: 0.3 }}>
              <div className="card shadow-sm mb-4">
                <div className="card-header">
                  <h6 className="mb-0">Security Settings</h6>
                </div>
                <div className="card-body">
                  <div className="d-flex justify-content-between align-items-center mb-4 pb-3 border-bottom">
                    <div>
                      <h6 className="mb-1">Password</h6>
                      <p className="small text-muted mb-0">Change your account password</p>
                    </div>
                    <button className="btn btn-outline-primary" onClick={() => { setActiveTab('profile'); setPasswordMode(true); setEditMode(false); }}>Change Password</button>
                  </div>
                  <div className="d-flex justify-content-between align-items-center mb-4 pb-3 border-bottom">
                    <div>
                      <h6 className="mb-1">Two-Factor Authentication</h6>
                      <p className="small text-muted mb-0">Add extra security to your account</p>
                    </div>
                    <div className="form-check form-switch">
                      <input className="form-check-input" type="checkbox" id="2faSwitch" />
                    </div>
                  </div>
                  <div className="d-flex justify-content-between align-items-center">
                    <div>
                      <h6 className="mb-1">Login Notifications</h6>
                      <p className="small text-muted mb-0">Get notified for new logins</p>
                    </div>
                    <div className="form-check form-switch">
                      <input className="form-check-input" type="checkbox" id="notifSwitch" defaultChecked />
                    </div>
                  </div>
                </div>
              </div>

              <div className="card shadow-sm">
                <div className="card-header">
                  <h6 className="mb-0">Notification Preferences</h6>
                </div>
                <div className="card-body">
                  <div className="mb-3">
                    <div className="form-check">
                      <input className="form-check-input" type="checkbox" id="orderNotif" defaultChecked />
                      <label className="form-check-label" htmlFor="orderNotif">New orders</label>
                    </div>
                  </div>
                  <div className="mb-3">
                    <div className="form-check">
                      <input className="form-check-input" type="checkbox" id="deliveryNotif" defaultChecked />
                      <label className="form-check-label" htmlFor="deliveryNotif">Delivery status updates</label>
                    </div>
                  </div>
                  <div className="mb-3">
                    <div className="form-check">
                      <input className="form-check-input" type="checkbox" id="stockNotif" defaultChecked />
                      <label className="form-check-label" htmlFor="stockNotif">Low stock alerts</label>
                    </div>
                  </div>
                  <div className="mb-3">
                    <div className="form-check">
                      <input className="form-check-input" type="checkbox" id="promoNotif" />
                      <label className="form-check-label" htmlFor="promoNotif">Promotions and offers</label>
                    </div>
                  </div>
                  <div className="text-end mt-3">
                    <button className="btn btn-primary" onClick={() => alert('Preferences saved!')}>Save Preferences</button>
                  </div>
                </div>
              </div>
            </motion.div>
          )}

          {activeTab === 'activity' && (
            <motion.div initial={{ opacity: 0 }} animate={{ opacity: 1 }} transition={{ duration: 0.3 }} className="card shadow-sm">
              <div className="card-header">
                <h6 className="mb-0">Recent Activity</h6>
              </div>
              <div className="card-body">
                <ul className="list-group list-group-flush">
                  {activities.map(activity => (
                    <motion.li 
                      key={activity.id}
                      initial={{ opacity: 0, x: -10 }}
                      animate={{ opacity: 1, x: 0 }}
                      transition={{ duration: 0.3 }}
                      className="list-group-item border-0 px-0 py-3"
                    >
                      <div className="d-flex">
                        <div className={`me-3 ${activity.color}`}>{activity.icon}</div>
                        <div className="flex-grow-1">
                          <h6 className="mb-1">{activity.action}</h6>
                          <p className="small text-muted mb-0"><FiClock className="me-1" /> {activity.time}</p>
                        </div>
                      </div>
                    </motion.li>
                  ))}
                </ul>
                <button className="btn btn-outline-primary w-100 mt-3">Load More Activities</button>
              </div>
            </motion.div>
          )}
        </motion.div>
      </motion.div>
    </div>
  );
};

export default ProfilePage;