import { useState } from "react";
import { useNavigate } from "react-router-dom";

import Sidebar from "../components/Sidebar";
import "../styles/CreateWorkOrder.css";

import { createWorkOrder } from "../services/workOrderService";
import Navbar from "../components/Navbar";

function CreateWorkOrder() {

  const navigate = useNavigate();


  const [formData, setFormData] = useState({

    workOrderCode: "",
    title: "",
    description: "",
    priority: "HIGH",
    status: "OPEN",
    slaDueDate: "",

  });



  const handleChange = (e) => {

    setFormData({

      ...formData,

      [e.target.name]: e.target.value,

    });

  };



  const handleSubmit = async (e) => {

    e.preventDefault();


    try {


      await createWorkOrder(formData);


      alert(
        "Work order created successfully!"
      );


      navigate("/workorders");


    } catch (error) {


      console.error(
        "Create Work Order Error:",
        error
      );


      alert(
        "Failed to create work order"
      );

    }

  };



  return (

    <>

      <Navbar />

      <Sidebar />

  
      <main className="create-workorder-page">


        <div className="create-header">


          <div>

            <span className="eyebrow">
              MANAGEMENT
            </span>


            <h1>
              Create Work Order
            </h1>


            <p>
              Add a new Keystone work order.
            </p>


          </div>


        </div>



        <div className="details-card">


          <form onSubmit={handleSubmit}>


            {/* Work Order Code */}

            <div className="form-group">

              <label>
                Work Order Code
              </label>


              <input

                type="text"

                name="workOrderCode"

                placeholder="Example: WO-001"

                value={formData.workOrderCode}

                onChange={handleChange}

                required

              />

            </div>




            {/* Title */}

            <div className="form-group">

              <label>
                Title
              </label>


              <input

                type="text"

                name="title"

                placeholder="Enter title"

                value={formData.title}

                onChange={handleChange}

                required

              />

            </div>




            {/* Description */}

            <div className="form-group">

              <label>
                Description
              </label>


              <textarea

                name="description"

                placeholder="Enter description"

                value={formData.description}

                onChange={handleChange}

                required

              />

            </div>




            {/* Priority */}

            <div className="form-group">

              <label>
                Priority
              </label>


              <select

                name="priority"

                value={formData.priority}

                onChange={handleChange}

              >

                <option value="LOW">
                  LOW
                </option>


                <option value="MEDIUM">
                  MEDIUM
                </option>


                <option value="HIGH">
                  HIGH
                </option>


              </select>

            </div>




            {/* Status */}

            <div className="form-group">

              <label>
                Status
              </label>


              <select

                name="status"

                value={formData.status}

                onChange={handleChange}

              >

                <option value="OPEN">
                  OPEN
                </option>


                <option value="NEW">
                  NEW
                </option>


                <option value="ASSIGNED">
                  ASSIGNED
                </option>


                <option value="IN_PROGRESS">
                  IN_PROGRESS
                </option>


                <option value="COMPLETED">
                  COMPLETED
                </option>


                <option value="CLOSED">
                  CLOSED
                </option>


              </select>

            </div>




            {/* SLA Date */}

            <div className="form-group">

              <label>
                SLA Due Date
              </label>


              <input

                type="datetime-local"

                name="slaDueDate"

                value={formData.slaDueDate}

                onChange={handleChange}

              />

            </div>




            <button
              type="submit"
              className="submit-workorder-btn"
            >

              Create Work Order

            </button>



          </form>


        </div>


      </main>


    </>

  );

}


export default CreateWorkOrder;