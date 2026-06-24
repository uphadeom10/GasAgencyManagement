import { createContext, useContext, useState } from "react";
import axios from "axios";
import { useNavigate } from "react-router-dom";
import { toast, ToastContainer } from "react-toastify"; // Optional: for logout feedback
const AuthContext = createContext();
const BASE_URL = import.meta.env.VITE_API_URL || ""
//console.log(BASE_URL) 
const AuthProvider = ({ children }) => {
  const [user, setUser] = useState(() => {
    const storedUser = sessionStorage.getItem("user");
    return storedUser ? JSON.parse(storedUser) : null;
  });
  const [token, setToken] = useState(sessionStorage.getItem("token") || "");
  const [userId, setUserId] = useState(() => {
    const storedUser = sessionStorage.getItem("user");
    return storedUser ? JSON.parse(storedUser).userId : null;
  });
  const navigate = useNavigate();
  const loginAction = async (data) => {
    try {
      const response = await axios.post(
        `${BASE_URL}/api/auth/login`,
        data
      );
    
      if (response.data.statusCode === 401) {
        return;
      }
      
      const { token, userName, role, userId } = response.data.result;
      //console.log(userId)
      setUserId(userId);
      // Store in sessionStorage
      sessionStorage.setItem("token", token);
      sessionStorage.setItem(
        "user",
        JSON.stringify({ name: userName, role, userId })
      );
      //sessionStorage.setItem("userId", userId);
      // Update state
      setUser({ name: userName, role, userId });
      setToken(token);
      // Return role for navigation
      return role;
    } catch (error) {
      if (error.response?.status === 401 || error.response?.status === 400) {
        toast.error("Invalid username or password. Please try again.");
      } else {
        toast.error("Server is unreachable. Please check your connection or try again.");
      }
      return undefined;
    }
  };
  const logOut = () => {
    sessionStorage.removeItem("token");
    sessionStorage.removeItem("user");
    setUser(null);
    setToken("");
    toast.success("Logged out successfully");
    navigate("/login");
  };
  return (
    <AuthContext.Provider value={{ user, token, loginAction, logOut, role: user?.role, userId }}>
      {children}
      <ToastContainer position="top-right" autoClose={3000} style={{ zIndex: 9999, marginTop: "70px" }} />
    </AuthContext.Provider>
  );
};
export const useAuth = () => useContext(AuthContext);
export default AuthProvider;