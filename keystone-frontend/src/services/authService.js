import api from "./api";

// LOGIN
export const login = async (email, password) => {
  const response = await api.post("/auth/login", {
    email,
    password,
  });

  console.log("LOGIN RESPONSE:", response.data);

  localStorage.setItem("token", response.data.token);
  console.log("Saved name:", response.data.name);
  localStorage.setItem("name", response.data.name);
  localStorage.setItem("email", response.data.email);
  localStorage.setItem("role", response.data.role);
localStorage.setItem(
    "profilePic",
    response.data.profilePic
);
  console.log("LOCAL STORAGE NAME:", localStorage.getItem("name"));

  return response.data;
};

// LOGOUT
export const logout = () => {
  localStorage.removeItem("token");
  localStorage.removeItem("name");
  localStorage.removeItem("email");
  localStorage.removeItem("role");
};

// CHECK LOGIN
export const isAuthenticated = () => {
  return !!localStorage.getItem("token");
};

// GET CURRENT USER
export const getCurrentUser = () => {
  return {
    name: localStorage.getItem("name"),
    email: localStorage.getItem("email"),
    role: localStorage.getItem("role"),
  };
};