import { InfiniteProductGrid } from "@/components/ProductCard";
import { $product } from "@/queries/product.query";
import { createFileRoute, useNavigate } from "@tanstack/react-router";
import z from "zod";

export const Route = createFileRoute("/_public/search")({
  validateSearch: z.object({
    q: z.string().optional(),
    size: z.number().default(20),
  }),
  component: SearchPage,
});
export function SearchPage() {
  const navigate = useNavigate();
  const search = Route.useSearch();

  const {
    data,
    fetchNextPage,
    hasNextPage,
    isFetchingNextPage,
    isLoading,
    isError,
  } = $product.Search({
    keyword: search.q,
    size: search.size,
  });

  const products = data?.pages.flatMap((page) => page.content) ?? [];

  if (!search.q) {
    return (
      <div className="text-center py-20 text-muted-foreground">
        Start typing to search
      </div>
    );
  }

  return (
    <div className="mx-auto max-w-7xl px-4 py-10">
      <h2 className="mb-6 text-xl font-semibold">Results for “{search.q}”</h2>

      <InfiniteProductGrid
        products={products}
        fetchNextPage={fetchNextPage}
        hasNextPage={hasNextPage}
        isFetchingNextPage={isFetchingNextPage}
        isLoading={isLoading}
        isError={isError}
        onViewDetails={(id) =>
          navigate({
            to: "/products/$productId",
            params: { productId: id },
          })
        }
      />
    </div>
  );
}
