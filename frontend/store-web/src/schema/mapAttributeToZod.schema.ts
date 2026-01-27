import { z } from "zod";

export function mapAttributeToZod(type: string) {
  switch (type) {
    case "NUMBER":
      return z.number();
    case "STRING":
      return z.string().min(1);
    case "BOOLEAN":
      return z.boolean();
    default:
      return z.any();
  }
}
