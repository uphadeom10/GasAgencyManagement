import React, { useState, useEffect } from "react";
import { motion } from "framer-motion";
import { Info } from "lucide-react";
import { useAuth } from "../../../../auth/AuthProvider";

const AddInventoryForm = ({ onAdd, onCancel }) => {
  const base_url = import.meta.env.VITE_API_URL || "";
  const { user } = useAuth();
  const loggedInUserId = user?.userId;

  const [categories, setCategories] = useState([]);
  const [products, setProducts] = useState([]);
  const [newInventory, setNewInventory] = useState({
    productCategoryId: "",
    productId: "",
    filled: 0,
    unFilled: 0,
    totalQuantity: 0,
    unitPrice: "",
    isAdded: true,
    isRemoved: false,
    reason: "",
    isNewConnection: false,
    createdBy: loggedInUserId,
    lastModifiedBy: loggedInUserId,
  });

  useEffect(() => {
    const fetchCategories = async () => {
      try {
        const token = sessionStorage.getItem("token");
        const response = await fetch(`${base_url}/masters/getProductCategoryList`, {
          headers: {
            "Content-Type": "application/json",
            Authorization: `Bearer ${token}`,
          },
        });

        if (!response.ok) throw new Error("Failed to fetch categories");
        const data = await response.json();
        setCategories(data.result);
      } catch (error) {
        console.error("Error fetching categories:", error);
      }
    };

    fetchCategories();
  }, []);

  useEffect(() => {
    const fetchProducts = async () => {
      try {
        const token = sessionStorage.getItem("token");
        const response = await fetch(
          `${base_url}/masters/getProductList`,
          {
            method: "POST",
            headers: {
              "Content-Type": "application/json",
              Authorization: `Bearer ${token}`,
            },
            body: JSON.stringify({ page: 0, size: 10 }),
          }
        );
  
        if (!response.ok) throw new Error("Failed to fetch products");
  
        const data = await response.json();
        setProducts(data.result);
      } catch (error) {
        console.error("Error fetching products:", error);
      }
    };
  
    fetchProducts();
  }, []);

  // Calculate totalQuantity whenever filled or unFilled changes
  useEffect(() => {
    setNewInventory(prev => ({
      ...prev,
      totalQuantity: Number(prev.filled) + Number(prev.unFilled)
    }));
  }, [newInventory.filled, newInventory.unFilled]);

  const handleChange = (e) => {
    const { name, value } = e.target;
    
    setNewInventory((prev) => ({
      ...prev,
      [name]: name === "filled" || name === "unFilled" || name === "unitPrice" 
        ? Number(value) 
        : value,
      lastModifiedBy: loggedInUserId,
    }));
  };

  const handleSubmit = (e) => {
    e.preventDefault();
    onAdd(newInventory);
  };

  return (
    <div className="container py-5">
      <motion.div
        className="shadow-card mx-auto"
        initial={{ y: -20, opacity: 0 }}
        animate={{ y: 0, opacity: 1 }}
        transition={{ type: "spring", stiffness: 100 }}
      >
        <h2 className="gradient-text mb-4">Add Inventory</h2>

        <form onSubmit={handleSubmit} autoComplete="off">
          <div className="row g-4">
            <div className="col-md-6">
              <label className="form-label">
                Category <Info size={16} title="Choose the product category." />
              </label>
              <select
                name="productCategoryId"
                className="form-select form-select-lg"
                value={newInventory.productCategoryId}
                onChange={handleChange}
                required
              >
                <option value="">Select Category</option>
                {categories.map((cat) => (
                  <option key={cat.id} value={cat.id}>
                    {cat.category_name}
                  </option>
                ))}
              </select>
            </div>

            <div className="col-md-6">
              <label className="form-label">
                Product <Info size={16} title="Choose the product." />
              </label>
              <select
                name="productId"
                className="form-select form-select-lg"
                value={newInventory.productId}
                onChange={handleChange}
                required
              >
                <option value="">Select Product</option>
                {products
                .filter((product) => product.isActive)
                .map((product) => (
                  <option key={product.Id} value={product.Id}>
                    {product.productName}
                  </option>
                ))}
              </select>
            </div>

            <div className="col-md-4">
              <label className="form-label">
                Filled Quantity <Info size={16} title="Enter the filled quantity." />
              </label>
              <input
                type="number"
                className="form-control form-control-lg"
                name="filled"
                value={newInventory.filled}
                onChange={handleChange}
                min="0"
                required
              />
            </div>

            <div className="col-md-4">
              <label className="form-label">
                Unfilled Quantity <Info size={16} title="Enter the unfilled quantity." />
              </label>
              <input
                type="number"
                className="form-control form-control-lg"
                name="unFilled"
                value={newInventory.unFilled}
                onChange={handleChange}
                min="0"
                required
              />
            </div>

            <div className="col-md-4">
              <label className="form-label">
                Total Quantity <Info size={16} title="Auto-calculated total quantity." />
              </label>
              <input
                type="number"
                className="form-control form-control-lg"
                name="totalQuantity"
                value={newInventory.totalQuantity}
                readOnly
                disabled
              />
            </div>

            <div className="col-md-6">
              <label className="form-label">
                Unit Price <Info size={16} title="Enter the unit price." />
              </label>
              <input
                type="number"
                className="form-control form-control-lg"
                name="unitPrice"
                value={newInventory.unitPrice}
                onChange={handleChange}
                min="0.01"
                step="0.01"
                required
              />
            </div>

           

            <div className="col-12">
              <label className="form-label">
                Reason <Info size={16} title="Enter the reason for adding inventory." />
              </label>
              <textarea
                className="form-control form-control-lg"
                name="reason"
                value={newInventory.reason}
                onChange={handleChange}
                required
                rows="3"
              />
            </div>
          </div>

          <div className="d-flex justify-content-end gap-3 mt-4 pt-4 border-top">
            <motion.button
              type="button"
              className="btn btn-outline-secondary px-4 py-2 rounded"
              onClick={onCancel}
              whileHover={{ scale: 1.03 }}
              whileTap={{ scale: 0.98 }}
            >
              Cancel
            </motion.button>
            <motion.button
              type="submit"
              className="btn btn-gradient px-4 py-2 rounded"
              whileHover={{ scale: 1.03 }}
              whileTap={{ scale: 0.98 }}
            >
              Save
            </motion.button>
          </div>
        </form>
      </motion.div>

      <style>{`
        .gradient-text {
          background: linear-gradient(90deg, #3b82f6, #8b5cf6);
          -webkit-background-clip: text;
          color: transparent;
        }

        .btn-gradient {
          background: linear-gradient(135deg, #3b82f6, #8b5cf6);
          color: white;
          border: none;
        }

        .btn-gradient:hover {
          background: linear-gradient(135deg, #2563eb, #7c3aed);
        }

        .shadow-card {
          background: #fff;
          border-radius: 1.5rem;
          padding: 2rem;
          box-shadow: 0 10px 25px rgba(0, 0, 0, 0.1);
          max-width: 900px;
        }
      `}</style>
    </div>
  );
};

export default AddInventoryForm;