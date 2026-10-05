import api from "./api";


// Upload profile picture

export const uploadProfilePic = async (email, file) => {

  const formData = new FormData();

  formData.append("file", file);


  const response = await api.post(
    `/users/profile/upload/${email}`,
    formData,
    {
      headers: {
        "Content-Type": "multipart/form-data",
      },
    }
  );


  return response.data;

};



// Get all users

export const getAllUsers = async () => {

  const response = await api.get(
    "/users"
  );


  return response.data;

};