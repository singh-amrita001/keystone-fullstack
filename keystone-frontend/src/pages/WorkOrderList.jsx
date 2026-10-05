import { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";

import Sidebar from "../components/Sidebar";
import Navbar from "../components/Navbar";
import {
  getWorkOrders,
  deleteWorkOrder,
} from "../services/workOrderService";

import "../styles/WorkOrderList.css";

function WorkOrderList() {
  const navigate = useNavigate();

  const [workOrders, setWorkOrders] = useState([]);
  const [loading, setLoading] = useState(true);

  const [page, setPage] = useState(0);
  const [totalPages, setTotalPages] = useState(0);

  const [status, setStatus] = useState("");
  const [sortBy, setSortBy] = useState("id");

  const [search, setSearch] = useState("");

  const refreshWorkOrders = async () => {
  try {
    setLoading(true);

    const response = await getWorkOrders(
      page,
      status,
      sortBy
    );

    setWorkOrders(response.data.content || []);
    setTotalPages(response.data.totalPages || 0);

  } catch (error) {
    console.log("Refresh Error:", error);

  } finally {
    setLoading(false);
  }
};
useEffect(() => {
  const fetchData = async () => {
    try {
      setLoading(true);

      const response = await getWorkOrders(
        page,
        status,
        sortBy
      );

      setWorkOrders(response.data.content || []);
      setTotalPages(response.data.totalPages || 0);

    } catch (error) {
      console.log("Fetch Error:", error);

    } finally {
      setLoading(false);
    }
  };

  fetchData();

}, [page, status, sortBy]);

 const handleDelete = async (id) => {
  const confirmDelete = window.confirm(
    "Delete this work order?"
  );

  if (!confirmDelete) {
    return;
  }

  try {
    await deleteWorkOrder(id);

    alert("Deleted successfully");

    await refreshWorkOrders();

  } catch (error) {
    console.log("Delete Error:", error);
  }
};

  const filteredOrders = workOrders.filter((order) =>
    order.title
      ?.toLowerCase()
      .includes(search.toLowerCase())
  );

  if (loading) {
    return (
      <>
      <Navbar />
        <Sidebar />
    
        <main className="workorders-page">
          <h2>Loading...</h2>
        </main>
      </>
    );
  }

  return (
    <>
      <Sidebar />

      <main className="workorders-page">
        <div className="header">
          <h1>Work Orders</h1>

          <button
            className="create-btn"
            onClick={() =>
              navigate("/workorders/create")
            }
          >
            + Create Work Order
          </button>
        </div>

        <div className="filters">
          <input
            type="text"
            placeholder="Search by title..."
            value={search}
            onChange={(e) =>
              setSearch(e.target.value)
            }
          />

          <select
            value={status}
            onChange={(e) =>
              setStatus(e.target.value)
            }
          >
            <option value="">
              All Status
            </option>

            <option value="OPEN">
              OPEN
            </option>

            <option value="IN_PROGRESS">
              IN_PROGRESS
            </option>

            <option value="COMPLETED">
              COMPLETED
            </option>
          </select>

          <select
            value={sortBy}
            onChange={(e) =>
              setSortBy(e.target.value)
            }
          >
            <option value="id">
              Sort by ID
            </option>

            <option value="title">
              Sort by Title
            </option>

            <option value="priority">
              Sort by Priority
            </option>
          </select>
        </div>

        <table>
          <thead>
            <tr>
              <th>ID</th>
              <th>Code</th>
              <th>Title</th>
              <th>Priority</th>
              <th>Status</th>
              <th>Action</th>
            </tr>
          </thead>

          <tbody>
            {filteredOrders.map((order) => (
              <tr key={order.id}>
                <td>{order.id}</td>

                <td>{order.workOrderCode}</td>

                <td>{order.title}</td>

                <td>{order.priority}</td>

                <td>{order.status}</td>

                <td>
                  <button
                    onClick={() =>
                      navigate(
                        `/workorders/${order.id}`
                      )
                    }
                  >
                    View
                  </button>

                  <button
                    onClick={() =>
                      navigate(
                        `/workorders/edit/${order.id}`
                      )
                    }
                  >
                    Edit
                  </button>

                  <button
                    onClick={() =>
                      handleDelete(order.id)
                    }
                  >
                    Delete
                  </button>
                </td>
              </tr>
            ))}
          </tbody>
        </table>

        <div className="pagination">
          <button
            disabled={page === 0}
            onClick={() =>
              setPage(page - 1)
            }
          >
            Previous
          </button>

          <span>
            Page {page + 1} of {totalPages}
          </span>

          <button
            disabled={page + 1 >= totalPages}
            onClick={() =>
              setPage(page + 1)
            }
          >
            Next
          </button>
        </div>
      </main>
    </>
  );
}

export default WorkOrderList;