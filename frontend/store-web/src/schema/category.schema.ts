// import { z } from "zod";

// export const CategoryResponseSchema = z.object({
//   id: z.string(),
//   name: z.string().min(1),
//   slug: z.string().min(1),
//   description: z.string().optional().nullable(),
//   parentId: z.string().optional().nullable(),
//   children: z.array(CategoryResponseSchema).default([]),
// });

// export type TCategory = z.infer<typeof CategoryResponseSchema>;

import { z } from "zod";

// export type TCategory = {
//   id: string;
//   name: string;
//   slug: string;
//   description?: string | null;
//   parentId?: string | null;
//   children: TCategory[];
// };

export const TCategory = z.object({
  id: z.string(),
  name: z.string().min(1),
  slug: z.string().optional().nullable(),
  description: z.number().positive(),
  parentId: z.string(),
});

export const CategoryResponseSchema = z.object({
  id: z.string(),
  name: z.string().min(1),
  slug: z.string().min(1),
  description: z.string().optional().nullable(),
  parentId: z.string().optional().nullable(),
  children: z.array(TCategory).default([]),
});

export type TCategoryResponse = z.infer<typeof CategoryResponseSchema>;

export type TCategory = z.infer<typeof TCategory>;
