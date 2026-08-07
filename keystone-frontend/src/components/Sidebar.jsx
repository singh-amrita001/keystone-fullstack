import {
  FaHome,
  FaClipboardList,
  FaUsers,
  FaCog,
  FaSignOutAlt
} from "react-icons/fa";

import {
  useNavigate,
  useLocation
} from "react-router-dom";

import { logout } from "../services/authService";

import "../styles/Sidebar.css";

function Sidebar() {

  const navigate = useNavigate();

  const location = useLocation();

  const handleLogout = () => {

    logout();

    navigate("/");
  };

  return (

    <aside className="sidebar">

      <div className="sidebar-menu">

        {/* Dashboard */}

        <div
          className={
            location.pathname === "/dashboard"
              ? "menu-item active"
              : "menu-item"
          }
          onClick={() => navigate("/dashboard")}
        >
          <FaHome />

          <span>Dashboard</span>
        </div>

        {/* Work Orders */}

        <div
          className={
            location.pathname.startsWith("/workorders")
              ? "menu-item active"
              : "menu-item"
          }
          onClick={() => navigate("/workorders")}
        >
          <FaClipboardList />

          <span>Work Orders</span>
        </div>

        {/* Users */}

        <div
          className={
            location.pathname === "/users"
              ? "menu-item active"
              : "menu-item"
          }
          onClick={() => navigate("/users")}
        >
          <FaUsers />

          <span>Users</span>
        </div>

        {/* Settings */}

        <div
          className={
            location.pathname === "/settings"
              ? "menu-item active"
              : "menu-item"
          }
          onClick={() => navigate("/settings")}
        >
          <FaCog />

          <span>Settings</span>
        </div>

      </div>

      {/* Logout */}

      <button
        className="logout-btn"
        onClick={handleLogout}
      >
        <FaSignOutAlt />

        <span>Logout</span>
      </button>

    </aside>

  );
}

export default Sidebar;