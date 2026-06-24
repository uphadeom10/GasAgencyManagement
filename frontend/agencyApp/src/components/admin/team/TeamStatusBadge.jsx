import { motion } from "framer-motion";
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import { faCheck, faTimes } from "@fortawesome/free-solid-svg-icons";

const TeamStatusBadge = ({ status }) => {
  return (
    <motion.span
      className={`badge d-flex align-items-center justify-content-center ${
        status ? "bg-success-light text-success" : "bg-danger-light text-danger"
      }`}
      style={{
        padding: "0.35em 0.85em",
        borderRadius: "50rem",
        fontSize: "0.875rem",
        fontWeight: 600,
        lineHeight: 1,
        whiteSpace: "nowrap",
        width: "fit-content",
        margin: "0 auto",
      }}
      whileHover={{ scale: 1.05 }}
    >
      <FontAwesomeIcon
        icon={status ? faCheck : faTimes}
        className="me-1"
        style={{ fontSize: "0.8em" }}
      />
      {status ? "Active" : "Inactive"}
    </motion.span>
  );
};

export default TeamStatusBadge;