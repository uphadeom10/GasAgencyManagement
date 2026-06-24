import { useState, useMemo } from "react";
import { motion } from "framer-motion";
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import { faEdit } from "@fortawesome/free-solid-svg-icons";

const InventoryTable = ({ inventories, headers, onEdit }) => {
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

  const sortedInventories = useMemo(() => {
    const sorted = [...inventories];
    if (sortConfig.key) {
      sorted.sort((a, b) => {
        const aValue = a[sortConfig.key];
        const bValue = b[sortConfig.key];

        if (typeof aValue === "string" && typeof bValue === "string") {
          return sortConfig.direction === "asc"
            ? aValue.localeCompare(bValue)
            : bValue.localeCompare(aValue);
        }

        return sortConfig.direction === "asc" ? aValue - bValue : bValue - aValue;
      });
    }
    return sorted;
  }, [inventories, sortConfig]);

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
            <th className="fw-medium py-3 px-3">S.No</th>

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
          {sortedInventories.length > 0 ? (
            sortedInventories.map((inventory, index) => (
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
                    {inventory[header]}
                  </td>
                ))}

                <td className="py-3 px-3">
                  <div className="d-flex justify-content-center gap-2">
                    <motion.button
                      className="btn btn-sm"
                      onClick={() => onEdit(inventory)}
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
                  </div>
                </td>
              </motion.tr>
            ))
          ) : (
            <tr>
              <td colSpan={headers.length + 2} className="text-center py-5 text-muted">
                <div
                  className="position-relative text-center"
                  style={{ width: "300px", margin: "0 auto" }}
                >
                  <img
                    src={path}
                    alt="no data"
                    className="img-fluid rounded"
                    style={{ opacity: 1 }}
                  />
                  <div
                    className="position-absolute top-50 start-50 translate-middle text-dark fw-bold"
                    style={{ fontSize: "1rem", marginLeft: "45px" }}
                  >
                    No data available
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

export default InventoryTable;
