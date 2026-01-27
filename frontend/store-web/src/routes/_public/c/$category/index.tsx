import { InfiniteProductGrid } from "@/components/ProductCard";
import { $product } from "@/queries/product.query";
import { createFileRoute, useNavigate } from "@tanstack/react-router";

export const Route = createFileRoute("/_public/c/$category/")({
  component: RouteComponent,
});

function RouteComponent() {
  const { category } = Route.useParams();
  const navigate = useNavigate();

  const {
    data,
    fetchNextPage,
    hasNextPage,
    isFetchingNextPage,
    isLoading,
    isError,
  } = $product.GetByCategory({
    categoryId: Number(category),
  });

  const allProducts = data?.pages.flatMap((page) => page.content) ?? [];

  return (
    <div className="mx-auto max-w-7xl px-4 py-10">
      <h2 className="mb-6 text-xl font-semibold">Category: {category}</h2>
      <InfiniteProductGrid
        products={allProducts}
        fetchNextPage={fetchNextPage}
        hasNextPage={hasNextPage}
        isFetchingNextPage={isFetchingNextPage}
        isLoading={isLoading}
        isError={isError}
        onViewDetails={(id) =>
          navigate({
            to: `/products/$productId`,
            params: { productId: id },
          })
        }
      />
    </div>
  );
}
