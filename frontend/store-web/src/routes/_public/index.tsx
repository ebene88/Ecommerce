import { CategoryCard } from "@/components/CategoryCard";
import { ProductCard } from "@/components/ProductCard";
import { Input } from "@/components/ui/input";
import { createFileRoute } from "@tanstack/react-router";

export const Route = createFileRoute("/_public/")({
  component: RouteComponent,
});

function RouteComponent() {
  return (
    <div className="flex flex-col gap-16">
      {/* ================= SEARCH / HERO ================= */}
      <section className="w-full bg-muted py-16">
        <div className="mx-auto max-w-3xl px-4 text-center">
          <h1 className="mb-4 text-3xl font-semibold text-foreground">
            What are you looking for?
          </h1>

          <p className="mb-6 text-muted-foreground">
            Search thousands of products from trusted sellers
          </p>

          <Input
            className="mx-auto max-w-xl"
            placeholder="Search products, brands, categories..."
          />
        </div>
      </section>

      {/* ================= CATALOG ================= */}
      <section className="w-full">
        <div className="mx-auto max-w-7xl px-4 sm:px-6 lg:px-8">
          <div className="grid grid-cols-12 gap-8">
            {/* ---------- Categories ---------- */}
            <aside className="col-span-12 lg:col-span-3">
              <CategoryCard />
            </aside>

            {/* ---------- Products ---------- */}
            <section className="col-span-12 lg:col-span-9">
              <ProductCard />
            </section>
          </div>
        </div>
      </section>
    </div>
  );
}
