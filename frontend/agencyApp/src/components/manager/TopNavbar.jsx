import React from 'react';
import 'bootstrap/dist/css/bootstrap.min.css';
import 'bootstrap-icons/font/bootstrap-icons.css';
import { useAuth } from '../../auth/AuthProvider';
import logopath from '../../assets/gas-cylinder.png';

const TopNavbar = ({ isMobile = false, onMenuClick }) => {
  const { token, user, logOut} = useAuth();
  const role = user?.role || 'Guest';
  const username = user?.name || 'Guest';
  const handlelogout = async () => {
    try {
      await logOut();
    } catch (error) {
      console.error('Logout failed:', error);
    }
  }
  return (
    <nav
      className="navbar navbar-expand navbar-dark shadow-sm w-100"
      style={{
        background: 'linear-gradient(90deg, #2b5876 0%, #4e4376 100%)',
        height: '56px',
        padding: '0 20px',
        zIndex: 10
      }}
    >
      <div className="container-fluid px-0 d-flex justify-content-between align-items-center">

        <div className="d-flex align-items-center">
          {isMobile && (
            <button
              className="btn btn-outline-light me-2 d-md-none d-flex align-items-center justify-content-center"
              style={{
                width: '40px',
                height: '40px',
                borderRadius: '8px',
                padding: '0',
              }}
              onClick={onMenuClick}
            >
              <i className="bi bi-list" style={{ fontSize: '1.5rem' }}></i>
            </button>
          )}

          <div
            style={{
              width: '40px',
              height: '40px',
              borderRadius: '8px',
              background: 'rgba(255,255,255,0.2)',
              display: 'flex',
              alignItems: 'center',
              justifyContent: 'center',
              marginRight: '12px'
            }}
          >
            <img src={logopath} style={{ width: '2rem', height: '2rem' }} />
          </div>
          <span className="navbar-brand mb-0" style={{
            fontSize: '1.2rem',
            fontWeight: '600',
            color: 'white'
          }}>Gas Agency</span>
        </div>

        <div className="ms-auto">
          <ul className="navbar-nav">
            <li className="nav-item dropdown">
              <a

                className="nav-link dropdown-toggle d-flex align-items-center"
                href="#"
                id="navbarDropdown"
                role="button"
                data-bs-toggle="dropdown"
                aria-expanded="false"
                style={{
                  padding: '8px 12px',
                  borderRadius: '8px',
                  transition: 'all 0.3s ease'
                }}
                onMouseEnter={(e) => e.currentTarget.style.background = 'rgba(255,255,255,0.1)'}
                onMouseLeave={(e) => e.currentTarget.style.background = 'transparent'}
              >
                <div
                  className="rounded-circle d-flex align-items-center justify-content-center"
                  style={{
                    width: '40px',
                    height: '40px',
                    background: 'rgba(255,255,255,0.2)',
                    color: 'white'
                  }}
                >
                  <i className="bi bi-person-fill"></i>
                </div>
                <div className="d-none d-md-block ms-2 text-start">
                  <div style={{ fontWeight: '500', color: 'white' }}>{username}</div>
                  <div style={{ fontSize: '0.8rem', opacity: 0.8, color: 'white' }}>{role}</div>
                </div>
              </a>
              <ul
                className="dropdown-menu dropdown-menu-end shadow"
                aria-labelledby="navbarDropdown"
                style={{
                  border: 'none',
                  borderRadius: '8px',
                  background: 'linear-gradient(180deg, #2b5876 0%, #4e4376 100%)',
                  overflow: 'hidden'
                }}
              >
                <li>
                  <a className="dropdown-item d-flex align-items-center" href="/manager/profile"
                    style={{ color: 'white', padding: '10px 15px' }}
                    onMouseEnter={(e) => e.currentTarget.style.background = 'rgba(255,255,255,0.1)'}
                    onMouseLeave={(e) => e.currentTarget.style.background = 'transparent'}
                  >
                    <i className="bi bi-person me-2"></i> Profile
                  </a>
                </li>
                <li>
                  <a className="dropdown-item d-flex align-items-center" href="/manager/profile?tab=settings"
                    style={{ color: 'white', padding: '10px 15px' }}
                    onMouseEnter={(e) => e.currentTarget.style.background = 'rgba(255,255,255,0.1)'}
                    onMouseLeave={(e) => e.currentTarget.style.background = 'transparent'}
                  >
                    <i className="bi bi-gear me-2"></i> Settings
                  </a>
                </li>
                <li><hr className="dropdown-divider my-2" style={{ borderColor: 'rgba(255,255,255,0.1)' }} /></li>
                <li>
                  <a className="dropdown-item d-flex align-items-center" onClick={handlelogout}
                    style={{ color: 'white', padding: '10px 15px' }}
                    onMouseEnter={(e) => e.currentTarget.style.background = 'rgba(255,255,255,0.1)'}
                    onMouseLeave={(e) => e.currentTarget.style.background = 'transparent'}
                  >
                    <i className="bi bi-box-arrow-right me-2"></i> Logout
                  </a>
                </li>
              </ul>
            </li>
          </ul>
        </div>
      </div>
    </nav>
  );
};

export default TopNavbar;
