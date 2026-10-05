import { useState } from "react";

import Sidebar from "../components/Sidebar";
import Navbar from "../components/Navbar";

import "../styles/Settings.css";


function Settings() {


const [password,setPassword] = useState("");

const [confirmPassword,setConfirmPassword] = useState("");



const handleChangePassword = (e)=>{

  e.preventDefault();


  if(password !== confirmPassword){

    alert("Passwords do not match");

    return;

  }


  alert("Password updated successfully");


  setPassword("");

  setConfirmPassword("");

};



return (

<>


  <Navbar />

  <Sidebar />


  <div className="settings-page">


    <div className="settings-card">


      <span className="settings-label">
        ACCOUNT SETTINGS
      </span>


      <h1>
        Change Password
      </h1>


      <p>
        Update your account password securely.
      </p>



      <form onSubmit={handleChangePassword}>


        <div className="settings-form-group">

          <label>
            New Password
          </label>


          <input

            type="password"

            placeholder="Enter new password"

            value={password}

            onChange={(e)=>
              setPassword(e.target.value)
            }

          />

        </div>




        <div className="settings-form-group">


          <label>
            Confirm Password
          </label>


          <input

            type="password"

            placeholder="Confirm password"

            value={confirmPassword}

            onChange={(e)=>
              setConfirmPassword(e.target.value)
            }

          />


        </div>



        <button
          className="settings-btn"
          type="submit"
        >

          Update Password

        </button>



      </form>



    </div>


  </div>


</>

);

}


export default Settings;