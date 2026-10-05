import { useState } from "react";
import Sidebar from "../components/Sidebar";
import { uploadProfilePic } from "../services/userService";

import "../styles/Profile.css";

function Profile() {
  const [name, setName] = useState(
    localStorage.getItem("name") || ""
  );

  const email = localStorage.getItem("email") || "";

  const role = localStorage.getItem("role") || "";

  const savedProfilePic = localStorage.getItem("profilePic");

  const [profilePic, setProfilePic] = useState(
    savedProfilePic && savedProfilePic !== "null"
      ? savedProfilePic
      : "https://i.pravatar.cc/150?img=12"
  );

  const [selectedFile, setSelectedFile] = useState(null);
const [currentPassword, setCurrentPassword] = useState("");
const [newPassword, setNewPassword] = useState("");
  const handleImageUpload = (event) => {
    const file = event.target.files[0];

    if (!file) return;

    setSelectedFile(file);

    setProfilePic(URL.createObjectURL(file));
  };

  const handleSave = async () => {
    if (!selectedFile) {
    alert("Please select an image first.");
    return;
  }
    try {
      const response = await uploadProfilePic(
        email,
        selectedFile
      );

      localStorage.setItem(
        "profilePic",
        response.profilePic
      );

         setProfilePic(response.profilePic);

      alert("Profile updated successfully");

      window.location.reload();
    } catch (error) {
      console.error(error);

      alert("Upload failed");
    }
  };

  return (
    <>
      <Sidebar />

      <div className="profile-container">
        <div className="profile-card">

          <div className="profile-header">
            <label htmlFor="profile-upload">
           <img
  src={
    profilePic.startsWith("blob:")
      ? profilePic
      : profilePic.startsWith("http")
      ? profilePic
      : `http://localhost:8082${profilePic}`
  }
  alt="Profile"
  className="profile-image"
/>  
        <div className="camera-icon">
                📷
              </div>
            </label>

            <input
              type="file"
              id="profile-upload"
              accept="image/*"
              hidden
              onChange={handleImageUpload}
            />

            <h2>{name}</h2>

            <p>{role}</p>
          </div>

          <div className="field">
            <label>Name</label>

            <input
              type="text"
              value={name}
              onChange={(e) =>
                setName(e.target.value)
              }
            />
          </div>

          <div className="field">
            <label>Email</label>

            <input
              type="email"
              value={email}
              disabled
            />
          </div>

          <div className="field">
            <label>Role</label>

            <input
              type="text"
              value={role}
              disabled
            />
          </div>

          <button
            className="save-btn"
            onClick={handleSave}
          >
            Update Profile
          </button>

        </div>
      </div>
    </>
  );
}

export default Profile;