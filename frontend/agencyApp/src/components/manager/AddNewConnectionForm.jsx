import React, { useState, useEffect } from 'react';
import { Modal, Button, Form, Row, Col, Table } from 'react-bootstrap';
import { FiX } from 'react-icons/fi';
import { useAuth } from '../../auth/AuthProvider';
import {toast} from 'react-toastify';

const AddNewConnectionForm = ({ onSuccess, onClose }) => {
  const { user } = useAuth();
  const [formData, setFormData] = useState({
    customerId: { id: '' }, // Changed to empty string initially
    isNewConnection: true,
    isDBC: false,
    isInventoryBuy: false,
    isCash: true,
    cashAmount: 0,
    isOnline: false,
    bankAccountId: { id: null },
    onlineAmount: null,
    onlinePhotoPath: "",
    createdBy: user.userId,
    lastModifiedBy: user.userId,
    newConnectionDetailsDtoList: []
  });

  const base_Url = import.meta.env.VITE_API_URL || "";
  const token = sessionStorage.getItem("token");
  const [products, setProducts] = useState([]);
  const [selectedProduct, setSelectedProduct] = useState({
    productsId: { id: '' },
    quantity: 0,
    unitPrice: 0
  });
  const [loadingProducts, setLoadingProducts] = useState(false);

  useEffect(() => {
    const fetchProducts = async () => {
      setLoadingProducts(true);
      try {
        const response = await fetch(`${base_Url}/masters/getProductList`, {
          method: 'POST',
          headers: {
            'accept': '*/*',
            'Authorization': `Bearer ${token}`,
            'Content-Type': 'application/json'
          },
          body: JSON.stringify({
            page: 0,
            size: 100
          })
        });
        const data = await response.json();
        setProducts(data.result || []);
      } catch (error) {
        console.error('Error fetching products:', error);
      } finally {
        setLoadingProducts(false);
      }
    };

    fetchProducts();
  }, []);

  const handleChange = (e) => {
    const { name, value, type, checked } = e.target;
    setFormData(prev => ({
      ...prev,
      [name]: type === 'checkbox' ? checked : value
    }));
  };

  const handleConnectionTypeChange = (type) => {
    setFormData(prev => ({
      ...prev,
      isNewConnection: type === 'new',
      isDBC: type === 'dbc',
      isInventoryBuy: type === 'inventory'
    }));
  };

  const handlePaymentMethodChange = (method) => {
    setFormData(prev => ({
      ...prev,
      isCash: method === 'cash',
      isOnline: method === 'online'
    }));
  };

  const handleProductSelect = (e) => {
    const productId = e.target.value;
    const product = products.find(p => p.Id == productId);
    setSelectedProduct(prev => ({
      ...prev,
      productsId: { id: productId },
      unitPrice: product?.productPrice || 0
    }));
  };

  const handleProductQuantityChange = (e) => {
    const quantity = e.target.value;
    setSelectedProduct(prev => ({
      ...prev,
      quantity: quantity
    }));
  };

  const addProduct = () => {
    if (selectedProduct.productsId.id && selectedProduct.quantity > 0) {
      setFormData(prev => ({
        ...prev,
        newConnectionDetailsDtoList: [...prev.newConnectionDetailsDtoList, selectedProduct]
      }));
      setSelectedProduct({
        productsId: { id: '' },
        quantity: 0,
        unitPrice: 0
      });
    }
  };

  const removeProduct = (index) => {
    setFormData(prev => ({
      ...prev,
      newConnectionDetailsDtoList: prev.newConnectionDetailsDtoList.filter((_, i) => i !== index)
    }));
  };

  const handleSubmit = (e) => {
    if (formData.newConnectionDetailsDtoList.length === 0) {
      toast.error("Please add at least one product.");
      
    }
    e.preventDefault();
    onSuccess(formData);
  };

  return (
    <Modal show={true} onHide={onClose} size="lg" centered>
      <Modal.Header closeButton>
        <Modal.Title>Add New Connection</Modal.Title>
      </Modal.Header>
      <Modal.Body>
        <Form onSubmit={handleSubmit}>
          {/* Customer ID Field */}
          <Row className="mb-3">
            <Col md={6}>
              <Form.Group>
                <Form.Label>Customer ID</Form.Label>
                <Form.Control
                  type="text"
                  value={formData.customerId.id}
                  onChange={(e) => setFormData(prev => ({
                    ...prev,
                    customerId: { id: e.target.value }
                  }))}
                  required
                />
              </Form.Group>
            </Col>
          </Row>

          {/* Connection Type Radio Group */}
          <Form.Group className="mb-3">
            <Form.Label>Connection Type</Form.Label>
            <div>
              <Form.Check
                inline
                type="radio"
                label="New Connection"
                name="connectionType"
                id="newConnection"
                checked={formData.isNewConnection}
                onChange={() => handleConnectionTypeChange('new')}
              />
              <Form.Check
                inline
                type="radio"
                label="DBC"
                name="connectionType"
                id="dbc"
                checked={formData.isDBC}
                onChange={() => handleConnectionTypeChange('dbc')}
              />
              <Form.Check
                inline
                type="radio"
                label="Inventory Buy"
                name="connectionType"
                id="inventoryBuy"
                checked={formData.isInventoryBuy}
                onChange={() => handleConnectionTypeChange('inventory')}
              />
            </div>
          </Form.Group>

          {/* Payment Method Radio Group */}
          <Form.Group className="mb-3">
            <Form.Label>Payment Method</Form.Label>
            <div>
              <Form.Check
                inline
                type="radio"
                label="Cash"
                name="paymentMethod"
                id="cash"
                checked={formData.isCash}
                onChange={() => handlePaymentMethodChange('cash')}
              />
              <Form.Check
                inline
                type="radio"
                label="Online"
                name="paymentMethod"
                id="online"
                checked={formData.isOnline}
                onChange={() => handlePaymentMethodChange('online')}
              />
            </div>
          </Form.Group>

          {/* Payment Details */}
          {formData.isCash && (
            <Row className="mb-3">
              <Col md={6}>
                <Form.Group>
                  <Form.Label>Cash Amount (₹)</Form.Label>
                  <Form.Control
                    type="number"
                    name="cashAmount"
                    value={formData.cashAmount}
                    onChange={handleChange}
                    step="0.01"
                    min="0"
                    required
                  />
                </Form.Group>
              </Col>
            </Row>
          )}

          {formData.isOnline && (
            <Row className="mb-3">
              <Col md={6}>
                <Form.Group>
                  <Form.Label>Online Amount (₹)</Form.Label>
                  <Form.Control
                    type="number"
                    name="onlineAmount"
                    value={formData.onlineAmount}
                    onChange={handleChange}
                    step="0.01"
                    min="0"
                    required
                  />
                </Form.Group>
              </Col>
              <Col md={6}>
                <Form.Group>
                  <Form.Label>Bank Account ID</Form.Label>
                  <Form.Control
                    type="text"
                    name="bankAccountId.id"
                    value={formData.bankAccountId.id}
                    onChange={(e) => setFormData(prev => ({
                      ...prev,
                      bankAccountId: { id: e.target.value }
                    }))}
                    required
                  />
                </Form.Group>
              </Col>
            </Row>
          )}

          {/* Products Section */}
          <h5>Products</h5>
          {loadingProducts ? (
            <p>Loading products...</p>
          ) : (
            <>
              <Row className="mb-3">
                <Col md={5}>
                  <Form.Group>
                    <Form.Label>Product</Form.Label>
                    <Form.Select
                      value={selectedProduct.productsId.id}
                      onChange={handleProductSelect}
                    >
                      <option value="">Select a product</option>
                      {products.map(product => (
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
                      value={selectedProduct.quantity}
                      onChange={handleProductQuantityChange}
                      min={formData.newConnectionDetailsDtoList.length > 0 ? 0 : 1}
                      required
                    />
                  </Form.Group>
                </Col>
                <Col md={2}>
                  <Form.Group>
                    <Form.Label>Unit Price (₹)</Form.Label>
                    <Form.Control
                      type="number"
                      value={selectedProduct.unitPrice}
                      readOnly
                    />
                  </Form.Group>
                </Col>
                <Col md={2} className="d-flex align-items-end">
                  <Button variant="primary" onClick={addProduct}>
                    Add
                  </Button>
                </Col>
              </Row>

              {formData.newConnectionDetailsDtoList.length > 0 && (
                <Table striped bordered hover className="mb-3">
                  <thead>
                    <tr>
                      <th>Product</th>
                      <th>Quantity</th>
                      <th>Unit Price (₹)</th>
                      <th>Total (₹)</th>
                      <th>Action</th>
                    </tr>
                  </thead>
                  <tbody>
                    {formData.newConnectionDetailsDtoList.map((item, index) => {
                      const product = products.find(p => p.Id == item.productsId.id);
                      const total = item.quantity * item.unitPrice;
                      return (
                        <tr key={index}>
                          <td>{product?.productName || 'Unknown Product'}</td>
                          <td>{item.quantity}</td>
                          <td>{item.unitPrice.toFixed(2)}</td>
                          <td>{total.toFixed(2)}</td>
                          <td>
                            <Button
                              variant="danger"
                              size="sm"
                              onClick={() => removeProduct(index)}
                            >
                              <FiX />
                            </Button>
                          </td>
                        </tr>
                      );
                    })}
                  </tbody>
                </Table>
              )}
            </>
          )}

          <div className="d-flex justify-content-end gap-2 mt-4">
            <Button variant="secondary" onClick={onClose}>
              Cancel
            </Button>
            <Button variant="primary" type="submit">
              Save Connection
            </Button>
          </div>
        </Form>
      </Modal.Body>
    </Modal>
  );
};

export default AddNewConnectionForm;