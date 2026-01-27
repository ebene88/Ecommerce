import { ProductField } from "@/components/form/product.form";
import { createFileRoute } from "@tanstack/react-router";

export const Route = createFileRoute("/_protected/product/new")({
  component: RouteComponent,
});

function RouteComponent() {
  return (
    <>
      <ProductField />
    </>
  );
}
