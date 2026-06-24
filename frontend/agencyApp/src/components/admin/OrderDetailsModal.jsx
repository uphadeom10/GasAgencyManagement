import { Modal, Button, Badge } from 'react-bootstrap';
import { FiHash, FiUser, FiCalendar, FiInfo } from 'react-icons/fi';
import { format } from 'date-fns';

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

const OrderDetailsModal = ({ show, handleClose, order }) => {
  if (!order) return null;

  const statusBadge = getStatusBadge(order.statusId);

  return (
    <Modal show={show} onHide={handleClose} centered>
      <Modal.Header closeButton className="px-3 py-2 border-bottom">
        <Modal.Title className="fs-6 fw-bold">
          Order Details #{order.assignmentId}
        </Modal.Title>
      </Modal.Header>

      <Modal.Body className="px-3 py-3">
        <div className="mb-3 d-flex align-items-center">
          <FiHash className="me-2 text-muted" />
          <span className="fw-semibold me-2">Order ID:</span>
          <Badge bg="primary">#{order.assignmentId}</Badge>
        </div>

        <div className="mb-3 d-flex align-items-center">
          <FiUser className="me-2 text-muted" />
          <span className="fw-semibold me-2">Customer:</span>
          <span>{order.customerName || order.pointHolderName || 'N/A'}</span>
        </div>

        <div className="mb-3 d-flex align-items-center">
          <FiInfo className="me-2 text-muted" />
          <span className="fw-semibold me-2">Status:</span>
          <Badge bg={statusBadge.variant}>{statusBadge.text}</Badge>
        </div>

        <div className="mb-3 d-flex align-items-center">
          <FiCalendar className="me-2 text-muted" />
          <span className="fw-semibold me-2">Date:</span>
          <span>{formatDate(order.assignmentCreatedDate)}</span>
        </div>

        {order.deliveryPersonName && (
          <div className="mb-3 d-flex align-items-center">
            <FiUser className="me-2 text-muted" />
            <span className="fw-semibold me-2">Delivery Person:</span>
            <span>{order.deliveryPersonName}</span>
          </div>
        )}

        {order.address && (
          <div className="mb-3">
            <span className="fw-semibold">Address:</span>
            <p className="mb-0 text-muted">{order.address}</p>
          </div>
        )}
      </Modal.Body>

      <Modal.Footer className="px-3 py-2 border-top">
        <Button variant="outline-secondary" size="sm" onClick={handleClose}>
          Close
        </Button>
      </Modal.Footer>
    </Modal>
  );
};

export default OrderDetailsModal;