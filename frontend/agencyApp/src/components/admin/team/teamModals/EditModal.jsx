import { motion } from "framer-motion";
import EditForm from "./EditForm";

const EditModal = ({ member, onSave, onClose }) => {
  return (
    <motion.div
      key="edit-form"
      initial={{ opacity: 0, y: 20 }}
      animate={{ opacity: 1, y: 0 }}
      exit={{ opacity: 0, y: -20 }}
      transition={{ duration: 0.2 }}
    >
      <EditForm
        member={member}
        onSave={onSave}
        onCancel={onClose}
      />
    </motion.div>
  );
};

export default EditModal;