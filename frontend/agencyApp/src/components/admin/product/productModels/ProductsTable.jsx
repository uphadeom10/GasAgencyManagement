import React, { useState, useMemo } from "react";
import { motion } from "framer-motion";
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import { faEdit, faTrash } from "@fortawesome/free-solid-svg-icons";
import { faPlus } from "@fortawesome/free-solid-svg-icons";
import StatusBadge from "../../../common/StatusBadge"; // Assuming this is correct

const ProductsTable = ({ products, headers, onEdit, onDelete }) => {
  const [sortConfig, setSortConfig] = useState({ key: null, direction: "asc" });
  const path = "../src/assets/no-data.svg";

  const handleSort = (key) => {
    setSortConfig((prevConfig) => {
      if (prevConfig.key === key) {
        return {
          key,
          direction: prevConfig.direction === "asc" ? "desc" : "asc",
        };
      } else {
        return { key, direction: "asc" };
      }
    });
  };

  const sortedProducts = useMemo(() => {
    const sorted = [...products];
    if (sortConfig.key) {
      sorted.sort((a, b) => {
        const aValue = a[sortConfig.key];
        const bValue = b[sortConfig.key];

        if (typeof aValue === "string" && typeof bValue === "string") {
          return sortConfig.direction === "asc"
            ? aValue.localeCompare(bValue)
            : bValue.localeCompare(aValue);
        }

        if (typeof aValue === "boolean" && typeof bValue === "boolean") {
          return sortConfig.direction === "asc"
            ? Number(aValue) - Number(bValue)
            : Number(bValue) - Number(aValue);
        }

        return sortConfig.direction === "asc" ? aValue - bValue : bValue - aValue;
      });
    }
    return sorted;
  }, [products, sortConfig]);

  return (
    <div className="table-responsive rounded-3 overflow-y">
      <table className="table table-hover align-middle mb-0">
        <thead className="sticky-top text-center">
          <tr
            style={{
              background: "linear-gradient(90deg, #2b5876 0%, #4e4376 100%)",
              color: "white",
            }}
          >
            <th className="fw-medium py-3 px-3">Sr.No</th>

            {headers.map((header) => (
              <th
                key={header}
                className="fw-medium py-3 px-3"
                style={{ whiteSpace: "nowrap", cursor: "pointer" }}
                onClick={() => handleSort(header)}
              >
                {header.charAt(0).toUpperCase() +
                  header.slice(1).replace(/([A-Z])/g, " $1")}
                {sortConfig.key === header && (
                  <span style={{ marginLeft: "5px" }}>
                    {sortConfig.direction === "asc" ? "▲" : "▼"}
                  </span>
                )}
              </th>
            ))}

            <th className="fw-medium py-3 px-3 text-center">Actions</th>
          </tr>
        </thead>

        <tbody>
          {sortedProducts.length > 0 ? (
            sortedProducts.map((product, index) => (
              <motion.tr
                key={index}
                initial={{ opacity: 0, y: 10 }}
                animate={{ opacity: 1, y: 0 }}
                transition={{ duration: 0.2, delay: index * 0.05 }}
                className="border-bottom border-light"
                whileHover={{
                  backgroundColor: "rgba(43, 88, 118, 0.03)",
                }}
              >
                <td className="py-3 px-3 text-center">{index + 1}</td>

                {headers.map((header) => (
                  <td key={header} className="py-3 px-3 text-center">
                    {header === "isActive" ? (
                      <StatusBadge status={product[header]} />
                    ) : (
                      product[header]
                    )}
                  </td>
                ))}

                <td className="py-3 px-3">
                  <div className="d-flex justify-content-center gap-2">
                    <motion.button
                      className="btn btn-sm"
                      onClick={() => onEdit(product)}
                      style={{
                        background: "rgba(43, 88, 118, 0.1)",
                        color: "#2b5876",
                        borderRadius: "6px",
                        padding: "5px 12px",
                        border: "none",
                        whiteSpace: "nowrap",
                      }}
                      whileHover={{
                        backgroundColor: "rgba(43, 88, 118, 0.2)",
                      }}
                      whileTap={{ scale: 0.95 }}
                    >
                      <FontAwesomeIcon icon={faEdit} className="me-1" />
                      Edit
                    </motion.button>

                    <motion.button
                      className="btn btn-sm"
                      onClick={() => onDelete(product)}
                      style={{
                        background: "rgba(220, 53, 69, 0.1)",
                        color: "#dc3545",
                        borderRadius: "6px",
                        padding: "5px 12px",
                        border: "none",
                        whiteSpace: "nowrap",
                      }}
                      whileHover={{
                        backgroundColor: "rgba(220, 53, 69, 0.2)",
                      }}
                      whileTap={{ scale: 0.95 }}
                    >
                      <FontAwesomeIcon icon={faTrash} className="me-1" />
                      Delete
                    </motion.button>
                  </div>
                </td>
              </motion.tr>
            ))
          ) : (
            <tr>
              <td
                colSpan={headers.length + 2}
                className="text-center py-5 text-muted"
              >
                <div
                  className="position-relative text-center"
                  style={{ width: "250px", margin: "0 auto" }}
                >
                  <img
                    src={path}
                    alt="no data"
                    className="img-fluid rounded"
                    style={{ opacity: 1 }}
                  />
                  <div
                    className="position-absolute top-50 start-50 translate-middle text-dark fw-bold"
                    style={{ fontSize: "1rem", marginLeft: "40px" }}
                  >
                    No products found
                  </div>
                </div>
              </td>
            </tr>
          )}
        </tbody>
      </table>
    </div>
  );
};

export default ProductsTable;
