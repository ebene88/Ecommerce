import axios from "axios";
import { getAccessToken } from "./auth";

export const $api = axios.create({
  baseURL: import.meta.env.VITE_API_URL,
  headers: {
    "Content-Type": "application/json",
  },
  withCredentials: true, // important if using cookies / Keycloak refresh
});

$api.interceptors.request.use((config) => {
  const token = getAccessToken();

  if (token) {
    config.headers.Authorization = `Bearer ${token}`;
  }

  return config;
});

$api.interceptors.response.use(
  (response) => response,
  (error) => {
    if (error.response?.status === 401) {
      // Optional: logout or refresh
      console.warn("Unauthorized — logging out");
      // keycloak.logout();
    }

    return Promise.reject(error);
  }
);
