import { useNavigate } from 'react-router-dom';
import { Container, Row, Col, Button } from 'react-bootstrap';
import { FaLock, FaArrowLeft, FaHome } from 'react-icons/fa';
import { useAuth } from "../auth/AuthProvider";
const Unauthorized = () => {
  const navigate = useNavigate();
  const { user } = useAuth(); 
  const fallbackPath = 
  user?.role === "Admin" ? "/dashboard" :
  user?.role === "Manager" ? "/manager/dashboard" :
  "/login"; // Adjust based on your routing logic
  return (
    <Container fluid className="min-vh-100 d-flex align-items-center justify-content-center bg-light p-4">
      <Row className="justify-content-center align-items-center g-4">
        {/* Illustration Column - shows on medium+ screens */}
        <Col md={6} className="d-none d-md-flex justify-content-center">
          <div className="pe-md-4">
            <svg width="100%" height="100%" viewBox="0 0 512 512" fill="none" xmlns="http://www.w3.org/2000/svg">
              <path d="M368 192H352V112C352 57.42 310.579 16 256 16C201.42 16 160 57.42 160 112V192H144C117.49 192 96 213.49 96 240V432C96 458.51 117.49 480 144 480H368C394.51 480 416 458.51 416 432V240C416 213.49 394.51 192 368 192ZM256 352C238.33 352 224 337.67 224 320C224 302.33 238.33 288 256 288C273.67 288 288 302.33 288 320C288 337.67 273.67 352 256 352ZM320 192H192V112C192 76.654 220.654 48 256 48C291.346 48 320 76.654 320 112V192Z" fill="#FF4D4F"/>
              <path d="M256 0C202.057 0 160 42.057 160 96V128H192V96C192 60.654 220.654 32 256 32C291.346 32 320 60.654 320 96V128H352V96C352 42.057 309.943 0 256 0Z" fill="#FF4D4F" fillOpacity="0.2"/>
            </svg>
          </div>
        </Col>

        {/* Content Column - full width on mobile, half on desktop */}
        <Col xs={12} md={6} className="text-center text-md-start">
          <div className="p-4 p-lg-5 bg-white rounded shadow-sm">
            <div className="mb-4 text-danger">
              <FaLock size={48} className="mb-3" />
              <h1 className="mb-3">403 - Access Denied</h1>
            </div>
            
            <p className="lead text-muted mb-4">
              You don't have permission to access this resource.
            </p>
            
            <p className="text-muted mb-4">
              Please contact your administrator if you believe this is an error.
            </p>
            
            <div className="d-flex flex-column flex-sm-row justify-content-center justify-content-md-start gap-3">
              <Button 
                variant="outline-primary" 
                onClick={() => navigate(fallbackPath)}
                className="d-flex align-items-center justify-content-center"
              >
                <FaArrowLeft className="me-2" />
                Go Back
              </Button>
              
              <Button 
                variant="primary" 
                onClick={() => navigate('/')}
                className="d-flex align-items-center justify-content-center"
              >
                <FaHome className="me-2" />
                Home
              </Button>
            </div>
          </div>
          
          <div className="mt-4 text-center text-muted small">
            <p>Need assistance? <a href="mailto:support@example.com">Contact our team</a></p>
          </div>
        </Col>
      </Row>
    </Container>
  );
};

export default Unauthorized;