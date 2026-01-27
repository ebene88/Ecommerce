import { $product } from "@/queries/product.query";
import type { TProduct } from "@/schema/product.schema";
import { Link } from "@tanstack/react-router";
import { Button } from "./ui/button";

type SimilarProductsProps = {
  productId: string;
};

export function SimilarProducts({ productId }: SimilarProductsProps) {
  const { data, isLoading, isError } = $product.GetSimilarById(productId);

  if (isLoading) {
    return <div className="mt-8 text-muted-foreground">Loading products…</div>;
  }

  if (isError || !data.items.length) {
    return (
      <div className="mt-8 text-muted-foreground">
        No similar products found.
      </div>
    );
  }

  return (
    <section className="mt-8 w-full">
      <h2 className="mb-4 text-lg font-semibold">
        Similar / Recommended Products
      </h2>

      <div className="grid grid-cols-2 sm:grid-cols-3 md:grid-cols-3 gap-4">
        {data.items.map((item: TProduct) => (
          <div
            key={item.id}
            className="group overflow-hidden rounded-xl border border-border bg-card shadow-sm transition hover:shadow-md"
          >
            {/* Image */}
            <div className="aspect-square bg-muted overflow-hidden">
              <img
                src={item.imageUrls?.[0] ?? "/placeholder-image.png"}
                alt={item.name}
                loading="lazy"
                className="h-full w-full object-cover transition-transform group-hover:scale-105"
              />
            </div>

            {/* Content */}
            <div className="p-4 space-y-2">
              <h3 className="text-sm font-medium">{item.name}</h3>

              <p className="text-sm text-muted-foreground line-clamp-2">
                {item.description}
              </p>

              <div className="flex items-center justify-between pt-3">
                <span className="text-lg font-semibold">
                  ${item.price.toFixed(2)}
                </span>

                <Button asChild size="sm">
                  <Link
                    to="/products/$productId"
                    params={{ productId: item.id }}
                  >
                    View Details
                  </Link>
                </Button>
              </div>
            </div>
          </div>
        ))}
      </div>
    </section>
  );
}
