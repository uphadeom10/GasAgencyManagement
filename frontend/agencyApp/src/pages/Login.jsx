import React, { useState } from 'react';
import { toast } from 'react-toastify';
import { useNavigate } from 'react-router-dom';
import { useAuth } from '../auth/AuthProvider';
import loginimg from "../assets/Computer login flat Illustrations.svg";
function Login() {
  const [username, setUsername] = useState('');
  const [password, setPassword] = useState('');
  const [isLoading, setIsLoading] = useState(false);
  const [showPassword, setShowPassword] = useState(false);

  const { loginAction } = useAuth();
  const navigate = useNavigate();
  const handleLogin = async (e) => {
    e.preventDefault();
    setIsLoading(true);

    if (!username.trim()) {
      toast.error('Please enter username');
      setIsLoading(false);
      return;
    }
    if (!password.trim()) {
      toast.error('Please enter password');
      setIsLoading(false);
      return;
    }

    try {
      const role = await loginAction({ username, password });
  
      if (role === 'Admin') {
        navigate('/dashboard');
        toast.success('Login successful!');
      } else if (role === 'Manager') {
        navigate('/manager/dashboard');
        toast.success('Login successful!');
      }else if (role === undefined) {
        toast.error('Invalid credentials. Please try again.');
      }
       else {
        toast.error('Unauthorized role');
      }
    } catch (error) {
      //toast.error(error.message || 'Login failed. Please try again.');
    } finally {
      setIsLoading(false);
    }
  };

  return (
    <div className="min-vh-100 d-flex flex-column flex-md-row align-items-center justify-content-center bg-light">
      {/* Illustration Section - Hidden on small screens */}
      <div className="col-md-6 d-none d-md-flex align-items-center justify-content-center p-5 ">
        <div className="text-center text-white">
          <img 
            src={loginimg} 
            alt="Login Illustration" 
            className="img-fluid mb-4" 
            style={{ maxHeight: '400px' }}
          />
          
        </div>
      </div>

      {/* Login Form Section */}
      <div className="col-12 col-md-6 p-5">
        <div className="mx-auto" style={{ maxWidth: '400px' }}>
          <div className="text-center mb-5">
            <h2 className="fw-bold text-primary">Sign In</h2>
            <p className="text-muted">Enter your credentials to continue</p>
          </div>

          <form onSubmit={handleLogin} className="needs-validation" noValidate>
            <div className="mb-4">
              <label htmlFor="username" className="form-label fw-semibold">Username</label>
              <div className="input-group">
                <span className="input-group-text">
                  <i className="bi bi-person-fill"></i>
                </span>
                <input
                  id="username"
                  onChange={(e) => setUsername(e.target.value)}
                  type="text"
                  className="form-control form-control-lg"
                  value={username}
                  placeholder="Enter your username"
                  required
                />
              </div>
              <div className="invalid-feedback">
                Please enter a valid username
              </div>
            </div>

            <div className="mb-4">
              <label htmlFor="password" className="form-label fw-semibold">Password</label>
              <div className="input-group">
                <span className="input-group-text">
                  <i className="bi bi-lock-fill"></i>
                </span>
                <input
                  id="password"
                  onChange={(e) => setPassword(e.target.value)}
                  type={showPassword ? "text" : "password"}
                  className="form-control form-control-lg"
                  value={password}
                  placeholder="Enter your password"
                  required
                />
                <button 
                  type="button" 
                  className="btn btn-outline-secondary"
                  onClick={() => setShowPassword(!showPassword)}
                >
                  <i className={`bi bi-eye${showPassword ? '-slash' : ''}-fill`}></i>
                </button>
              </div>
              <div className="invalid-feedback">
                Please enter your password
              </div>
            </div>

            <div className="d-flex justify-content-between align-items-center mb-4">
              <div className="form-check">
                <input 
                  type="checkbox" 
                  className="form-check-input" 
                  id="rememberMe"
                />
                <label className="form-check-label" htmlFor="rememberMe">
                  Remember me
                </label>
              </div>
              <a href="/forgot-password" className="text-decoration-none">
                Forgot password?
              </a>
            </div>

            <button 
              type="submit" 
              className="btn btn-primary btn-lg w-100 mb-3"
              disabled={isLoading}
            >
              {isLoading ? (
                <>
                  <span className="spinner-border spinner-border-sm me-2" role="status" aria-hidden="true"></span>
                  Signing in...
                </>
              ) : (
                'Sign In'
              )}
            </button>

            <div className="text-center mt-4">
              <p className="text-muted">
                Don't have an account?{' '}
                <a href="/register" className="text-decoration-none fw-semibold">
                  Sign up
                </a>
              </p>
            </div>
          </form>
        </div>
      </div>
    </div>
  );
}

export default Login;