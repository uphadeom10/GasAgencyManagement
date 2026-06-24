import { Form, InputGroup } from "react-bootstrap";
import { FiSearch } from "react-icons/fi";

const AssignmentSearchBar = ({ onSearch, placeholder = "Search by ID..." }) => {
  const handleSearch = (e) => {
    onSearch(e.target.value);
  };

  return (
    <div className="mb-2 p-2 bg-light rounded">
      <InputGroup>
        <InputGroup.Text>
          <FiSearch />
        </InputGroup.Text>
        <Form.Control
          type="text"
          placeholder={placeholder}
          onChange={handleSearch}
        />
      </InputGroup>
    </div>
  );
};

export default AssignmentSearchBar;