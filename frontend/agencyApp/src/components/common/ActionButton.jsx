import React, { useState, useEffect } from "react";
import { motion, AnimatePresence } from "framer-motion";
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import { faSpinner } from "@fortawesome/free-solid-svg-icons";
const ActionButton = ({ 
    icon, 
    label, 
    onClick, 
    bgColor, 
    textColor, 
    hoverBgColor 
  }) => {
    return (
      <motion.button
        className="btn d-flex align-items-center"
        style={{
          padding: "10px 20px",
          borderRadius: "8px",
          background: bgColor,
          color: textColor,
          border: "none",
          whiteSpace: "nowrap",
        }}
        onClick={onClick}
        whileHover={{ 
          scale: 1.03, 
          opacity: 0.9,
          backgroundColor: hoverBgColor 
        }}
        whileTap={{ scale: 0.98 }}
      >
        <FontAwesomeIcon icon={icon} className="me-2" />
        {label}
      </motion.button>
    );
  };

  export default ActionButton;