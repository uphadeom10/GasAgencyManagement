import { useState, useEffect, useCallback } from "react";
import { toast } from "react-toastify";

const useTeamMembers = () => {
  const [teamMembers, setTeamMembers] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [pagination, setPagination] = useState({
    currentPage: 0,
    totalPages: 0,
    totalElements: 0,
  });

  const base_Url = import.meta.env.VITE_API_URL || "";
  const PAGE_SIZE = 5;

  const fetchTeamMembers = useCallback(async (page = 0) => {
    const token = sessionStorage.getItem("token");
    if (!token) {
      setError("Authentication token not found");
      setLoading(false);
      return;
    }

    setLoading(true);
    setError(null); // Clear previous errors
    
    try {
      const response = await fetch(`${base_Url}/masters/getUsersList`, {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
          Authorization: `Bearer ${token}`,
        },
        body: JSON.stringify({
          page,
          size: PAGE_SIZE,
        }),
      });

      if (!response.ok) {
        throw new Error(`HTTP error! status: ${response.status}`);
      }

      const data = await response.json();

      setTeamMembers(
        data.result.map((member) => ({
          id: member.Id,
          SrNo: member.Sr_no,
          "Employee Name": member.Employee_name,
          Role: member.Role,
          "Mobile Number": member.Mobile_number,
          status: member.Status,
          userName: member.UserName,
        }))
      );

      setPagination({
        currentPage: page,
        totalPages: data.totalPages || Math.ceil(data.count / PAGE_SIZE),
        totalElements: data.count,
      });
    } catch (err) {
      setError(err.message);
      toast.error(`Failed to fetch team members: ${err.message}`);
    } finally {
      setLoading(false);
    }
  }, [base_Url, PAGE_SIZE]); // Added dependencies
  // Automatically fetch first page on mount
  useEffect(() => {
    fetchTeamMembers(0);
  }, [fetchTeamMembers]);
  const handleDelete = async (memberId) => {
    const token = sessionStorage.getItem("token");
    if (!token) {
      setError("Authentication token not found");
      return false;
    }

    try {
      const response = await fetch(
        `${base_Url}/masters/deleteUserById/${memberId}`,
        {
          method: "DELETE",
          headers: {
            Authorization: `Bearer ${token}`,
          },
        }
      );
      
      if (response.status === 200) {
        toast.warning("User deleted successfully! This cannot be undone.");
        // Use the current pagination state from the hook's closure
        await fetchTeamMembers(pagination.currentPage);
        return true;
      }
      
      if (response.status === 400) {
        throw new Error("Failed to delete user. Please try again.");
      }
      
      throw new Error("Something went wrong. Please try again later.");

    } catch (err) {
      setError(err.message);
      toast.error(err.message);
      return false;
    }
  };

  const handleSave = async (memberData) => {
    const token = sessionStorage.getItem("token");
    if (!token) {
      setError("Authentication token not found");
      return false;
    }

    try {
      const response = await fetch(`${base_Url}/masters/createAndUpdateUsers`, {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
          Authorization: `Bearer ${token}`,
        },
        body: JSON.stringify(memberData),
      });
      
      if (response.status === 200) {
        toast.success("User saved successfully!");
        await fetchTeamMembers(pagination.currentPage);
        return true;
      }
      
      if (response.status === 400) {
        throw new Error("Failed to save user. Please try again.");
      }
      
      if (response.status === 409) {
        throw new Error("User with these details already exists.");
      }
      
      throw new Error("Something went wrong. Please try again later.");

    } catch (err) {
      setError(err.message);
      toast.error(err.message);
      return false;
    }
  };

  return {
    teamMembers,
    loading,
    error,
    pagination,
    fetchTeamMembers,
    handleDelete,
    handleSave,
  };
};

export default useTeamMembers;