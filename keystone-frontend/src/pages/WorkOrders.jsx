import {
    useEffect,
    useState
} from "react";

import { useNavigate } from "react-router-dom";



import { getWorkOrders } from "../services/workOrderService";
import Sidebar from "../components/Sidebar";
 import Navbar from "../components/Navbar";
import "../styles/WorkOrders.css";



function WorkOrders() {


    const [workOrders,setWorkOrders] = useState([]);

    const [loading,setLoading] = useState(true);

    const [search,setSearch] = useState("");

    const [status,setStatus] = useState("");

    const [page,setPage] = useState(0);

    const [totalPages,setTotalPages] = useState(1);


    const navigate = useNavigate();



    useEffect(()=>{


        const loadWorkOrders = async()=>{


            try{


                setLoading(true);


                const response = await getWorkOrders(
                    page,
                    status
                );


                setWorkOrders(
                    response.data.content || []
                );


                setTotalPages(
                    response.data.totalPages || 1
                );


            }
            catch(error){


                console.error(
                    "Error loading work orders:",
                    error
                );


            }
            finally{


                setLoading(false);


            }


        };


        loadWorkOrders();


    },[page,status]);





    const filteredWorkOrders =
        workOrders.filter((order)=>{


            const text =
                search.toLowerCase();



            return (

                order.title
                ?.toLowerCase()
                .includes(text)

                ||

                order.description
                ?.toLowerCase()
                .includes(text)

                ||

                order.workOrderCode
                ?.toLowerCase()
                .includes(text)

            );


        });







    if(loading){


        return(

            <>
<Navbar />



            <Sidebar />


                <main className="workorders">


                    <h2>
                        Loading work orders...
                    </h2>


                </main>


            </>

        );


    }






    return(


        <>
   
<Navbar />

            <Sidebar />




            <main className="workorders">



                <div className="workorders-header">


                    <div>


                        <span className="eyebrow">
                            MANAGEMENT
                        </span>


                        <h1>
                            Work Orders
                        </h1>


                        <p>
                            Create and manage your Keystone work orders.
                        </p>


                    </div>





                    <button

                        className="create-btn"

                        onClick={()=> 
                            navigate("/workorders/create")
                        }

                    >

                        + Create Work Order


                    </button>



                </div>






                <div className="workorder-tools">


                    <input

                        type="text"

                        placeholder="Search work orders..."

                        value={search}

                        onChange={(e)=>
                            setSearch(e.target.value)
                        }

                    />





                    <select

                        value={status}

                        onChange={(e)=>{

                            setStatus(
                                e.target.value
                            );

                            setPage(0);

                        }}

                    >


                        <option value="">
                            All Status
                        </option>


                        <option value="OPEN">
                            OPEN
                        </option>


                        <option value="IN_PROGRESS">
                            IN PROGRESS
                        </option>


                        <option value="COMPLETED">
                            COMPLETED
                        </option>


                        <option value="CLOSED">
                            CLOSED
                        </option>


                    </select>



                </div>







                <div className="workorder-list">


                    {

                        filteredWorkOrders.length === 0 ?


                        (

                            <h3>
                                No work orders found.
                            </h3>

                        )


                        :


                        filteredWorkOrders.map((order)=>(


                            <div

                                className="workorder-card"

                                key={order.id}

                            >



                                <span

                                className={
                                    `status-badge ${order.status.toLowerCase()}`
                                }

                                >

                                    {order.status}

                                </span>




                                <h3>
                                    {order.title}
                                </h3>




                                <p>
                                    {order.description}
                                </p>




                                <p>

                                    <b>
                                        Priority:
                                    </b>

                                    {" "}

                                    {order.priority}

                                </p>




                                <p>

                                    <b>
                                        Work Code:
                                    </b>

                                    {" "}

                                    {order.workOrderCode}

                                </p>





                                <button

                                    onClick={()=>


                                        navigate(
                                            `/workorders/${order.id}`
                                        )

                                    }

                                >

                                    View Details


                                </button>



                            </div>


                        ))


                    }



                </div>







                <div className="pagination">



                    <button

                        disabled={
                            page===0
                        }

                        onClick={()=>
                            setPage(page-1)
                        }

                    >

                        Previous


                    </button>





                    <span>

                        Page {page+1} of {totalPages}

                    </span>





                    <button

                        disabled={
                            page >= totalPages-1
                        }

                        onClick={()=>
                            setPage(page+1)
                        }

                    >

                        Next


                    </button>



                </div>





            </main>



        </>


    );


}


export default WorkOrders;