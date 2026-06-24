import { motion } from "framer-motion";
import TeamStatusBadge from "./TeamStatusBadge";
import TeamPagination from "./TeamPagination";
import TeamEmptyState from "./TeamEmptyState";
import TeamLoading from "./TeamLoading";
import TeamError from "./TeamError";
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import {
  faUserPlus,
  faCheck,
  faTimes,
  faEdit,
  faTrash,
  faAngleLeft,
  faAngleRight,
  faAnglesLeft,
  faAnglesRight,
  faSearch,
  faSpinner,
} from "@fortawesome/free-solid-svg-icons";
const TeamTable = ({
  members,
  loading,
  error,
  pagination,
  fetchTeamMembers,
  onEdit,
  onDelete,
}) => {
  const headers = members.length > 0 
    ? Object.keys(members[0]).filter((key) => key !== "id") 
    : [];

  if (loading) return <TeamLoading />;
  if (error) return <TeamError error={error} />;

  return (
    <motion.div
      key="table-view"
      initial={{ opacity: 0 }}
      animate={{ opacity: 1 }}
      exit={{ opacity: 0 }}
    >
      <div className="table-responsive rounded-3 overflow-y">
        <table className="table table-hover align-middle mb-0">
          <thead className="sticky-top text-center">
            <tr style={{
              background: "linear-gradient(90deg, #2b5876 0%, #4e4376 100%)",
              color: "white",
            }}>
              {headers.map((header) => (
                <th key={header} className="fw-medium py-3 px-3" style={{ whiteSpace: "nowrap" }}>
                  {header.charAt(0).toUpperCase() + header.slice(1).replace(/([A-Z])/g, " $1")}
                </th>
              ))}
              <th className="fw-medium py-3 px-3 text-center">Actions</th>
            </tr>
          </thead>

          <tbody>
            {members.length > 0 ? (
              members.map((member, index) => (
                <TableRow 
                  key={index} 
                  member={member} 
                  headers={headers}
                  onEdit={onEdit}
                  onDelete={onDelete}
                />
              ))
            ) : (
              <TeamEmptyState colSpan={headers.length + 1} />
            )}
          </tbody>
        </table>
      </div>

      {pagination.totalPages > 1 && (
        <TeamPagination 
          pagination={pagination} 
          onPageChange={fetchTeamMembers} 
        />
      )}
    </motion.div>
  );
};

const TableRow = ({ member, headers, onEdit, onDelete }) => (
  <motion.tr
    initial={{ opacity: 0, y: 10 }}
    animate={{ opacity: 1, y: 0 }}
    transition={{ duration: 0.2 }}
    className="border-bottom border-light"
    whileHover={{ backgroundColor: "rgba(43, 88, 118, 0.03)" }}
  >
    {headers.map((header) => (
      <td key={header} className="py-3 px-3 text-center">
        {header === "status" ? (
          <TeamStatusBadge status={member[header]} />
        ) : (
          member[header]
        )}
      </td>
    ))}
    <td className="py-3 px-3">
      <RowActions onEdit={() => onEdit(member)} onDelete={() => onDelete(member)} />
    </td>
  </motion.tr>
);

const RowActions = ({ onEdit, onDelete }) => (
  <div className="d-flex justify-content-center gap-2">
    <motion.button
      className="btn btn-sm"
      onClick={onEdit}
      style={{
        background: "rgba(43, 88, 118, 0.1)",
        color: "#2b5876",
        borderRadius: "6px",
        padding: "5px 12px",
        border: "none",
        whiteSpace: "nowrap",
      }}
      whileHover={{ backgroundColor: "rgba(43, 88, 118, 0.2)" }}
      whileTap={{ scale: 0.95 }}
    >
      <FontAwesomeIcon icon={faEdit} className="me-1" />
      Edit
    </motion.button>
    <motion.button
      className="btn btn-sm"
      onClick={onDelete}
      style={{
        background: "rgba(220, 53, 69, 0.1)",
        color: "#dc3545",
        borderRadius: "6px",
        padding: "5px 12px",
        border: "none",
        whiteSpace: "nowrap",
      }}
      whileHover={{ backgroundColor: "rgba(220, 53, 69, 0.2)" }}
      whileTap={{ scale: 0.95 }}
    >
      <FontAwesomeIcon icon={faTrash} className="me-1" />
      Delete
    </motion.button>
  </div>
);

export default TeamTable;