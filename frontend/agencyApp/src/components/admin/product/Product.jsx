import React, { useState, useEffect, useCallback} from "react";
import { motion, AnimatePresence } from "framer-motion";
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import { faPlus } from "@fortawesome/free-solid-svg-icons";
import ProductsTable from "./productModels/ProductsTable";
import LoadingSpinner from "../../common/LoadingSpinner";
import ErrorAlert from "../../common/ErrorAlert";  
import SearchBar from "../../common/SearchBar";
import ActionButton from "../../common/ActionButton";
import EditProductForm from "./productModels/EditProductForm";
import AddProductForm from "./productModels/AddProductForm";
import Pagination from "../../common/Pagination";  
import ConfirmationModal from "../../common/ConfirmationModal";
import { toast, ToastContainer } from "react-toastify";

const Team = () => {
  // State management (same as before)
  
  // Fetch function (same as before)
  const PAGE_SIZE = 5;
  const MAX_VISIBLE_PAGES = 5;
  // Filtered products (same as before)
  const base_Url = import.meta.env.VITE_API_URL || ""
  // Handlers (same as before)
  // State management
    const [products, setProducts] = useState([]);
      const [searchTerm, setSearchTerm] = useState("");
      const [selectedProduct, setSelectedProduct] = useState(null);
      const [isEditing, setIsEditing] = useState(false);
      const [isAdding, setIsAdding] = useState(false);
      const [showDeleteModal, setShowDeleteModal] = useState(false);
      const [productToDelete, setproductToDelete] = useState(null);
      const [loading, setLoading] = useState(true);
      const [error, setError] = useState(null);
      const [pagination, setPagination] = useState({
        currentPage: 0,
        totalPages: 0,
        totalElements: 0,
      });
    
      // Fetch team products with pagination
      const fetchProducts = useCallback(async (page = 0) => {
        const token = sessionStorage.getItem("token");
        setLoading(true);
        try {
          const response = await fetch(`${base_Url}/masters/getProductList`, {
            method: "POST",
            headers: {
              "Content-Type": "application/json",
              Authorization: `Bearer ${token}`,
            },
            body: JSON.stringify({
              page,
              size: PAGE_SIZE,
            }),
          });
      
          if (!response.ok)
            throw new Error(`HTTP error! status: ${response.status}`);
      
          const data = await response.json();
      
          setProducts(
            data.result.map((product) => ({
              id: product.Id,
              productName: product.productName,
              categoryId: product.categoryId,
              categoryName: product.categoryName,
              isActive: product.isActive,
            }))
          );
      
          setPagination({
            currentPage: page,
            totalPages: Math.ceil(data.count / PAGE_SIZE),
            totalElements: data.count,
          });
        } catch (err) {
          setError(err.message);
        } finally {
          setLoading(false);
        }
      }, []);
      
    
      useEffect(() => {
        fetchProducts();
      }, [fetchProducts]);
    
      // Filter products based on search term
      const filteredProducts = products.filter((product) =>
        product.productName?.toLowerCase().includes(searchTerm.toLowerCase())
      );
      
      const headers =
      products.length > 0
        ? Object.keys(products[0]).filter((key) => key !== "id")
        : [];
    
    
      // Action handlers
      const handleEdit = (product) => {
        setSelectedProduct(product);
        setIsEditing(true);
        setIsAdding(false);
      };
    
      const confirmDelete = (product) => {
        setproductToDelete(product);
        setShowDeleteModal(true);
      };
    
      const handleDelete = async () => {
        if (!productToDelete?.id) return;
    
        const token = sessionStorage.getItem("token");
        try {
          const response = await fetch(
            `${base_Url}/masters/deleteProductById/${productToDelete.id}`,
            {
              method: "DELETE",
              headers: {
                Authorization: `Bearer ${token}`,
              },
            }
          );
          if (!response.ok)
            throw new Error(`HTTP error! status: ${response.status}`);
          if (response.status === 200) {
            toast.warning("Product deleted successfully")
          } else if (response.status === 400) {
            toast.error("Failed to delete product")
          }
          await fetchProducts(pagination.currentPage);
          setShowDeleteModal(false);
          setproductToDelete(null);
        } catch (err) {
          setError(err.message);
        }
      };
    
      const handleSave = async (updatedProduct) => {
        const token = sessionStorage.getItem("token");
        try {
          const response = await fetch(
            `${base_Url}/masters/saveAndUpdateProduct`,
            {
              method: "POST",
              headers: {
                "Content-Type": "application/json",
                Authorization: `Bearer ${token}`,
              },
              body: JSON.stringify(updatedProduct),
            }
          );
          if (!response.ok)
            throw new Error(`HTTP error! status: ${response.status}`);
    
          await fetchProducts(pagination.currentPage);
          setIsEditing(false);
        } catch (err) {
          setError(err.message);
        }
      };
    
      const handleAdd = async (newProduct) => {
        const token = sessionStorage.getItem("token");
        try {
          const response = await fetch(
            `${base_Url}/masters/saveAndUpdateProduct`,
            {
              method: "POST",
              headers: {
                "Content-Type": "application/json",
                Authorization: `Bearer ${token}`,
              },
              body: JSON.stringify(newProduct),
            }
          );
    
          if (!response.ok)
            throw new Error(`HTTP error! status: ${response.status}`);
          if (response.status === 200) {
            toast.success("Product added successfully")
          } else if (response.status === 400) {
            toast.error("Failed to add product")
          } else {
            toast.error("Failed to add product")
          }
          await fetchProducts(0);
          setSearchTerm("");
          setIsAdding(false);
        } catch (err) {
          setError(err.message);
        }
      };
    
      // Pagination handlers
      const handlePageChange = (newPage) => {
        if (newPage >= 0 && newPage < pagination.totalPages) {
          fetchProducts(newPage);
        }
      };
    
      const renderPagination = () => {
        const { currentPage, totalPages } = pagination;
        const pages = [];
    
        let startPage = Math.max(
          0,
          currentPage - Math.floor(MAX_VISIBLE_PAGES / 2)
        );
        let endPage = Math.min(totalPages - 1, startPage + MAX_VISIBLE_PAGES - 1);
    
        if (endPage - startPage + 1 < MAX_VISIBLE_PAGES) {
          startPage = Math.max(0, endPage - MAX_VISIBLE_PAGES + 1);
        }
    
        for (let i = startPage; i <= endPage; i++) {
          pages.push(
            <motion.button
              key={i}
              onClick={() => handlePageChange(i)}
              className={`btn ${
                i === currentPage ? "btn-primary" : "btn-outline-primary"
              }`}
              style={{
                minWidth: "40px",
                margin: "0 2px",
                borderRadius: "6px",
              }}
              whileHover={{ scale: 1.05 }}
              whileTap={{ scale: 0.95 }}
            >
              {i + 1}
            </motion.button>
          );
        }
    
        return (
          <div className="d-flex flex-wrap justify-content-center align-items-center mt-4 gap-2">
            <motion.button
              onClick={() => handlePageChange(0)}
              disabled={currentPage === 0}
              className="btn btn-outline-primary"
              style={{
                minWidth: "40px",
                borderRadius: "6px",
              }}
              whileHover={{ scale: 1.05 }}
              whileTap={{ scale: 0.95 }}
            >
              <FontAwesomeIcon icon={faAnglesLeft} />
            </motion.button>
            <motion.button
              onClick={() => handlePageChange(currentPage - 1)}
              disabled={currentPage === 0}
              className="btn btn-outline-primary"
              style={{
                minWidth: "40px",
                borderRadius: "6px",
              }}
              whileHover={{ scale: 1.05 }}
              whileTap={{ scale: 0.95 }}
            >
              <FontAwesomeIcon icon={faAngleLeft} />
            </motion.button>
    
            {startPage > 0 && <span className="mx-1 text-muted">...</span>}
    
            {pages}
    
            {endPage < totalPages - 1 && (
              <span className="mx-1 text-muted">...</span>
            )}
    
            <motion.button
              onClick={() => handlePageChange(currentPage + 1)}
              disabled={currentPage >= totalPages - 1}
              className="btn btn-outline-primary"
              style={{
                minWidth: "40px",
                borderRadius: "6px",
              }}
              whileHover={{ scale: 1.05 }}
              whileTap={{ scale: 0.95 }}
            >
              <FontAwesomeIcon icon={faAngleRight} />
            </motion.button>
            <motion.button
              onClick={() => handlePageChange(totalPages - 1)}
              disabled={currentPage >= totalPages - 1}
              className="btn btn-outline-primary"
              style={{
                minWidth: "40px",
                borderRadius: "6px",
              }}
              whileHover={{ scale: 1.05 }}
              whileTap={{ scale: 0.95 }}
            >
              <FontAwesomeIcon icon={faAnglesRight} />
            </motion.button>
    
            <div className="d-flex flex-wrap justify-content-center gap-2 mt-2 mt-md-0">
              <span className="text-muted">
                Page {currentPage + 1} of {totalPages}
              </span>
              <span className="text-muted">
                Total Records: {pagination.totalElements}
              </span>
            </div>
          </div>
        );
      };
    

  if (loading && !isEditing && !isAdding) {
    return <LoadingSpinner />;
  }

  if (error) {
    return <ErrorAlert error={error} onDismiss={() => setError(null)} />;
  }

  return (
    <motion.div
      initial={{ opacity: 0 }}
      animate={{ opacity: 1 }}
      className="container-fluid p-3 p-md-4"
      style={{
        background: "linear-gradient(180deg, #e8ecf1 0%, #dcd9e6 100%)",
        borderRadius: "12px",
        boxShadow: "0 4px 20px rgba(0,0,0,0.05)",
        minHeight: "calc(100vh - 66px)",
      }}
    >
      {!isEditing && !isAdding && (
        <div className="d-flex flex-column flex-md-row align-items-center justify-content-between mb-4 gap-3">
          <SearchBar searchTerm={searchTerm} setSearchTerm={setSearchTerm} />
          
          <ActionButton
            icon={faPlus}
            label="Add Product"
            onClick={() => {
              setSelectedProduct(null);
              setIsEditing(false);
              setIsAdding(true);
            }}
            bgColor="linear-gradient(90deg, #2b5876 0%, #4e4376 100%)"
            textColor="white"
          />
        </div>
      )}

      <AnimatePresence mode="wait">
        {isEditing && selectedProduct ? (
          <motion.div
            key="edit-form"
            initial={{ opacity: 0, y: 20 }}
            animate={{ opacity: 1, y: 0 }}
            exit={{ opacity: 0, y: -20 }}
            transition={{ duration: 0.2 }}
          >
            <EditProductForm
              product={selectedProduct}
              onSave={handleSave}
              onCancel={() => setIsEditing(false)}
            />
          </motion.div>
        ) : isAdding ? (
          <motion.div
            key="add-form"
            initial={{ opacity: 0, y: 20 }}
            animate={{ opacity: 1, y: 0 }}
            exit={{ opacity: 0, y: -20 }}
            transition={{ duration: 0.2 }}
          >
            <AddProductForm
              onAdd={handleAdd}
              onCancel={() => setIsAdding(false)}
            />
          </motion.div>
        ) : (
          <motion.div
            key="table-view"
            initial={{ opacity: 0 }}
            animate={{ opacity: 1 }}
            exit={{ opacity: 0 }}
          >
            <ProductsTable
              products={filteredProducts}
              headers={headers}
              onEdit={handleEdit}
              onDelete={confirmDelete}
            />

            {pagination.totalPages > 1 && (
              <Pagination
                currentPage={pagination.currentPage}
                totalPages={pagination.totalPages}
                totalElements={pagination.totalElements}
                onPageChange={handlePageChange}
              />
            )}
          </motion.div>
        )}
      </AnimatePresence>

      <ConfirmationModal
        isOpen={showDeleteModal}
        onClose={() => setShowDeleteModal(false)}
        onConfirm={handleDelete}
        title="Confirm Deletion"
        message={`Are you sure you want to delete ${productToDelete?.["productName"]}?`}
      />
    </motion.div>
  );
};

export default Team;