import { useState, useEffect, useCallback } from "react";
import { Modal, Form, Button, Row, Col, Table, Alert } from "react-bootstrap";
import { useAuth } from "../../auth/AuthProvider"; // Adjust the import path as necessary

const CreateAssignmentModal = ({ show, onHide, onSubmit }) => {
  const base_Url = import.meta.env.VITE_API_URL || "";
  const { user } = useAuth();
  const userId = user?.userId;
  const [formData, setFormData] = useState({
    isCustomer: false,
    isPoint: true,
    customerId: null,
    agencyPointId: null,
    dailyAssignmentDetailsRequestDtos: [],
    isCompletedByDeliveryPerson: false,
    createdBy: userId,
    lastModifiedBy: userId,
    assignedById: { id: userId },
  });

  const [products, setProducts] = useState([]);
  const [customers, setCustomers] = useState([]);
  const [agencyPoints, setAgencyPoints] = useState([]);
  const [loading, setLoading] = useState({
    products: false,
    customers: false,
    agencyPoints: false,
  });
  const [error, setError] = useState(null);
  const [newProduct, setNewProduct] = useState({
    productCategoryId: null,
    productsId: null,
    quantityAssigned: 1,
    unitPrice: 0,
  });

  // Dummy data for customers
  const dummyCustomers = [
    { id: 1, name: "Rahul Jadhav", address: "Flat 102, Kothrud, Pune" },
    { id: 2, name: "Sneha Kulkarni", address: "Bungalow 3, Baner Road, Pune" },
    { id: 3, name: "Ajay Deshmukh", address: "A1, Viman Nagar, Pune" },
    { id: 4, name: "Neha Patil", address: "Plot 45, Hadapsar, Pune" },
    { id: 5, name: "Vikram Joshi", address: "Block C, Wakad, Pune" },
  ];

  // Dummy data for agency points
  const dummyAgencyPoints = [
    { id: 1, name: "Kothrud Main Depot", location: "Kothrud" },
    { id: 2, name: "Baner Delivery Hub", location: "Baner" },
    { id: 3, name: "Hadapsar Distribution Center", location: "Hadapsar" },
    { id: 4, name: "Wakad Logistics Station", location: "Wakad" },
    { id: 5, name: "Viman Nagar Supply Unit", location: "Viman Nagar" },
  ];

  // Fetch products
  const fetchProducts = useCallback(async () => {
    try {
      setLoading((prev) => ({ ...prev, products: true }));
      const token = sessionStorage.getItem("token");
      const response = await fetch(`${base_Url}/masters/getProductList`, {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
          Authorization: `Bearer ${token}`,
        },
        body: JSON.stringify({
          page: 0,
          size: 100,
        }),
      });
      const data = await response.json();
      setProducts(data.result);
      setCustomers(dummyCustomers); // Replace with actual API call
      setAgencyPoints(dummyAgencyPoints); // Replace with actual API call
    } catch (err) {
      setError("Failed to fetch products");
      console.error(err);
    } finally {
      setLoading((prev) => ({ ...prev, products: false }));
    }
  }, []);

  // Fetch customers and agency points (you'll need to implement these API calls)
  // const fetchCustomers = useCallback(async () => {...});
  // const fetchAgencyPoints = useCallback(async () => {...});

  useEffect(() => {
    if (show) {
      fetchProducts();
      // fetchCustomers();
      // fetchAgencyPoints();
    }
  }, [show, fetchProducts]);

  const handleSubmit = (e) => {
    e.preventDefault();
    if (formData.dailyAssignmentDetailsRequestDtos.length === 0) {
      setError("Please add at least one product");
      return;
    }

    if (
      (!formData.isCustomer && !formData.isPoint) ||
      (formData.isCustomer && !formData.customerId) ||
      (formData.isPoint && !formData.agencyPointId)
    ) {
      setError("Please select either a customer or agency point");
      return;
    }

    setError(null);
    onSubmit({
      ...formData,
      createdBy: userId,
      lastModifiedBy: userId,
      assignedById: { id: userId },
    });
  };

  const handleAddProduct = () => {
    if (
      !newProduct.productsId ||
      !newProduct.quantityAssigned ||
      newProduct.quantityAssigned <= 0
    ) {
      setError("Please select a product and enter valid quantity");
      return;
    }

    const selectedProduct = products.find(
      (p) => p.Id === newProduct.productsId
    );
    const productToAdd = {
      productCategoryId: { id: selectedProduct.categoryId },
      productsId: { id: selectedProduct.Id },
      quantityAssigned: newProduct.quantityAssigned,
      unitPrice: selectedProduct.productPrice,
    };

    setFormData((prev) => ({
      ...prev,
      dailyAssignmentDetailsRequestDtos: [
        ...prev.dailyAssignmentDetailsRequestDtos,
        productToAdd,
      ],
    }));

    setNewProduct({
      productCategoryId: null,
      productsId: null,
      quantityAssigned: 1,
      unitPrice: 0,
    });
  };

  const handleRemoveProduct = (index) => {
    setFormData((prev) => ({
      ...prev,
      dailyAssignmentDetailsRequestDtos:
        prev.dailyAssignmentDetailsRequestDtos.filter((_, i) => i !== index),
    }));
  };

  const handleRecipientTypeChange = (type) => {
    setFormData((prev) => ({
      ...prev,
      isCustomer: type === "customer",
      isPoint: type === "point",
      customerId: type === "customer" ? prev.customerId : null,
      agencyPointId: type === "point" ? prev.agencyPointId : null,
    }));
  };

  return (
    <Modal show={show} onHide={onHide} size="lg">
      <Modal.Header closeButton>
        <Modal.Title>Create New Assignment</Modal.Title>
      </Modal.Header>
      <Form onSubmit={handleSubmit}>
        <Modal.Body>
          {error && <Alert variant="danger">{error}</Alert>}

          <Form.Group className="mb-3">
            <Form.Label>Assignment For</Form.Label>
            <div>
              <Form.Check
                inline
                label="Customer"
                type="radio"
                name="recipientType"
                checked={formData.isCustomer}
                onChange={() => handleRecipientTypeChange("customer")}
              />
              <Form.Check
                inline
                label="Agency Point"
                type="radio"
                name="recipientType"
                checked={formData.isPoint}
                onChange={() => handleRecipientTypeChange("point")}
              />
            </div>
          </Form.Group>

          {formData.isCustomer ? (
            <Form.Group className="mb-3">
              <Form.Label>Customer ID</Form.Label>
              <Form.Control
                type="text"
                value={formData.customerId?.id||""}
                onChange={(e) =>
                  setFormData({
                    ...formData,
                    customerId: { id: e.target.value },
                  })
                }
              />
            </Form.Group>
          ) : (
            <Form.Group className="mb-3">
              <Form.Label>Agency Point ID</Form.Label>
              <Form.Control
                type="text"
                value={formData.agencyPointId?.id||""}
                onChange={(e) =>
                  setFormData({
                    ...formData,
                    agencyPointId: { id: e.target.value },
                  })
                }
              />
            </Form.Group>
          )}

          <hr />
          <h5>Add Products</h5>

          <Row className="mb-3">
            <Col md={5}>
              <Form.Group>
                <Form.Label>Product</Form.Label>
                <Form.Select
                  value={newProduct.productsId || ""}
                  onChange={(e) => {
                    const productId = e.target.value;
                    const selectedProduct = products.find(
                      (p) => p.Id == productId
                    );
                    setNewProduct({
                      ...newProduct,
                      productsId: productId ? parseInt(productId) : null,
                      productCategoryId: selectedProduct?.categoryId || null,
                      unitPrice: selectedProduct?.productPrice || 0,
                    });
                  }}
                  disabled={loading.products}
                >
                  <option value="">Select Product</option>
                  {products.map((product) => (
                    <option key={product.Id} value={product.Id}>
                      {product.productName} - ₹{product.productPrice}
                    </option>
                  ))}
                </Form.Select>
              </Form.Group>
            </Col>
            <Col md={3}>
              <Form.Group>
                <Form.Label>Quantity</Form.Label>
                <Form.Control
                  type="number"
                  min="1"
                  value={newProduct.quantityAssigned}
                  onChange={(e) =>
                    setNewProduct({
                      ...newProduct,
                      quantityAssigned: parseInt(e.target.value) || 0,
                    })
                  }
                />
              </Form.Group>
            </Col>
            <Col md={2}>
              <Form.Group>
                <Form.Label>Price</Form.Label>
                <Form.Control
                  type="number"
                  value={newProduct.unitPrice}
                  readOnly
                />
              </Form.Group>
            </Col>
            <Col md={2} className="d-flex align-items-end">
              <Button
                variant="primary"
                onClick={handleAddProduct}
                disabled={!newProduct.productsId}
              >
                Add
              </Button>
            </Col>
          </Row>

          {formData.dailyAssignmentDetailsRequestDtos.length > 0 && (
            <Table striped bordered hover>
              <thead>
                <tr>
                  <th>Product</th>
                  <th>Quantity</th>
                  <th>Unit Price</th>
                  <th>Total</th>
                  <th>Action</th>
                </tr>
              </thead>
              <tbody>
                {formData.dailyAssignmentDetailsRequestDtos.map(
                  (item, index) => {
                    const product = products.find(
                      (p) => p.Id === item.productsId.id
                    );
                    return (
                      <tr key={index}>
                        <td>{product?.productName || "Unknown"}</td>
                        <td>{item.quantityAssigned}</td>
                        <td>₹{item.unitPrice}</td>
                        <td>₹{item.quantityAssigned * item.unitPrice}</td>
                        <td>
                          <Button
                            variant="danger"
                            size="sm"
                            onClick={() => handleRemoveProduct(index)}
                          >
                            Remove
                          </Button>
                        </td>
                      </tr>
                    );
                  }
                )}
              </tbody>
            </Table>
          )}
        </Modal.Body>
        <Modal.Footer>
          <Button variant="secondary" onClick={onHide}>
            Cancel
          </Button>
          <Button
            variant="primary"
            type="submit"
            disabled={formData.dailyAssignmentDetailsRequestDtos.length === 0}
          >
            Create Assignment
          </Button>
        </Modal.Footer>
      </Form>
    </Modal>
  );
};
export default CreateAssignmentModal;
