export const getUserFromToken = () => {
  const token = localStorage.getItem("token");

  if (!token) {
    return null;
  }

  try {
    const payload = token.split(".")[1];

    if (!payload) {
      return null;
    }

    const base64 = payload
      .replace(/-/g, "+")
      .replace(/_/g, "/");

    const decodedPayload = JSON.parse(
      atob(base64)
    );

    return decodedPayload;

  } catch (error) {
    console.error(
      "Unable to decode JWT:",
      error
    );

    return null;
  }
};

export const getUserRole = () => {
  const user = getUserFromToken();

  if (!user) {
    return null;
  }

  return user.role || null;
};