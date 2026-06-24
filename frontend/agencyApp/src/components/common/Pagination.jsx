import { motion } from "framer-motion";
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import {
  faAngleLeft,
  faAngleRight,
  faAnglesLeft,
  faAnglesRight,
} from "@fortawesome/free-solid-svg-icons";

const Pagination = ({ currentPage, totalPages, totalElements, onPageChange }) => {
  const MAX_VISIBLE_PAGES = 5;

  const renderPageButtons = () => {
    const pages = [];
    let startPage = Math.max(0, currentPage - Math.floor(MAX_VISIBLE_PAGES / 2));
    let endPage = Math.min(totalPages - 1, startPage + MAX_VISIBLE_PAGES - 1);

    // Adjust startPage if we’re near the end
    if (endPage - startPage + 1 < MAX_VISIBLE_PAGES) {
      startPage = Math.max(0, endPage - MAX_VISIBLE_PAGES + 1);
    }

    // ✅ Populate the pages array
    for (let i = startPage; i <= endPage; i++) {
      pages.push(i);
    }

    return pages.map((i) => (
      <motion.button
        key={i}
        onClick={() => onPageChange(i)}
        className={`btn ${i === currentPage ? "btn-primary" : "btn-outline-primary"}`}
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
    ));
  };

  // Calculate startPage and endPage for rendering ellipsis (...)
  const startPage = Math.max(0, currentPage - Math.floor(MAX_VISIBLE_PAGES / 2));
  const endPage = Math.min(totalPages - 1, startPage + MAX_VISIBLE_PAGES - 1);

  return (
    <div className="d-flex flex-wrap justify-content-center align-items-center mt-4 gap-2">
      {/* First Page */}
      <motion.button
        onClick={() => onPageChange(0)}
        disabled={currentPage === 0}
        className="btn btn-outline-primary"
        style={{ minWidth: "40px", borderRadius: "6px" }}
        whileHover={{ scale: 1.05 }}
        whileTap={{ scale: 0.95 }}
      >
        <FontAwesomeIcon icon={faAnglesLeft} />
      </motion.button>

      {/* Previous Page */}
      <motion.button
        onClick={() => onPageChange(currentPage - 1)}
        disabled={currentPage === 0}
        className="btn btn-outline-primary"
        style={{ minWidth: "40px", borderRadius: "6px" }}
        whileHover={{ scale: 1.05 }}
        whileTap={{ scale: 0.95 }}
      >
        <FontAwesomeIcon icon={faAngleLeft} />
      </motion.button>

      {/* Ellipsis before pages */}
      {startPage > 0 && <span className="mx-1 text-muted">...</span>}

      {/* Page Numbers */}
      {renderPageButtons()}

      {/* Ellipsis after pages */}
      {endPage < totalPages - 1 && <span className="mx-1 text-muted">...</span>}

      {/* Next Page */}
      <motion.button
        onClick={() => onPageChange(currentPage + 1)}
        disabled={currentPage >= totalPages - 1}
        className="btn btn-outline-primary"
        style={{ minWidth: "40px", borderRadius: "6px" }}
        whileHover={{ scale: 1.05 }}
        whileTap={{ scale: 0.95 }}
      >
        <FontAwesomeIcon icon={faAngleRight} />
      </motion.button>

      {/* Last Page */}
      <motion.button
        onClick={() => onPageChange(totalPages - 1)}
        disabled={currentPage >= totalPages - 1}
        className="btn btn-outline-primary"
        style={{ minWidth: "40px", borderRadius: "6px" }}
        whileHover={{ scale: 1.05 }}
        whileTap={{ scale: 0.95 }}
      >
        <FontAwesomeIcon icon={faAnglesRight} />
      </motion.button>

      {/* Page Info */}
      <div className="d-flex flex-wrap justify-content-center gap-2 mt-2 mt-md-0">
        <span className="text-muted">Page {currentPage + 1} of {totalPages}</span>
        <span className="text-muted">Total Records: {totalElements}</span>
      </div>
    </div>
  );
};

export default Pagination;
