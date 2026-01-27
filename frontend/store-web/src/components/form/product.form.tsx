"use client";

import { useEffect, useMemo, useState } from "react";
import { useForm, Controller } from "react-hook-form";
import { zodResolver } from "@hookform/resolvers/zod";

import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { Select } from "@/components/ui/select";
import {
  Field,
  FieldDescription,
  FieldGroup,
  FieldLabel,
  FieldLegend,
  FieldSeparator,
  FieldSet,
} from "@/components/ui/field";

import { $category } from "@/queries/category.query";
import {
  BaseProductSchema,
  buildProductSchema,
  type AttributeDef,
  type TProductRequestForm,
} from "@/schema/product.schema";

export function ProductField() {
  const [schema, setSchema] = useState(() =>
    BaseProductSchema.extend({ attributes: {} })
  );

  const form = useForm<TProductRequestForm>({
    resolver: zodResolver(schema),
    defaultValues: {
      name: "",
      price: 0,
      description: "",
      categoryId: 0,
      attributes: {},
    },
  });

  const categoryId = form.watch("categoryId");

  // Fetch all categories
  const { data: categories = [] } = $category.GetAll();

  // Fetch attributes for selected category
  const { data: attributes = [] } =
    $category.GetAttributesByCategoryId(categoryId);

  // Rebuild schema when attributes change
  useEffect(() => {
    if (!attributes.length) {
      setSchema(BaseProductSchema.extend({ attributes: {} }));
      form.setValue("attributes", {}); // reset attributes
      return;
    }

    const dynamicSchema = buildProductSchema(attributes as AttributeDef[]);
    setSchema(dynamicSchema);

    // Reset only the attributes field
    form.setValue("attributes", {});
  }, [attributes, form]);

  const onSubmit = (data: TProductRequestForm) => {
    console.log("FORM DATA:", data);
  };

  return (
    <div className="w-full max-w-md">
      <form onSubmit={form.handleSubmit(onSubmit)}>
        <FieldGroup>
          <FieldSet>
            <FieldLegend>Product Info</FieldLegend>
            <FieldDescription>Enter valid product information</FieldDescription>

            <FieldGroup>
              {/* Product Name */}
              <Field>
                <FieldLabel>Product Name</FieldLabel>
                <Controller
                  control={form.control}
                  name="name"
                  render={({ field, fieldState }) => (
                    <>
                      <Input {...field} placeholder="Samsung Galaxy S23" />
                      {fieldState.error && (
                        <p className="text-sm text-destructive">
                          {fieldState.error.message}
                        </p>
                      )}
                    </>
                  )}
                />
              </Field>

              {/* Price */}
              <Field>
                <FieldLabel>Price</FieldLabel>
                <Controller
                  control={form.control}
                  name="price"
                  render={({ field, fieldState }) => (
                    <>
                      <Input
                        type="number"
                        step="0.01"
                        placeholder="1299"
                        value={field.value ?? 0}
                        onChange={(e) => field.onChange(Number(e.target.value))}
                      />
                      {fieldState.error && (
                        <p className="text-sm text-destructive">
                          {fieldState.error.message}
                        </p>
                      )}
                    </>
                  )}
                />
              </Field>

              {/* Description */}
              <Field>
                <FieldLabel>Description</FieldLabel>
                <Controller
                  control={form.control}
                  name="description"
                  render={({ field, fieldState }) => (
                    <>
                      <Input {...field} placeholder="Flagship smartphone..." />
                      {fieldState.error && (
                        <p className="text-sm text-destructive">
                          {fieldState.error.message}
                        </p>
                      )}
                    </>
                  )}
                />
              </Field>

              {/* Category Selection */}
              <Field>
                <FieldLabel>Category</FieldLabel>
                <Controller
                  control={form.control}
                  name="categoryId"
                  render={({ field, fieldState }) => (
                    <>
                      <Select {...field}>
                        <option value={0}>Select category</option>
                        {categories.map((cat) => (
                          <option key={cat.id} value={cat.id}>
                            {cat.name}
                          </option>
                        ))}
                      </Select>
                      {fieldState.error && (
                        <p className="text-sm text-destructive">
                          {fieldState.error.message}
                        </p>
                      )}
                    </>
                  )}
                />
              </Field>

              {/* Dynamic Attributes */}
              {attributes.map((attr) => (
                <Field key={attr.code}>
                  <FieldLabel>{attr.label}</FieldLabel>

                  <Controller
                    control={form.control}
                    name={`attributes.${attr.code}`}
                    render={({ field }) => {
                      if (attr.type === "NUMBER")
                        return (
                          <Input
                            type="number"
                            value={field.value ?? ""}
                            onChange={(e) =>
                              field.onChange(Number(e.target.value))
                            }
                            placeholder={attr.label}
                          />
                        );
                      if (attr.type === "BOOLEAN")
                        return (
                          <Select {...field}>
                            <option value="">Select</option>
                            <option value="true">Yes</option>
                            <option value="false">No</option>
                          </Select>
                        );
                      return <Input {...field} placeholder={attr.label} />;
                    }}
                  />
                </Field>
              ))}
            </FieldGroup>
          </FieldSet>

          <FieldSeparator />

          <Field orientation="horizontal">
            <Button type="submit">Save Product</Button>
            <Button
              type="button"
              variant="outline"
              onClick={() => form.reset()}
            >
              Cancel
            </Button>
          </Field>
        </FieldGroup>
      </form>
    </div>
  );
}
