import { isAuthenticated, login } from "@/lib/auth";
import { createFileRoute, Outlet } from "@tanstack/react-router";

export const Route = createFileRoute("/_protected")({
  beforeLoad: async () => {
    if (!isAuthenticated()) {
      login(); // redirect to Keycloak
      throw new Error("Redirecting to Keycloak login");
    }
  },
  component: ProtectedLayout,
});

function ProtectedLayout() {
  return <Outlet />;
}
