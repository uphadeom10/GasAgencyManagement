import React from "react";

const SearchBar = ({ searchTerm, onSearchChange }) => {
  return (
    <div className=" d-flex justify-content-between">
      <input
        type="text"
        className="form-control"
        placeholder="Search team members..."
        value={searchTerm}
        onChange={(e) => onSearchChange(e.target.value)}
      />
    </div>
  );
};

export default SearchBar;
