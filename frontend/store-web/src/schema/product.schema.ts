import { z } from "zod";

export const ProductResponseSchema = z.object({
  id: z.string(),
  name: z.string().min(1),
  description: z.string().optional().nullable(),
  price: z.number().positive(),
  categoryName: z.string(),
  sellerName: z.string(),
  imageUrls: z.array(z.string().url()).default([]),
});

export const PaginatedProductResponseSchema = z.object({
  content: z.array(ProductResponseSchema),
  totalPages: z.number().int().nonnegative(),
  totalElements: z.number().int().nonnegative(),
  size: z.number().int().positive(),
  number: z.number().int().nonnegative(),
});
export type TProduct = z.infer<typeof ProductResponseSchema>;
export type TPaginatedProduct = z.infer<typeof PaginatedProductResponseSchema>;

export const SimilarProductResponseSchema = z.object({
  productId: z.string(),
  items: z.array(ProductResponseSchema),
});
export type TSimilarProduct = z.infer<typeof SimilarProductResponseSchema>;
