import React, { useState, useEffect } from 'react';
import { motion, AnimatePresence } from 'framer-motion';
import Sidebar from '../components/admin/Sidebar';
import TopNavbar from '../components/admin/TopNavbar';
import 'bootstrap/dist/css/bootstrap.min.css';
import { toast, ToastContainer } from "react-toastify";
const MainLayout = ({ children, username,role }) => {
  const [sidebarCollapsed, setSidebarCollapsed] = useState(false);
  const [isMobile, setIsMobile] = useState(false);

  useEffect(() => {
    const handleResize = () => {
      const isNowMobile = window.innerWidth < 768;
      if (isNowMobile !== isMobile) {
        setIsMobile(isNowMobile);
        setSidebarCollapsed(isNowMobile);
      }
    };

    handleResize();
    window.addEventListener('resize', handleResize);
    return () => window.removeEventListener('resize', handleResize);
  }, [isMobile]);

  const toggleSidebar = () => setSidebarCollapsed(prev => !prev);

  return (
    <div className="d-flex flex-column" style={{ minHeight: '100vh' }}>
      {/* Navbar - fixed at top */}
      <div className='bg-light' style={{ position: 'fixed', top: 0, width: '100%', zIndex: 1030 }}>
      <TopNavbar 
        username={username} 
        role={role}
        onMenuClick={toggleSidebar} 
        isSidebarCollapsed={sidebarCollapsed}
        isMobile={isMobile}
        className="fixed-top z-3 shadow-sm"
        style={{ height: '56px', position: 'sticky', top: 0 , overflowY: 'hidden'}}
      />
      </div>

      {/* Main content area */}
      <div className="d-flex" style={{ 
        paddingTop: '56px', // Offset for fixed navbar
        minHeight: 'calc(100vh - 56px)'
      }}>
        {/* Sidebar */}
        <motion.aside
          className="bg-dark text-white d-flex flex-column"
          initial={false}
          animate={{
            width: sidebarCollapsed ? (isMobile ? '0px' : '80px') : '250px',
            minWidth: sidebarCollapsed ? (isMobile ? '0px' : '80px') : '250px'
          }}
          transition={{ type: 'spring', damping: 25 }}
          style={{
            position: 'sticky',
            top: '56px',
            height: 'calc(100vh - 56px)',
            overflowY: 'auto',
            zIndex: 1020
          }}
        >
          <AnimatePresence>
            {(!isMobile || !sidebarCollapsed) && (
              <Sidebar
                collapsed={sidebarCollapsed}
                isMobile={isMobile}
                onItemClick={() => isMobile && setSidebarCollapsed(true)}
              />
            )}
          </AnimatePresence>
        </motion.aside>

        {/* Mobile Overlay */}
        <AnimatePresence>
          {isMobile && !sidebarCollapsed && (
            <motion.div
              className="position-fixed top-0 start-0 w-100 h-100 z-2"
              initial={{ opacity: 0 }}
              animate={{ opacity: 0.5 }}
              exit={{ opacity: 0 }}
              onClick={toggleSidebar}
              style={{
                backgroundColor: 'black',
                top: '56px'
              }}
            />
          )}
        </AnimatePresence>

        {/* Main Content Area */}
        <motion.main
          className="flex-grow-1 p-1 bg-light overflow-auto"
          initial={false}
          animate={{
            marginLeft: !isMobile ? (sidebarCollapsed ? '80px' : '0px') : '0px'
          }}
          transition={{ type: 'spring', damping: 25 }}
          
          style={{
            minHeight: 'calc(100vh - 56px)',
            zIndex: 1,
            overflowY: 'auto',
          }}
        >
          {children}
        </motion.main>
      </div>
      <ToastContainer position="top-right" autoClose={3000} style={{ zIndex: 9999, marginTop: "70px" }} />
    </div>
  );
};

export default MainLayout;