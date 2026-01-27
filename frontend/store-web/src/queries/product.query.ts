// queries/product.query.ts
import { $api } from "@/lib/axios";
import type {
  TPaginatedProduct,
  TProduct,
  TSimilarProduct,
} from "@/schema/product.schema";
import type { TResponse } from "@/schema/response.schema";
import {
  useInfiniteQuery,
  useMutation,
  useQuery,
  useSuspenseQuery,
} from "@tanstack/react-query";
import { queryClient } from "./client";

type ProductSearchParams = {
  keyword?: string;
  size?: number;
};

type FilterPayload = {
  categoryId?: number;
  subcategory?: number;
};

function useInfiniteProductSearch({ keyword, size = 12 }: ProductSearchParams) {
  return useInfiniteQuery<
    TPaginatedProduct, // queryFn return type
    Error, // error type
    TPaginatedProduct, // data type
    [string, string | undefined, number], // queryKey type
    number // pageParam type
  >({
    queryKey: ["products", "search", keyword, size],

    enabled: !!keyword,

    initialPageParam: 0,

    queryFn: async ({ pageParam = 0 }) => {
      const res = await $api.get<TResponse<TPaginatedProduct>>(
        "/products/search",
        {
          params: {
            keyword,
            page: pageParam,
            size,
          },
        }
      );

      return res.data.data;
    },

    getNextPageParam: (lastPage, allPages) => {
      return lastPage.hasNext ? allPages.length : undefined;
    },
  });
}

export function useGetFilteredInfiniteProductsCategory(
  filters: FilterPayload,
  pageSize: number = 12
) {
  return useInfiniteQuery<
    TPaginatedProduct,
    Error,
    TPaginatedProduct,
    [string, FilterPayload, number],
    number
  >({
    queryKey: ["products-filter", filters, pageSize],
    initialPageParam: 0,

    queryFn: async ({ pageParam }) => {
      const res = await $api.post<TResponse<TPaginatedProduct>>(
        `/products/filter?page=${pageParam}&size=${pageSize}`,
        filters
      );

      return res.data.data;
    },

    getNextPageParam: (lastPage, allPages) => {
      return lastPage.hasNext ? allPages.length : undefined;
    },
  });
}

export function useGetFilteredInfiniteProductsSubCategory(payload: {
  categoryId: number;
  subcategory: number;
  pageSize?: number;
}) {
  return useInfiniteQuery<
    TPaginatedProduct,
    Error,
    TPaginatedProduct,
    [string, string, typeof payload],
    number
  >({
    queryKey: ["products", "subcategory", payload],
    initialPageParam: 0,

    queryFn: async ({ pageParam }) => {
      const res = await $api.post<TResponse<TPaginatedProduct>>(
        `/product/public/filter?page=${pageParam}&size=${
          payload.pageSize ?? 12
        }`,
        {
          categoryId: payload.categoryId,
          subcategory: payload.subcategory,
        }
      );

      return res.data.data;
    },

    getNextPageParam: (lastPage, allPages) => {
      return lastPage.hasNext ? allPages.length : undefined;
    },
  });
}

function useGetInfiniteProducts(pageSize: number = 12) {
  return useInfiniteQuery<
    TPaginatedProduct,
    Error,
    TPaginatedProduct,
    [string, number],
    number
  >({
    queryKey: ["products", pageSize],
    initialPageParam: 0,

    queryFn: async ({ pageParam }) => {
      const res = await $api.get<TResponse<TPaginatedProduct>>(
        `/product/public?page=${pageParam}&size=${pageSize}`
      );
      return res.data.data;
    },

    // ✅ FIX: derive next page from total loaded pages
    getNextPageParam: (lastPage, allPages) => {
      return lastPage.hasNext ? allPages.length : undefined;
    },
  });
}

function useProductSearchSuggest(params: { q?: string }) {
  return useQuery({
    queryKey: ["products", "search", params],
    queryFn: async () =>
      (
        await $api.get("products/suggest", {
          params,
        })
      ).data.data,
    enabled: !!params.q,
  });
}

function useGetProductById(id: string) {
  return useSuspenseQuery({
    queryKey: ["product", id],
    queryFn: async () =>
      (await $api.get<TResponse<TProduct>>(`/product/public/${id}`)).data.data,
  });
}

function useGetSimilarProductById(id: string) {
  return useSuspenseQuery({
    queryKey: ["similar", "product", id],
    queryFn: async () =>
      (
        await $api.get<TResponse<TSimilarProduct>>(
          `/recommendations/product/${id}`
        )
      ).data.data,
  });
}

function useGetProductByUser(userId: number) {
  return useSuspenseQuery({
    queryKey: ["product-user", userId],
    queryFn: async () =>
      (await $api.get<TResponse<TPaginatedProduct>>(`/product/users/${userId}`))
        .data.data,
  });
}

function useGetMyProducts() {
  return useSuspenseQuery({
    queryKey: ["my-product"],
    queryFn: async () =>
      (await $api.get<TResponse<TPaginatedProduct>>(`/product/my-products`))
        .data.data,
  });
}

function useCreateProduct() {
  return useMutation({
    mutationFn: async (data: TPaginatedProduct) =>
      (await $api.put<TResponse<TPaginatedProduct>>("/product", data)).data,
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ["product"] });
    },
  });
}

function useUpdateProduct() {
  return useMutation({
    mutationFn: async (data: TPaginatedProduct) =>
      (await $api.put<TResponse<TPaginatedProduct>>("/product", data)).data,
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ["product"] });
    },
  });
}

//  --- Category of Product Queries ---

export const $product = {
  GetAll: useGetInfiniteProducts,
  GetById: useGetProductById,
  GetByCategory: useGetFilteredInfiniteProductsCategory,
  GetBySubCategory: useGetFilteredInfiniteProductsSubCategory,
  GetSimilarById: useGetSimilarProductById,
  GetByUser: useGetProductByUser,
  GetMyProducts: useGetMyProducts,
  Search: useInfiniteProductSearch,
  SearchSuggest: useProductSearchSuggest,
  Create: useCreateProduct,
  Update: useUpdateProduct,
};
