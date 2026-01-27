import { ProductField } from "@/components/form/product.form";
import { Icons } from "@/components/icons";
import { createFileRoute } from "@tanstack/react-router";

export const Route = createFileRoute("/_protected/sellerProfile")({
  component: RouteComponent,
});

function RouteComponent() {
  return (
    <>
      <div className="gap-6 p-6 border-b bg-card/50 flex justify-between items-center">
        <div className="col-span-3 flex items-center gap-4">
          <Icons.user className="w-8 h-8 md:w-10 md:h-10 bg-primary p-1 rounded-md text-primary-foreground" />
          <h3 className="text-sm md:text-lg font-medium">Seller Profile</h3>
        </div>

        <div className="md:flex gap-4 items-center font-mono"></div>
      </div>
      <ProductField />
    </>
  );
}
