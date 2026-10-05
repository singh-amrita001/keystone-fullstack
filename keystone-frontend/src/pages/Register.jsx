import { useState } from "react";
import { Link, useNavigate } from "react-router-dom";
import axios from "axios";

import heroLogo from "../assets/hero.png";
import "../styles/Register.css";


function Register() {


    const navigate = useNavigate();



    const [user, setUser] = useState({

        name: "",
        email: "",
        password: ""

    });



    const [error, setError] = useState("");



    const handleChange = (e) => {

        setUser({

            ...user,
            [e.target.name]: e.target.value

        });

    };



    const handleSubmit = async (e) => {

        e.preventDefault();


        setError("");



        try {


            await axios.post(

                "http://localhost:8082/api/auth/register",

                {
                    ...user,
                    role: "USER"
                }

            );


            navigate("/login");



        } catch (error) {


            setError(

                error.response?.data?.message ||
                "Registration failed"

            );


        }


    };



    return (


        <div className="register-page">



            <div className="register-card">





                {/* LOGO */}

                <div className="logo">


                    <img

                        src={heroLogo}

                        alt="Keystone Logo"

                        className="auth-logo"

                    />





                </div>






                <h1>

                    Create Account

                </h1>




                <p>

                    Register for Keystone Enterprise Suite

                </p>







                {error && (


                    <div className="error">

                        {error}

                    </div>


                )}








                <form onSubmit={handleSubmit}>





                    <label>

                        Full Name

                    </label>



                    <input

                        type="text"

                        name="name"

                        placeholder="Enter your name"

                        value={user.name}

                        onChange={handleChange}

                        required

                    />







                    <label>

                        Email

                    </label>



                    <input

                        type="email"

                        name="email"

                        placeholder="Enter your email"

                        value={user.email}

                        onChange={handleChange}

                        required

                    />








                    <label>

                        Password

                    </label>



                    <input

                        type="password"

                        name="password"

                        placeholder="Create password"

                        value={user.password}

                        onChange={handleChange}

                        required

                    />







                    <button type="submit">


                        Register


                    </button>





                </form>








                <div className="login-link">



                    Already have an account?



                    <Link to="/login">


                        <span>

                            Login

                        </span>


                    </Link>



                </div>





            </div>





        </div>


    );


}


export default Register;