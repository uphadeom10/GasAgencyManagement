import { motion } from "framer-motion";

const TeamError = ({ error }) => {
  return (
    <motion.div
      className="alert alert-danger mt-4 mx-3 mx-md-5"
      initial={{ opacity: 0, y: -20 }}
      animate={{ opacity: 1, y: 0 }}
      style={{
        background: "rgba(220, 53, 69, 0.1)",
        border: "none",
        borderRadius: "8px",
        color: "#dc3545",
      }}
    >
      <div className="d-flex justify-content-between align-items-center">
        <span>Error: {error}</span>
        <button
          className="btn btn-sm"
          onClick={() => window.location.reload()}
          style={{
            background: "rgba(220, 53, 69, 0.2)",
            color: "#dc3545",
            borderRadius: "20px",
          }}
        >
          Retry
        </button>
      </div>
    </motion.div>
  );
};

export default TeamError;