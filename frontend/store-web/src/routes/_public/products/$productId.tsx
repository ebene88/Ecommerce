import { SimilarProducts } from "@/components/SimilarProducts";
import { $product } from "@/queries/product.query";
import type { TProduct } from "@/schema/product.schema";
import { createFileRoute } from "@tanstack/react-router";
import { useState } from "react";

// 1️⃣ Create the route
export const Route = createFileRoute("/_public/products/$productId")({
  component: RouteComponent,
});

// 2️⃣ Main component
function RouteComponent() {
  const { productId } = Route.useParams();

  const { data } = $product.GetById(productId); // fetch product by ID
  const product: TProduct | undefined = data;

  const [selectedImage, setSelectedImage] = useState<string | undefined>(
    product?.imageUrls?.[0]
  );

  if (!product) return <div>Loading...</div>;

  return (
    <div className="max-w-7xl mx-auto p-6 grid grid-cols-1 lg:grid-cols-3 gap-8">
      {/* ===================== Left Column: Images + Gallery ===================== */}
      <div className="lg:col-span-2 flex flex-col items-center">
        {/* Main Image */}
        <div className="w-full aspect-[4/5] bg-muted rounded-xl overflow-hidden mb-4">
          <img
            src={selectedImage ?? "/placeholder-image.png"}
            alt={product.name}
            className="w-full h-full object-cover"
          />
        </div>

        {/* Thumbnail Gallery */}
        <div className="flex space-x-2 overflow-x-auto">
          {product.imageUrls.map((url, i) => (
            <button
              key={i}
              className={`w-20 h-20 rounded-lg overflow-hidden border ${
                selectedImage === url ? "border-primary" : "border-border"
              }`}
              onClick={() => setSelectedImage(url)}
            >
              <img
                src={url}
                alt={`Thumbnail ${i + 1}`}
                className="w-full h-full object-cover"
              />
            </button>
          ))}
        </div>

        {/* Product Details */}
        <div className="mt-6 w-full">
          <h1 className="text-2xl font-bold">{product.name}</h1>
          <p className="text-sm text-muted-foreground mt-2 line-clamp-4">
            {product.description}
          </p>
        </div>

        {/* Recommendations */}
        <div className="mt-8 w-full">
          <SimilarProducts productId={productId} />
        </div>
      </div>

      {/* ===================== Right Column: Price & Seller ===================== */}
      <div className="flex flex-col space-y-6">
        <div className="p-6 border rounded-xl bg-card shadow-sm">
          <span className="text-3xl font-bold text-foreground">
            ${product.price.toFixed(2)}
          </span>
        </div>

        <div className="p-4 border rounded-xl flex items-center space-x-4">
          <div className="w-12 h-12 rounded-full overflow-hidden bg-muted">
            <img
              src={"/placeholder-avatar.png"}
              alt={product.sellerName}
              className="w-full h-full object-cover"
            />
          </div>
          <div className="flex flex-col">
            <span className="font-semibold">{product.sellerName}</span>
            <span className="text-sm text-muted-foreground">Seller</span>
          </div>
        </div>
      </div>
    </div>
  );
}
