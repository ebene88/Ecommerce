import { QueryClient, QueryClientProvider } from "@tanstack/react-query";
import { RouterProvider } from "@tanstack/react-router";
import { StrictMode } from "react";
import { createRoot } from "react-dom/client";
import { ThemeProvider } from "./hooks/use-theme";
import { initKeycloak } from "./lib/keycloak-init";
import { router } from "./router";
import "./style/index.css";
const queryClient = new QueryClient();

async function bootstrap() {
  // ✅ Initialize Keycloak silently
  await initKeycloak();

  createRoot(document.getElementById("root")!).render(
    <StrictMode>
      <QueryClientProvider client={queryClient}>
        <ThemeProvider>
          <RouterProvider router={router} />
        </ThemeProvider>
      </QueryClientProvider>
    </StrictMode>
  );
}

bootstrap();
