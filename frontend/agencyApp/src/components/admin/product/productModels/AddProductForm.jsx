import React, { useState, useEffect } from "react";
import { motion } from "framer-motion";
import { Info } from "lucide-react";
import { useAuth } from "../../../../auth/AuthProvider";

const AddProductForm = ({ onAdd, onCancel }) => {
  const base_url = import.meta.env.VITE_API_URL || "";
  const { user } = useAuth();
  const loggedInUserId = user?.userId;
  

  const [categories, setCategories] = useState([]);
  const [newProduct, setNewProduct] = useState({
    productName: "",
    productCategoryId: { id: "" },
    isActive: true,
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

  const handleChange = (e) => {
    const { name, value } = e.target;

    if (name === "productCategoryId.id") {
      setNewProduct((prev) => ({
        ...prev,
        productCategoryId: { id: value },
      }));
    } else {
      setNewProduct((prev) => ({
        ...prev,
        [name]: name === "isActive" ? value === "true" : value,
      }));
    }

    setNewProduct((prev) => ({
      ...prev,
      lastModifiedBy: loggedInUserId,
    }));
  };

  const handleSubmit = (e) => {
    e.preventDefault();
    
    onAdd(newProduct);
  };

  return (
    <div className="container py-5">
      <motion.div
        className="shadow-card mx-auto"
        initial={{ y: -20, opacity: 0 }}
        animate={{ y: 0, opacity: 1 }}
        transition={{ type: "spring", stiffness: 100 }}
      >
        <h2 className="gradient-text mb-4">Add New Product</h2>

        <form onSubmit={handleSubmit} autoComplete="off">
          <div className="row g-4">
            <div className="col-md-6">
              <label className="form-label">
                Product Name <Info size={16} title="Enter the product name." />
              </label>
              <input
                type="text"
                className="form-control form-control-lg"
                name="productName"
                value={newProduct.productName}
                onChange={handleChange}
                required
              />
            </div>

            <div className="col-md-6">
              <label className="form-label">
                Category <Info size={16} title="Choose the product category." />
              </label>
              <select
                name="productCategoryId.id"
                className="form-select form-select-lg"
                value={newProduct.productCategoryId.id}
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
              <label className="form-label">Product Status</label>
              <select
                name="isActive"
                className="form-select form-select-lg"
                value={String(newProduct.isActive)}
                onChange={handleChange}
              >
                <option value="true">Active</option>
                <option value="false">Inactive</option>
              </select>
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

export default AddProductForm;
