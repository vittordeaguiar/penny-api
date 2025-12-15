import { Skeleton } from "@/components/ui/skeleton";
import { CategoryCard } from "./category-card";
import type { CategoryResponseDTO } from "@/types/api.types";

interface CategoryGridProps {
  categories: CategoryResponseDTO[];
  isLoading: boolean;
  onEdit: (category: CategoryResponseDTO) => void;
}

export function CategoryGrid({ categories, isLoading, onEdit }: CategoryGridProps) {
  if (isLoading) {
    return (
      <div className="grid gap-4 md:grid-cols-2 lg:grid-cols-3">
        {[...Array(6)].map((_, i) => (
          <Skeleton key={i} className="h-40" />
        ))}
      </div>
    );
  }

  if (categories.length === 0) {
    return (
      <div className="text-center py-12">
        <p className="text-slate-600">Nenhuma categoria encontrada</p>
        <p className="text-sm text-slate-500 mt-2">Crie sua primeira categoria para começar</p>
      </div>
    );
  }

  return (
    <div className="grid gap-4 md:grid-cols-2 lg:grid-cols-3">
      {categories.map((category) => (
        <CategoryCard key={category.id} category={category} onEdit={onEdit} />
      ))}
    </div>
  );
}
