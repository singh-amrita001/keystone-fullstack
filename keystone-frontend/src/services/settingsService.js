import api from "./api";

export const changePassword = async (email, password) => {

  const response = await api.put(

    `/users/change-password/${email}`,

    {
      password: password
    }

  );

  return response.data;
};