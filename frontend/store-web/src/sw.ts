/// <reference lib="webworker" />

import {
  cleanupOutdatedCaches,
  createHandlerBoundToURL,
  precacheAndRoute,
} from "workbox-precaching";

import { NavigationRoute, registerRoute } from "workbox-routing";
import { CacheFirst, NetworkFirst } from "workbox-strategies";

declare let self: ServiceWorkerGlobalScope;

self.addEventListener("message", (event) => {
  if (event.data && event.data.type === "SKIP_WAITING") self.skipWaiting();
});

// Precache build assets
precacheAndRoute(self.__WB_MANIFEST);

// Clean old caches
cleanupOutdatedCaches();

let allowlist;
if (import.meta.env.DEV) allowlist = [/^\/$/];

// SPA offline navigation
registerRoute(
  new NavigationRoute(createHandlerBoundToURL("index.html"), { allowlist })
);

// ✅ Cache API calls
registerRoute(
  ({ url }) => url.pathname.startsWith("/api"),
  new NetworkFirst({
    cacheName: "api-cache",
    networkTimeoutSeconds: 3,
  })
);

// ✅ Cache images
registerRoute(
  ({ request }) => request.destination === "image",
  new CacheFirst({
    cacheName: "image-cache",
  })
);
