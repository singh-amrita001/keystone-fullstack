import { useState } from "react";
import { useNavigate } from "react-router-dom";
import { login } from "../services/authService";
import "./../styles/Login.css";


function Login(){

    const navigate = useNavigate();

    const [email,setEmail] = useState("");
    const [password,setPassword] = useState("");
    const [error,setError] = useState("");


const handleLogin = async (e) => {

    e.preventDefault();

    setError("");

    try {

       const response = await login(
    email,
    password
);


console.log(
    "LOGIN SUCCESS RESPONSE:",
    response
);


navigate("/dashboard");




    } catch (err) {


        console.log(
            "LOGIN FAILED:",
            err
        );

    setError(
        "Invalid email or password"
    );

        if(err.response){

            console.log(
                "STATUS:",
                err.response.status
            );


            console.log(
                "DATA:",
                err.response.data
            );

        }


        setError(
            "Login failed"
        );

    }

};

return (

<div className="login-page">


    {/* LEFT SIDE */}

    <div className="brand-section">


        <div className="brand-content">


            <h1>
                Keystone
            </h1>


            <p>
              Enterprise Work Order
              Management Platform
            </p>


            <div className="features">

                <div>
                    ✓ Smart Work Orders
                </div>

                <div>
                    ✓ Secure JWT Authentication
                </div>

                <div>
                    ✓ Real Time Management
                </div>

            </div>


        </div>


    </div>




    {/* RIGHT SIDE */}


    <div className="form-section">


        <div className="login-card">


            <h2>
                Welcome Back
            </h2>


            <p className="subtitle">
                Login to your Keystone account
            </p>



            <form onSubmit={handleLogin}>


                <label>
                    Email Address
                </label>


                <input
                type="email"
                placeholder="keystoneadmin@gmail.com"
                value={email}
                onChange={
                    e=>setEmail(e.target.value)
                }
                />



                <label>
                    Password
                </label>


                <input

                type="password"

                placeholder="Enter password"

                value={password}

                onChange={
                    e=>setPassword(e.target.value)
                }

                />



                {
                    error &&
                    <p className="error">
                        {error}
                    </p>
                }



                <button>

                    Sign In

                </button>
<p className="register-text">
  Don't have an account?

  <span onClick={() => navigate("/register")}>
    Register
  </span>
</p>

            </form>


        </div>


    </div>



</div>


)

}


export default Login;