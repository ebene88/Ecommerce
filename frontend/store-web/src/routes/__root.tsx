import { ThemeToggle } from "@/components/theme-toggle";
import {
  Breadcrumb,
  BreadcrumbItem,
  BreadcrumbLink,
  BreadcrumbList,
  BreadcrumbSeparator,
} from "@/components/ui/breadcrumb";

import { Separator } from "@/components/ui/separator";
import {
  SidebarInset,
  SidebarProvider,
  SidebarTrigger,
} from "@/components/ui/sidebar";
import { useThemes } from "@/hooks/use-theme";

import { cn } from "@/lib/utils";
import {
  createRootRoute,
  Link,
  Outlet,
  useLocation,
} from "@tanstack/react-router";
import React from "react";

export const Route = createRootRoute({
  component: RootComponent,
});

function RootComponent() {
  const { theme } = useThemes();

  return (
    <div className={cn(theme === "dark" ? "dark" : "")}>
      <div id="dialog-wrapper" />
      <div className=" flex flex-col w-full min-h-svh overflow-hidden">
        <SidebarProvider
          style={{ "--sidebar-width": "18rem" } as React.CSSProperties}
        >
          <SidebarInset className="rounded-none w-full max-w-full">
            <header
              className="
            sticky top-0 z-50
            flex justify-between items-center
            p-3 border-b
            w-full max-w-full
            bg-background
          "
            >
              <div className="flex items-center gap-1 sm:gap-2 min-w-0">
                <SidebarTrigger className="-ml-1 text-foreground" />
                <Separator orientation="vertical" className="h-4" />
                <Breadcrumbs />
              </div>
              <div className="flex flex-wrap gap-2">
                <ThemeToggle />
              </div>
            </header>
            <div className="px-5 py-10">
              <Outlet />
            </div>
          </SidebarInset>
        </SidebarProvider>
      </div>
    </div>
  );
}

function Breadcrumbs() {
  const { pathname } = useLocation();
  const pathSegments = pathname.split("/").filter((segment) => segment);

  return (
    <Breadcrumb>
      <div className="max-w-[100px] sm:max-w-none overflow-hidden sm:overflow-visible">
        <BreadcrumbList className="flex flex-nowrap sm:flex-wrap">
          {pathSegments.length > 0 ? (
            pathSegments.map((segment, index) => {
              const to =
                index === 0
                  ? `/${segment}`
                  : `${pathSegments.slice(0, index).join("/")}/${segment}`;

              return (
                <React.Fragment key={segment + "-" + index}>
                  <BreadcrumbItem key={`breadcrumb-item-${segment}-${index}`}>
                    <BreadcrumbLink
                      asChild
                      className="
    font-mono capitalize text-muted-foreground text-[13px] leading-tight
    max-w-[100px] truncate overflow-hidden text-ellipsis whitespace-nowrap
    sm:max-w-none sm:truncate-none sm:overflow-visible sm:whitespace-normal
    sm:text-sm md:text-base lg:text-base
  "
                    >
                      <Link to={to}>{segment}</Link>
                    </BreadcrumbLink>
                  </BreadcrumbItem>
                  {index !== pathSegments.length - 1 && (
                    <BreadcrumbSeparator
                      key={`breadcrumb-separator-${segment}-${index}`} // Unique key for separators
                      className="hidden md:block"
                    />
                  )}
                </React.Fragment>
              );
            })
          ) : (
            <BreadcrumbItem key="overview">
              <BreadcrumbLink
                asChild
                className="text-muted-foreground capitalize font-mono   text-[13px] leading-tight max-w-[180px] truncate break-words 
               sm:text-sm sm:max-w-[250px] sm:truncate-none
              md:text-base lg:text-base
               md:truncate-none"
              >
                <Link to={`/`}>Overview</Link>
              </BreadcrumbLink>
            </BreadcrumbItem>
          )}
        </BreadcrumbList>
      </div>
    </Breadcrumb>
  );
}

export default Breadcrumbs;
