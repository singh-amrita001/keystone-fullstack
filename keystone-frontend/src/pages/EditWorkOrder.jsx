import { useEffect, useState } from "react";

import {
    useParams,
    useNavigate
} from "react-router-dom";
import Navbar from "../components/Navbar";

import Sidebar from "../components/Sidebar";


import {
    getWorkOrderById,
    updateWorkOrder
} from "../services/workOrderService";


import "../styles/CreateWorkOrder.css";



function EditWorkOrder(){


    const {id}=useParams();

    const navigate=useNavigate();


    const [formData,setFormData]=useState({

        workOrderCode:"",
        title:"",
        description:"",
        priority:"HIGH",
        status:"OPEN",
        slaDueDate:""

    });



    const [loading,setLoading]=useState(true);



    useEffect(()=>{


        async function loadData(){


            try{


                const response =
                await getWorkOrderById(id);


                setFormData(response.data);


            }
            catch(error){

                console.log(error);

                alert(
                    "Failed to load work order"
                );

            }
            finally{

                setLoading(false);

            }


        }


        loadData();


    },[id]);






    const handleChange=(e)=>{


        setFormData({

            ...formData,

            [e.target.name]:
            e.target.value

        });


    };







    const handleSubmit=async(e)=>{


        e.preventDefault();


        try{


            await updateWorkOrder(
                id,
                formData
            );


            alert(
                "Work order updated successfully"
            );


            navigate(
                `/workorders/${id}`
            );


        }
        catch(error){


            console.log(error);


            alert(
                "Update failed"
            );


        }


    };





    if(loading){


        return(

            <>
              <Navbar />  
            <Sidebar/>

            <main className="workorders">

                <h2>
                    Loading...
                </h2>

            </main>

            </>

        );


    }




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
            Edit Work Order
          </h1>

          <p>
            Update and manage your work order details.
          </p>

        </div>

      </div>

      <div className="details-card">

        <form onSubmit={handleSubmit}>

          <div className="form-group">

            <label>Work Order Code</label>

            <input
              type="text"
              name="workOrderCode"
              placeholder="WO-001"
              value={formData.workOrderCode}
              onChange={handleChange}
            />

          </div>

          <div className="form-group">

            <label>Title</label>

            <input
              type="text"
              name="title"
              placeholder="Enter title"
              value={formData.title}
              onChange={handleChange}
            />

          </div>

          <div className="form-group">

            <label>Description</label>

            <textarea
              name="description"
              placeholder="Enter description"
              value={formData.description}
              onChange={handleChange}
            />

          </div>

          <div className="form-group">

            <label>Priority</label>

            <select
              name="priority"
              value={formData.priority}
              onChange={handleChange}
            >
              <option value="LOW">LOW</option>
              <option value="MEDIUM">MEDIUM</option>
              <option value="HIGH">HIGH</option>
            </select>

          </div>

          <div className="form-group">

            <label>Status</label>

            <select
              name="status"
              value={formData.status}
              onChange={handleChange}
            >
              <option value="OPEN">OPEN</option>
              <option value="IN_PROGRESS">IN PROGRESS</option>
              <option value="COMPLETED">COMPLETED</option>
              <option value="CLOSED">CLOSED</option>
            </select>

          </div>

          <div className="form-group">

            <label>SLA Due Date</label>

            <input
              type="datetime-local"
              name="slaDueDate"
              value={formData.slaDueDate?.slice(0, 16)}
              onChange={handleChange}
            />

          </div>

        <button
    type="submit"
    className="submit-workorder-btn"
>
    Update Work Order
</button>

        </form>

      </div>

    </main>
  </>
);



}



export default EditWorkOrder;