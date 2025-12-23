import { $category } from "@/queries/category.query";
import type { TCategory, TCategoryResponse } from "@/schema/category.schema";
import { ChevronRight } from "lucide-react";
import { useState } from "react";

export const CategoryCard = () => {
  const { data } = $category.GetAll();
  const [activeCategory, setActiveCategory] =
    useState<TCategoryResponse | null>(null);

  return (
    <div className="lg:sticky lg:top-24 relative w-64">
      {/* Hover Zone Wrapper */}
      <div className="relative" onMouseLeave={() => setActiveCategory(null)}>
        {/* Main Category Card */}
        <div className="rounded-xl border border-border bg-card p-4 shadow-sm">
          <h2 className="mb-4 text-lg font-semibold">Categories</h2>

          <ul className="space-y-1">
            {data.data.map((category: TCategoryResponse) => {
              const hasChildren = category.children.length > 0;

              return (
                <li
                  key={category.id}
                  onMouseEnter={() =>
                    hasChildren && setActiveCategory(category)
                  }
                  className="group flex items-center justify-between rounded-md px-3 py-2 text-sm cursor-pointer text-muted-foreground hover:bg-muted hover:text-foreground transition"
                >
                  <span>{category.name}</span>

                  {hasChildren && (
                    <ChevronRight className="h-4 w-4 opacity-0 group-hover:opacity-100 transition" />
                  )}
                </li>
              );
            })}
          </ul>
        </div>

        {/* Subcategory Overlay */}
        {activeCategory && (
          <div
            className="
              absolute top-0 left-full ml-2
              w-64 rounded-xl border border-border bg-card p-4 shadow-lg
              z-50
            "
          >
            <h3 className="mb-3 text-sm font-semibold">
              {activeCategory.name}
            </h3>

            <ul className="space-y-1">
              {activeCategory.children.map((sub: TCategory) => (
                <li
                  key={sub.id}
                  className="cursor-pointer rounded-md px-3 py-2 text-sm text-muted-foreground hover:bg-muted hover:text-foreground transition"
                >
                  {sub.name}
                </li>
              ))}
            </ul>
          </div>
        )}
      </div>
    </div>
  );
};
