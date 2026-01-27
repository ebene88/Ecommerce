import { $api } from "@/lib/axios";

import type { TResponse } from "@/schema/response.schema";

import type { TCategoryResponse } from "@/schema/category.schema";
import type { TPaginatedProduct } from "@/schema/product.schema";
import { useMutation, useSuspenseQuery } from "@tanstack/react-query";
import { queryClient } from "./client";

function useCategoryAttributes(categoryId?: string | number) {
  return useQuery({
    queryKey: ["category-attributes", categoryId],
    queryFn: async () =>
      (await api.get(`/categories/${categoryId}/attributes`)).data.data,
    enabled: !!categoryId,
  });
}

export const $category = {
  GetAll: () =>
    useSuspenseQuery({
      queryFn: async () =>
        (await $api.get<TResponse<TCategoryResponse[]>>("/categories")).data,
      queryKey: ["category"],
    }),

  GetById: (id: string) =>
    useSuspenseQuery({
      queryFn: async () =>
        (await $api.get<TResponse<TCategoryResponse>>(`/categories/${id}`)).data
          .data,
      queryKey: ["category", id],
    }),

  Create: () =>
    useMutation({
      mutationFn: async (data: TPaginatedProduct) =>
        (await $api.put<TResponse<TCategoryResponse>>("/categories", data))
          .data,
      onSuccess: () => {
        queryClient.invalidateQueries({ queryKey: ["category"] });
      },
    }),

  Update: () =>
    useMutation({
      mutationFn: async (data: TCategoryResponse) =>
        (await $api.put<TResponse<TCategoryResponse>>("/categories", data))
          .data,
      onSuccess: () => {
        queryClient.invalidateQueries({ queryKey: ["category"] });
      },
    }),

  GetAttributesByCategoryId: (categoryId: number) =>
    useSuspenseQuery({
      queryKey: ["category-attributes", categoryId],
      queryFn: async () =>
        (await $api.get(`/categories/${categoryId}/attributes`)).data.data,
    }),
};
