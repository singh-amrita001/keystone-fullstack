import { useEffect, useState } from "react";

import Sidebar from "../components/Sidebar";

import Navbar from "../components/Navbar";<Navbar />

import {
  FaClipboardList,
  FaUsers,
  FaCheckCircle,
  FaClock
} from "react-icons/fa";

import { getDashboardStats } from "../services/dashboardService";

import "../styles/Dashboard.css";


function Dashboard() {


const [stats,setStats] = useState({

  totalOrders:0,
  pendingOrders:0,
  completedOrders:0,
  users:0

});


const [loading,setLoading] = useState(true);



useEffect(()=>{


const loadStats = async()=>{


  try{


    const data = await getDashboardStats();


    setStats(data);


  }
  catch(error){


    console.log(error);


  }
  finally{


    setLoading(false);


  }


};



loadStats();


},[]);



return (

<>



<Navbar />



<Sidebar />

<main className="main-area">


{

loading ?


(

<h2 className="loading">
Loading...
</h2>


)


:


(


<div className="dashboard-container">



<div className="dashboard-header">


<span className="eyebrow">
OVERVIEW
</span>


<h1>
Dashboard
</h1>


<p>
Welcome to Keystone Enterprise Suite
</p>


</div>





<div className="stats-grid">



<div className="stat-card">


<div className="card-top">

<FaClipboardList />

<span>
Total Orders
</span>


</div>


<h2>
{stats.totalOrders}
</h2>


</div>







<div className="stat-card">


<div className="card-top">

<FaClock />

<span>
Pending Orders
</span>


</div>


<h2>
{stats.pendingOrders}
</h2>


</div>







<div className="stat-card">


<div className="card-top">

<FaCheckCircle />

<span>
Completed
</span>


</div>


<h2>
{stats.completedOrders}
</h2>


</div>







<div className="stat-card">


<div className="card-top">

<FaUsers />

<span>
Users
</span>


</div>


<h2>
{stats.users}
</h2>


</div>




</div>






<div className="dashboard-grid">



<div className="box">


<h2>
Recent Activity
</h2>


<div className="activity-item">

<span>
Work order created
</span>

<small>
Today
</small>

</div>



<div className="activity-item">

<span>
User registered
</span>

<small>
Today
</small>

</div>



<div className="activity-item">

<span>
Order completed
</span>

<small>
Yesterday
</small>

</div>



</div>







<div className="box">


<h2>
Summary
</h2>



<div className="summary-row">

<span>
Total Orders
</span>

<strong>
{stats.totalOrders}
</strong>

</div>




<div className="summary-row">

<span>
Pending
</span>

<strong>
{stats.pendingOrders}
</strong>

</div>




<div className="summary-row">

<span>
Users
</span>

<strong>
{stats.users}
</strong>

</div>



</div>




</div>



</div>


)


}


</main>


</>

);


}


export default Dashboard;