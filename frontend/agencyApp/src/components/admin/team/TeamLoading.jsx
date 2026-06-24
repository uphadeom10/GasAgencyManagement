import { motion } from "framer-motion";
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import { faSpinner } from "@fortawesome/free-solid-svg-icons";

const TeamLoading = () => {
  return (
    <div className="d-flex justify-content-center align-items-center min-vh-50">
      <motion.div
        animate={{ rotate: 360 }}
        transition={{ repeat: Infinity, duration: 1, ease: "linear" }}
      >
        <FontAwesomeIcon
          icon={faSpinner}
          size="2x"
          className="text-primary"
        />
      </motion.div>
    </div>
  );
};

export default TeamLoading;