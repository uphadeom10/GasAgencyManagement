import React, { useState, useEffect } from "react";
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import { faSearch } from "@fortawesome/free-solid-svg-icons"; 
import { motion, AnimatePresence } from "framer-motion";
import { faPlus } from "@fortawesome/free-solid-svg-icons";
const ConfirmationModal = ({ 
    isOpen, 
    onClose, 
    onConfirm, 
    title, 
    message, 
    confirmText = "Delete",
    cancelText = "Cancel"
  }) => {
    return (
      <AnimatePresence>
        {isOpen && (
          <motion.div
            className="modal-backdrop show d-flex justify-content-center align-items-center"
            style={{
              position: "fixed",
              top: 0,
              left: 0,
              right: 0,
              bottom: 0,
              zIndex: 1050,
              backgroundColor: "rgba(255, 255, 255, 0.1)",
              backdropFilter: "blur(8px)",
            }}
            initial={{ opacity: 0 }}
            animate={{ opacity: 1 }}
            exit={{ opacity: 0 }}
          >
            <motion.div
              className="modal-dialog modal-dialog-centered"
              initial={{ y: -50, opacity: 0 }}
              animate={{ y: 0, opacity: 1 }}
              exit={{ y: 50, opacity: 0 }}
              transition={{ type: "spring", damping: 25 }}
            >
              <div
                className="modal-content border-0 shadow-lg"
                style={{ borderRadius: "12px" }}
              >
                <div
                  className="modal-header border-0 pb-0"
                  style={{ padding: "1.5rem" }}
                >
                  <h5 className="modal-title fw-bold">{title}</h5>
                  <button
                    type="button"
                    className="btn-close"
                    onClick={onClose}
                  />
                </div>
                <div className="modal-body pt-0 px-4">
                  <p>{message}</p>
                  <p className="text-muted small">This action cannot be undone.</p>
                </div>
                <div
                  className="modal-footer border-0 pt-0"
                  style={{ padding: "1.5rem" }}
                >
                  <motion.button
                    type="button"
                    className="btn btn-light"
                    onClick={onClose}
                    style={{ borderRadius: "8px" }}
                    whileHover={{ backgroundColor: "rgba(0,0,0,0.05)" }}
                    whileTap={{ scale: 0.98 }}
                  >
                    {cancelText}
                  </motion.button>
                  <motion.button
                    type="button"
                    className="btn btn-danger"
                    onClick={onConfirm}
                    style={{
                      borderRadius: "8px",
                      background: "linear-gradient(90deg, #dc3545 0%, #c82333 100%)",
                      border: "none",
                    }}
                    whileHover={{ opacity: 0.9 }}
                    whileTap={{ scale: 0.98 }}
                  >
                    {confirmText}
                  </motion.button>
                </div>
              </div>
            </motion.div>
          </motion.div>
        )}
      </AnimatePresence>
    );
  };
  
  export default ConfirmationModal;