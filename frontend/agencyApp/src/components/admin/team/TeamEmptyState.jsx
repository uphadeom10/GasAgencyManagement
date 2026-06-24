import svg_path from "../../../assets/no-data.svg";

const TeamEmptyState = ({ colSpan }) => {
  return (
    <tr>
      <td colSpan={colSpan} className="text-center py-5 text-muted">
        <div
          className="position-relative text-center"
          style={{ width: "300px", margin: "0 auto" }}
        >
          <img
            src={svg_path}
            alt="No data"
            className="img-fluid rounded"
            style={{ opacity: 1 }}
          />
          <div
            className="position-absolute top-50 start-50 translate-middle text-dark fw-bold"
            style={{ fontSize: "1rem", marginLeft: "45px" }}
          >
            No team members found
          </div>
        </div>
      </td>
    </tr>
  );
};

export default TeamEmptyState;