import { useEffect, useState } from "react";

import { useParams, useNavigate } from "react-router-dom";

import Navbar from "../components/Navbar";
import Sidebar from "../components/Sidebar";

import {
    getWorkOrderById,
    deleteWorkOrder
} from "../services/workOrderService";

import "../styles/WorkOrderDetails.css";


function WorkOrderDetails() {


    const { id } = useParams();

    const navigate = useNavigate();


    const [workOrder,setWorkOrder] = useState(null);

    const [loading,setLoading] = useState(true);



    useEffect(()=>{


        async function loadWorkOrder(){


            try{


                const response =
                    await getWorkOrderById(id);


                setWorkOrder(
                    response.data
                );


            }
            catch(error){


                console.log(
                    "Error loading work order:",
                    error
                );


            }
            finally{


                setLoading(false);


            }


        }


        loadWorkOrder();


    },[id]);





    const handleDelete = async()=>{


        const confirmDelete =
            window.confirm(
                "Are you sure you want to delete this work order?"
            );


        if(!confirmDelete)
            return;



        try{


            await deleteWorkOrder(id);


            alert(
                "Work order deleted successfully"
            );


            navigate("/workorders");


        }
        catch(error){


            console.log(error);


            alert(
                "Delete failed"
            );


        }


    };





    if(loading){


        return(

            <>

                <Navbar />

                <Sidebar />


                <main className="workorders-details-page">


                    <h2>
                        Loading...
                    </h2>


                </main>


            </>

        );

    }





    if(!workOrder){


        return(

            <>

                <Navbar />

                <Sidebar />


                <main className="workorders-details-page">


                    <h2>
                        Work order not found
                    </h2>


                </main>


            </>

        );

    }





    return(

        <>


            <Navbar />

            <Sidebar />



            <main className="workorders-details-page">


                <button

                    className="back-btn"

                    onClick={() =>
                        navigate("/workorders")
                    }

                >

                    ← Back


                </button>





                <div className="details-card">


                    <h1>

                        {workOrder.title}

                    </h1>





                    <div className="details-item">

                        <strong>
                            Work Code:
                        </strong>


                        <span>
                            {workOrder.workOrderCode}
                        </span>


                    </div>





                    <div className="details-item">


                        <strong>
                            Status:
                        </strong>


                        <span>
                            {workOrder.status}
                        </span>


                    </div>





                    <div className="details-item">


                        <strong>
                            Priority:
                        </strong>


                        <span>
                            {workOrder.priority}
                        </span>


                    </div>





                    <div className="details-item">


                        <strong>
                            Description:
                        </strong>


                        <span>
                            {workOrder.description}
                        </span>


                    </div>





                    <div className="details-item">


                        <strong>
                            SLA Due Date:
                        </strong>


                        <span>
                            {workOrder.slaDueDate}
                        </span>


                    </div>







                    <div className="details-actions">


                        <button

                            className="edit-btn"

                            onClick={()=>


                                navigate(
                                    `/workorders/edit/${id}`
                                )

                            }

                        >

                            Edit


                        </button>





                        <button

                            className="delete-btn"

                            onClick={handleDelete}

                        >

                            Delete


                        </button>



                    </div>



                </div>



            </main>



        </>

    );


}


export default WorkOrderDetails;