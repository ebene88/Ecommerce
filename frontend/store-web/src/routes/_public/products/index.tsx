import { createFileRoute } from "@tanstack/react-router";

export const Route = createFileRoute("/_public/products/")({
  component: RouteComponent,
});

function RouteComponent() {
  return (
    <div className="font-bold text-lg">
      Hello "/_public/products/"! im public
    </div>
  );
}
