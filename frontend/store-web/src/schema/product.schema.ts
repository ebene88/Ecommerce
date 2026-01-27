import { z } from "zod";
import { mapAttributeToZod } from "./mapAttributeToZod.schema";

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
  hasNext: z.boolean(),
});
export type TProduct = z.infer<typeof ProductResponseSchema>;
export type TPaginatedProduct = z.infer<typeof PaginatedProductResponseSchema>;

export const SimilarProductResponseSchema = z.object({
  productId: z.string(),
  items: z.array(ProductResponseSchema),
});
export type TSimilarProduct = z.infer<typeof SimilarProductResponseSchema>;

export const BaseProductSchema = z.object({
  name: z.string().min(1, "Name is required"),
  description: z.string().optional().nullable(),
  price: z.number().min(0).optional(),
  categoryId: z.number(),
});
export interface AttributeDef {
  code: string;
  label: string;
  type: "STRING" | "NUMBER" | "BOOLEAN";
  filterable: boolean;
  searchable: boolean;
}

export function buildProductSchema(attributes: AttributeDef[]) {
  const attrSchema: Record<string, z.ZodTypeAny> = {};

  attributes.forEach((attr) => {
    attrSchema[attr.code] = mapAttributeToZod(attr.type);
  });

  return BaseProductSchema.extend({
    attributes: z.object(attrSchema),
  });
}

export type TProductRequestForm = z.infer<typeof BaseProductSchema> & {
  attributes?: Record<string, any>;
};
