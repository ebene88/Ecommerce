export type TErrorResponse = {
  code: string;
  message: string;
};

export type TResponse<T> = {
  status: string;
  message: string;
  data: T;
  error: TErrorResponse;
};

export type TPagedResponse<T> = {
  content: T[];
  totalPages: number;
  totalElements: number;
  size: number;
  number: number; // current page
};
