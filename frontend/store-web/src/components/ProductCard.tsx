import type { TProduct } from "@/schema/product.schema";
import { Link } from "@tanstack/react-router";
import { useCallback, useEffect, useRef } from "react";
import { Button } from "./ui/button";

type InfiniteProductGridProps = {
  products: TProduct[];
  fetchNextPage: () => void;
  hasNextPage?: boolean;
  isFetchingNextPage?: boolean;
  onViewDetails?: (id: string) => void;
  isLoading?: boolean;
  isError?: boolean;
};

export const InfiniteProductGrid = ({
  products = [],
  fetchNextPage,
  hasNextPage,
  isFetchingNextPage,
  onViewDetails,
  isLoading,
  isError,
}: InfiniteProductGridProps) => {
  const loadMoreRef = useRef<HTMLDivElement>(null);

  const handleObserver = useCallback(() => {
    if (hasNextPage && !isFetchingNextPage) {
      fetchNextPage();
    }
  }, [hasNextPage, isFetchingNextPage, fetchNextPage]);

  const observerRef = useRef<IntersectionObserver | null>(null);

  useEffect(() => {
    const node = loadMoreRef.current;
    if (!node) return;

    observerRef.current = new IntersectionObserver(
      (entries) => {
        if (entries[0].isIntersecting) {
          console.log("Observer intersected! Fetching next page...");
          handleObserver();
        }
      },
      { root: null, rootMargin: "0px", threshold: 0.1 }
    );

    observerRef.current.observe(node);

    return () => observerRef.current?.disconnect();
  }, [handleObserver]);

  // Skeletons
  const renderSkeletons = (count: number) =>
    Array.from({ length: count }).map((_, idx) => (
      <div
        key={idx}
        className="animate-pulse group overflow-hidden rounded-xl border border-border bg-card shadow-sm"
      >
        <div className="aspect-square " />
        <div className="p-4 space-y-2">
          <div className="h-4 w-3/4  rounded" />
          <div className="h-4 w-full  rounded" />
          <div className="flex items-center justify-between pt-3">
            <div className="h-5 w-16  rounded" />
            <div className="h-8 w-20  rounded" />
          </div>
        </div>
      </div>
    ));

  if (isLoading)
    return (
      <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-6">
        {renderSkeletons(9)}
      </div>
    );
  if (isError)
    return (
      <p className="text-center py-4 text-red-500">Error loading products</p>
    );
  if (products.length === 0)
    return <p className="text-center py-4">No products found</p>;

  return (
    <>
      <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-6">
        {products.map((item) => (
          <div
            key={item.id}
            className="group overflow-hidden rounded-xl border border-border bg-card shadow-sm hover:shadow-md transition"
          >
            <div className="aspect-square bg-muted overflow-hidden">
              <img
                src={item.imageUrls[0]}
                alt={item.name}
                className="h-full w-full object-cover transition-transform group-hover:scale-105"
              />
            </div>

            <div className="p-4 space-y-2">
              <h3 className="text-sm font-medium text-foreground">
                {item.name}
              </h3>
              {item.description && (
                <p className="text-sm text-muted-foreground line-clamp-2">
                  {item.description}
                </p>
              )}
              <div className="flex items-center justify-between pt-3">
                <span className="text-lg font-semibold text-foreground">
                  ${item.price.toFixed(2)}
                </span>
                <Button
                  className="rounded-md bg-primary px-4 py-2 text-sm text-primary-foreground hover:opacity-90 transition"
                  onClick={() => onViewDetails?.(item.id)}
                >
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

        {isFetchingNextPage && renderSkeletons(3)}
      </div>

      <div ref={loadMoreRef} className="h-32 w-full bg-transparent" />
    </>
  );
};
