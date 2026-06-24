import { motion } from "framer-motion";
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import { faUserPlus } from "@fortawesome/free-solid-svg-icons";

const TeamActions = ({ setIsAdding }) => {
  return (
    <motion.button
      className="btn d-flex align-items-center ms-md-auto"
      style={{
        padding: "10px 20px",
        borderRadius: "8px",
        background: "linear-gradient(90deg, #2b5876 0%, #4e4376 100%)",
        color: "white",
        border: "none",
        whiteSpace: "nowrap",
      }}
      onClick={() => setIsAdding(true)}
      whileHover={{ scale: 1.03, opacity: 0.9 }}
      whileTap={{ scale: 0.98 }}
    >
      <FontAwesomeIcon icon={faUserPlus} className="me-2" />
      Add User
    </motion.button>
  );
};

export default TeamActions;