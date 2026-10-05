import heroLogo from "../assets/hero.png";
import "../styles/Navbar.css";

function Navbar(){

    const name =
    localStorage.getItem("name") || "Amrita Singh";


    const role =
    localStorage.getItem("role") || "USER";


    const savedImage =
    localStorage.getItem("profilePic");


    const profilePic =
    savedImage && savedImage !== "null"
    ?
        savedImage.startsWith("http")
        ?
            savedImage
        :
            `http://localhost:8082${savedImage}`
    :
        "https://i.pravatar.cc/150?img=12";


    return(

        <nav className="navbar">


            {/* LEFT SIDE */}

            <div className="navbar-left">


                <img
                    src={heroLogo}
                    alt="Keystone Logo"
                    className="navbar-logo"
                />


                <div className="brand">

                    <h2>
                        Keystone
                    </h2>


                    <span>
                        Enterprise Suite
                    </span>

                </div>


            </div>




            {/* RIGHT SIDE */}

            <div className="navbar-right">


                <div className="user-info">


                    <span className="user-name">
                        {name}
                    </span>


                    <span className="user-role">
                        {role}
                    </span>


                </div>



                <img
                    src={profilePic}
                    alt="Profile"
                    className="navbar-profile"
                />


            </div>


        </nav>

    );

}

export default Navbar;