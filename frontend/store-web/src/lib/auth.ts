import { keycloak } from "./keycloak";

let accessToken: string | null = null;

export const setAccessToken = (token: string | null) => {
  accessToken = token;
};

export const getAccessToken = () => accessToken;

export const isAuthenticated = () => !!accessToken;

export const login = () => {
  keycloak.login({
    redirectUri: window.location.href, // return to requested page
  });
};

export const logout = () => {
  keycloak.logout({
    redirectUri: window.location.origin,
  });
};
