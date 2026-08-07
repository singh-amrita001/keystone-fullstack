import {
  BrowserRouter,
  Routes,
  Route,
  Navigate
} from "react-router-dom";

import Login from "./pages/Login";
import Register from "./pages/Register";
import Dashboard from "./pages/Dashboard";
import WorkOrders from "./pages/WorkOrders";
import WorkOrderDetails from "./pages/WorkOrderDetails";
import CreateWorkOrder from "./pages/CreateWorkOrder";
import EditWorkOrder from "./pages/EditWorkOrder";
import Users from "./pages/Users";
import Profile from "./pages/Profile";
import Settings from "./pages/Settings";

function App() {
  return (
    <BrowserRouter>
      <Routes>

        <Route path="/" element={<Login />} />

        <Route path="/register" element={<Register />} />

        <Route path="/dashboard" element={<Dashboard />} />

        <Route path="/workorders" element={<WorkOrders />} />

        <Route
          path="/workorders/:id"
          element={<WorkOrderDetails />}
        />

        <Route
          path="/workorders/create"
          element={<CreateWorkOrder />}
        />

        <Route
          path="/workorders/edit/:id"
          element={<EditWorkOrder />}
        />

        <Route path="/users" element={<Users />} />

        <Route path="/profile" element={<Profile />} />

        <Route path="/settings" element={<Settings />} />

        <Route
          path="*"
          element={<Navigate to="/" />}
        />

      </Routes>
    </BrowserRouter>
  );
}

export default App;