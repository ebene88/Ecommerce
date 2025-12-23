import { setAccessToken } from "./auth";
import { keycloak } from "./keycloak";

export async function initKeycloak() {
  const authenticated = await keycloak.init({
    onLoad: "check-sso", // public pages still work
    pkceMethod: "S256",
    checkLoginIframe: false,
  });

  if (authenticated) {
    setAccessToken(keycloak.token ?? null);
  }

  // 🔁 Handle token refresh
  keycloak.onTokenExpired = async () => {
    try {
      const refreshed = await keycloak.updateToken(30);
      if (refreshed) {
        setAccessToken(keycloak.token ?? null);
      }
    } catch {
      setAccessToken(null);
      keycloak.logout();
    }
  };

  return authenticated;
}
