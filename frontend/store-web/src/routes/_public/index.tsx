import { CategoryCard } from "@/components/CategoryCard";
import { InfiniteProductGrid } from "@/components/ProductCard";
import { Input } from "@/components/ui/input";
import { $product } from "@/queries/product.query";
import { createFileRoute, useNavigate } from "@tanstack/react-router";
import { useDebounce } from "@uidotdev/usehooks";
import { useEffect, useState } from "react";

export const Route = createFileRoute("/_public/")({ component: HomePage });

export function HomePage() {
  const navigate = useNavigate();
  const [searchValue, setSearchValue] = useState("");
  const debouncedSearch = useDebounce(searchValue, 300);

  const [showSuggestions, setShowSuggestions] = useState(false);
  const [suggestions, setSuggestions] = useState<string[]>([]);

  // Fetch search suggestions
  const { data: suggestData, isFetching: suggestLoading } =
    $product.SearchSuggest({ q: debouncedSearch });

  useEffect(() => {
    if (debouncedSearch && suggestData?.length) {
      setSuggestions(suggestData);
      setShowSuggestions(true);
    } else {
      setSuggestions([]);
      setShowSuggestions(false);
    }
  }, [debouncedSearch, suggestData]);

  const handleSuggestionClick = (suggest: string) => {
    setSearchValue(suggest);
    setShowSuggestions(false);
    navigate({ to: "/search", search: { q: suggest, page: 0, size: 20 } });
  };

  // Infinite products
  const useGetAllProducts = $product.GetAll;
  const {
    data,
    fetchNextPage,
    hasNextPage,
    isFetchingNextPage,
    isLoading,
    isError,
  } = useGetAllProducts();

  const allProducts = data?.pages.flatMap((page) => page.content) ?? [];

  return (
    <div className="flex flex-col gap-16">
      <section className="w-full bg-muted py-16">
        <div className="mx-auto max-w-3xl px-4 text-center relative">
          <h1 className="mb-4 text-3xl font-semibold text-foreground">
            What are you looking for?
          </h1>

          <p className="mb-6 text-muted-foreground">
            Search thousands of products from trusted sellers
          </p>

          <Input
            className="mx-auto max-w-xl"
            placeholder="Search products, brands, categories..."
            value={searchValue}
            onChange={(e) => setSearchValue(e.target.value)}
          />

          {showSuggestions && suggestions.length > 0 && (
            <ul
              className="
      absolute left-0 right-0 mx-auto mt-2 max-w-xl
      rounded-lg border border-border
      bg-background shadow-md
      z-20
      max-h-64 overflow-y-auto
    "
            >
              {suggestLoading && (
                <li className="px-4 py-2 text-sm text-muted-foreground text-left">
                  Loading…
                </li>
              )}

              {!suggestLoading &&
                suggestions.map((sug, idx) => (
                  <li
                    key={idx}
                    onClick={() => handleSuggestionClick(sug)}
                    className="
            px-4 py-2 text-sm text-foreground text-left
            cursor-pointer
            transition-colors duration-150
            hover:bg-foreground hover:text-background
          "
                  >
                    {sug}
                  </li>
                ))}
            </ul>
          )}
        </div>
      </section>

      {/* CATALOG */}
      <section className="w-full">
        <div className="mx-auto max-w-7xl px-4 sm:px-6 lg:px-8">
          <div className="grid grid-cols-12 gap-8">
            <aside className="col-span-12 lg:col-span-3">
              <CategoryCard />
            </aside>
            <section className="col-span-12 lg:col-span-9">
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
            </section>
          </div>
        </div>
      </section>
    </div>
  );
}
