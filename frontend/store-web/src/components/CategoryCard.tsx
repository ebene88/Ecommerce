// import { $category } from "@/queries/category.query";
// import type { TCategory, TCategoryResponse } from "@/schema/category.schema";
// import { useNavigate } from "@tanstack/react-router";
// import { ChevronRight } from "lucide-react";
// import { useEffect, useState } from "react";

// export const CategoryCard = () => {
//   const { data } = $category.GetAll();
//   const [activeCategory, setActiveCategory] =
//     useState<TCategoryResponse | null>(null);
//   const [hovered, setHovered] = useState(false);
//   const navigate = useNavigate();

//   // Small delay to prevent flicker
//   useEffect(() => {
//     if (!hovered) {
//       const timer = setTimeout(() => setActiveCategory(null), 120);
//       return () => clearTimeout(timer);
//     }
//   }, [hovered]);

//   return (
//     <div className="lg:sticky lg:top-24 relative w-64">
//       <div
//         className="relative"
//         onMouseEnter={() => setHovered(true)}
//         onMouseLeave={() => setHovered(false)}
//       >
//         {/* Main Category Card */}
//         <div className="rounded-xl border border-border bg-card p-4 shadow-sm">
//           <h2 className="mb-4 text-lg font-semibold">Categories</h2>

//           <ul className="space-y-1">
//             {data.data.map((category: TCategoryResponse) => {
//               const hasChildren = category.children.length > 0;

//               return (
//                 <li
//                   key={category.id}
//                   onMouseEnter={() =>
//                     hasChildren && setActiveCategory(category)
//                   }
//                   className="group flex items-center justify-between rounded-md px-3 py-2 text-sm cursor-pointer text-muted-foreground hover:bg-muted hover:text-foreground transition-colors"
//                 >
//                   <span
//                     onClick={() =>
//                       navigate({
//                         to: "/c/$category",
//                         params: { category: String(category.id) },
//                       })
//                     }
//                   >
//                     {category.name}
//                   </span>

//                   {hasChildren && (
//                     <ChevronRight className="h-4 w-4 opacity-0 group-hover:opacity-100 transition-opacity duration-200" />
//                   )}
//                 </li>
//               );
//             })}
//           </ul>
//         </div>

//         {/* Subcategory Panel (Animated) */}
//         <div
//           className={`
//             absolute top-0 left-full ml-2
//             w-64 rounded-xl border border-border bg-card p-4 shadow-lg
//             z-50
//             transition-all duration-200 ease-out
//             ${
//               activeCategory
//                 ? "opacity-100 translate-x-0 pointer-events-auto"
//                 : "opacity-0 translate-x-2 pointer-events-none"
//             }
//           `}
//         >
//           {activeCategory && (
//             <>
//               <h3 className="mb-3 text-sm font-semibold">
//                 {activeCategory.name}
//               </h3>

//               <ul className="space-y-1">
//                 {activeCategory.children.map((sub: TCategory) => (
//                   <li
//                     key={sub.id}
//                     onClick={(e) => {
//                       e.preventDefault();
//                       e.stopPropagation();
//                       navigate({
//                         to: "/c/$category/$subcategory",
//                         params: {
//                           category: String(activeCategory.id),
//                           subcategory: String(sub.id),
//                         },
//                       });
//                     }}
//                     className="cursor-pointer rounded-md px-3 py-2 text-sm text-muted-foreground hover:bg-muted hover:text-foreground transition-colors"
//                   >
//                     {sub.name}
//                   </li>
//                 ))}
//               </ul>
//             </>
//           )}
//         </div>
//       </div>
//     </div>
//   );
// };

import { $category } from "@/queries/category.query";
import type { TCategory, TCategoryResponse } from "@/schema/category.schema";
import { Link } from "@tanstack/react-router";
import { ChevronRight } from "lucide-react";
import { useEffect, useState } from "react";

export const CategoryCard = () => {
  const { data } = $category.GetAll();
  const [activeCategory, setActiveCategory] =
    useState<TCategoryResponse | null>(null);
  const [hovered, setHovered] = useState(false);

  useEffect(() => {
    if (!hovered) {
      const timer = setTimeout(() => setActiveCategory(null), 120);
      return () => clearTimeout(timer);
    }
  }, [hovered]);

  return (
    <div className="lg:sticky lg:top-24 relative w-64">
      <div
        className="relative"
        onMouseEnter={() => setHovered(true)}
        onMouseLeave={() => setHovered(false)}
      >
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
                  className="group flex items-center justify-between rounded-md px-3 py-2 text-sm text-muted-foreground hover:bg-muted hover:text-foreground transition-colors"
                >
                  <Link
                    to="/c/$category"
                    params={{ category: String(category.id) }}
                    className="flex-1"
                  >
                    {category.name}
                  </Link>

                  {hasChildren && (
                    <ChevronRight className="h-4 w-4 opacity-0 group-hover:opacity-100 transition-opacity duration-200" />
                  )}
                </li>
              );
            })}
          </ul>
        </div>

        {/* Subcategory Panel */}
        <div
          className={`
            absolute top-0 left-full ml-2
            w-64 rounded-xl border border-border bg-card p-4 shadow-lg
            z-50
            transition-all duration-200 ease-out
            ${
              activeCategory
                ? "opacity-100 translate-x-0 pointer-events-auto"
                : "opacity-0 translate-x-2 pointer-events-none"
            }
          `}
        >
          {activeCategory && (
            <>
              <h3 className="mb-3 text-sm font-semibold">
                {activeCategory.name}
              </h3>

              <ul className="space-y-1">
                {activeCategory.children.map((sub: TCategory) => (
                  <li key={sub.id}>
                    <Link
                      to="/c/$category/$subcategory"
                      params={{
                        category: String(activeCategory.id),
                        subcategory: String(sub.id),
                      }}
                      className="
                        block rounded-md px-3 py-2 text-sm
                        text-muted-foreground
                        hover:bg-muted hover:text-foreground
                        transition-colors
                      "
                    >
                      {sub.name}
                    </Link>
                  </li>
                ))}
              </ul>
            </>
          )}
        </div>
      </div>
    </div>
  );
};
