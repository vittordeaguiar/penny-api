import { useState } from "react";
import {
  Dialog,
  DialogContent,
  DialogDescription,
  DialogHeader,
  DialogTitle,
} from "@/components/ui/dialog";
import { CategoryForm } from "./category-form";
import { useCreateCategory, useUpdateCategory } from "@/lib/hooks/use-categories";
import type { CategoryResponseDTO } from "@/types/api.types";

interface CategoryDialogProps {
  open: boolean;
  onOpenChange: (open: boolean) => void;
  category?: CategoryResponseDTO;
}

export function CategoryDialog({ open, onOpenChange, category }: CategoryDialogProps) {
  const createMutation = useCreateCategory();
  const updateMutation = useUpdateCategory();

  const isLoading = createMutation.isPending || updateMutation.isPending;

  async function handleSubmit(values: any) {
    if (category) {
      await updateMutation.mutateAsync({ id: category.id, data: values });
    } else {
      await createMutation.mutateAsync(values);
    }
    onOpenChange(false);
  }

  return (
    <Dialog open={open} onOpenChange={onOpenChange}>
      <DialogContent>
        <DialogHeader>
          <DialogTitle>{category ? "Editar" : "Nova"} Categoria</DialogTitle>
          <DialogDescription>
            {category
              ? "Atualize as informações da categoria"
              : "Crie uma nova categoria para organizar suas transações"}
          </DialogDescription>
        </DialogHeader>
        <CategoryForm category={category} onSubmit={handleSubmit} isLoading={isLoading} />
      </DialogContent>
    </Dialog>
  );
}
