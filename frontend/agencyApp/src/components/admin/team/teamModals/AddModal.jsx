import { motion } from "framer-motion";
import AddUserForm from "./AddUserForm";

const AddModal = ({ onSave, onClose }) => {
  return (
    <motion.div
      key="add-form"
      initial={{ opacity: 0, y: 20 }}
      animate={{ opacity: 1, y: 0 }}
      exit={{ opacity: 0, y: -20 }}
      transition={{ duration: 0.2 }}
    >
      <AddUserForm
        onAdd={onSave}
        onCancel={onClose}
      />
    </motion.div>
  );
};

export default AddModal;