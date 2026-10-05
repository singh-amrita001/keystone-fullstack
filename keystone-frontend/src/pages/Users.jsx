import { useEffect, useState } from "react";

import Sidebar from "../components/Sidebar";
import Navbar from "../components/Navbar";
import { getAllUsers } from "../services/userService";

import "../styles/Users.css";





function Users() {


  const [users, setUsers] = useState([]);

  const [loading, setLoading] = useState(true);



  useEffect(() => {


    const fetchUsers = async () => {


      try {


        const data = await getAllUsers();


        console.log("Users API Response:", data);


        setUsers(data);


      }
      catch(error) {


        console.error(
          "Failed to load users:",
          error
        );


      }
      finally {


        setLoading(false);


      }


    };



    fetchUsers();


  }, []);




  const getProfileImage = (profilePic) => {


    if(!profilePic)
    {

      return "https://i.pravatar.cc/150?img=12";

    }



    if(profilePic.startsWith("http"))
    {

      return profilePic;

    }



    return `http://localhost:8082${profilePic}`;


  };





  return (

    <>

<Navbar />
      <Sidebar />



      <div className="users-container">



        <div className="users-card">

 <h1>
        Users Management
      </h1>

      <p>
        Manage system users and roles.
      </p>

      {
        loading ?

        (
          <h3>
            Loading users...
          </h3>
        )

        :

        (

        <table>

          <thead>
            <tr>
              <th>Profile</th>
              <th>Name</th>
              <th>Email</th>
              <th>Role</th>
              <th>Status</th>
            </tr>
          </thead>


          <tbody>

          {
            users.map((user)=>(

              <tr key={user.id}>

                <td>
                  <img
                    src={getProfileImage(user.profilePic)}
                    alt="profile"
                    className="user-avatar"
                  />
                </td>

                <td>{user.name}</td>

                <td>{user.email}</td>

                <td>
                  <span className={
                    user.role==="ADMIN"
                    ?
                    "admin-badge"
                    :
                    "user-badge"
                  }>
                    {user.role}
                  </span>
                </td>

                <td>
                  <span className="active-status">
                    Active
                  </span>
                </td>

              </tr>

            ))
          }

          </tbody>

        </table>

        )

      }


    </div>

  </div>

</>
);



}


export default Users;