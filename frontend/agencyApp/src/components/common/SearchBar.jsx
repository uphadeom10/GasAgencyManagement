import React, { useState, useEffect } from "react";
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import { faSearch } from "@fortawesome/free-solid-svg-icons";

const SearchBar = ({ searchTerm, setSearchTerm }) => {
    return (
      <div className="position-relative w-100 w-md-auto">
        <FontAwesomeIcon
          icon={faSearch}
          className="position-absolute top-50 translate-middle-y text-muted"
          style={{ left: "10px" }}
        />
        <input
          type="text"
          value={searchTerm}
          onChange={(e) => setSearchTerm(e.target.value)}
          className="form-control ps-5"
          style={{
            borderRadius: "8px",
            border: "1px solid rgba(0,0,0,0.1)",
            padding: "10px 10px",
            background: "rgba(0,0,0,0.02)",
            maxWidth: "400px",
          }}
          placeholder="Search products..."
        />
      </div>
    );
  };
  
  export default SearchBar;