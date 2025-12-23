import { $api } from "@/lib/axios";

import type { TResponse } from "@/schema/response.schema";

import type {
  TPaginatedProduct,
  TProduct,
  TSimilarProduct,
} from "@/schema/product.schema";
import { useMutation, useSuspenseQuery } from "@tanstack/react-query";
import { queryClient } from "./client";

export const $product = {
  GetAll: () =>
    useSuspenseQuery({
      queryFn: async () =>
        (await $api.get<TResponse<TPaginatedProduct>>("/product/public")).data,
      queryKey: ["products"],
    }),

  GetById: (id: string) =>
    useSuspenseQuery({
      queryFn: async () =>
        (await $api.get<TResponse<TProduct>>(`/product/public/${id}`)).data
          .data,
      queryKey: ["product", id],
    }),
  GetSimilarProductById: (id: string) =>
    useSuspenseQuery({
      queryFn: async () =>
        (
          await $api.get<TResponse<TSimilarProduct>>(
            `/recommendations/product/${id}`
          )
        ).data.data,
      queryKey: ["similar", "product", id],
    }),
  GetProductByUser: (userId: number) =>
    useSuspenseQuery({
      queryFn: async () =>
        (
          await $api.get<TResponse<TPaginatedProduct>>(
            `/product/users/${userId}`
          )
        ).data.data,
      queryKey: ["product-user", userId],
    }),
  GetMyProducts: () =>
    useSuspenseQuery({
      queryFn: async () =>
        (await $api.get<TResponse<TPaginatedProduct>>(`/product/my-products}`))
          .data.data,
      queryKey: ["my-product"],
    }),

  Create: () =>
    useMutation({
      mutationFn: async (data: TPaginatedProduct) =>
        (await $api.put<TResponse<TPaginatedProduct>>("/product", data)).data,
      onSuccess: () => {
        queryClient.invalidateQueries({ queryKey: ["product"] });
      },
    }),

  Update: () =>
    useMutation({
      mutationFn: async (data: TPaginatedProduct) =>
        (await $api.put<TResponse<TPaginatedProduct>>("/product", data)).data,
      onSuccess: () => {
        queryClient.invalidateQueries({ queryKey: ["product"] });
      },
    }),
};
