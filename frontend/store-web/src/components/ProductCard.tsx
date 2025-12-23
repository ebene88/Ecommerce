import { $product } from "@/queries/product.query";
import type { TProduct } from "@/schema/product.schema";
import { Link } from "@tanstack/react-router";
import { Button } from "./ui/button";

export const ProductCard = () => {
  const { data, isLoading, isError } = $product.GetAll();

  if (isLoading) return <p>Loading...</p>;
  if (isError || !data?.data?.content) return <p>Error loading products</p>;

  return (
    <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-6">
      {data.data.content.map((item: TProduct) => (
        <div
          key={item.id}
          className="group overflow-hidden rounded-xl border border-border bg-card shadow-sm hover:shadow-md transition"
        >
          {/* Image */}
          <div className="aspect-square bg-muted overflow-hidden">
            <img
              src={item.imageUrls[0]}
              alt={item.name}
              className="h-full w-full object-cover transition-transform group-hover:scale-105"
            />
          </div>

          {/* Content */}
          <div className="p-4 space-y-2">
            <h3 className="text-sm font-medium text-foreground">{item.name}</h3>

            <p className="text-sm text-muted-foreground line-clamp-2">
              {item.description}
            </p>

            <div className="flex items-center justify-between pt-3">
              <span className="text-lg font-semibold text-foreground">
                ${item.price.toFixed(2)}
              </span>

              <Button className="rounded-md bg-primary px-4 py-2 text-sm text-primary-foreground hover:opacity-90 transition">
                <Link
                  to={`/products/$productId`}
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
  );
};
