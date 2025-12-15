import { useState } from 'react';
import { Plus } from 'lucide-react';
import { Button } from '@/components/ui/button';
import { PageHeader } from '@/components/layout/page-header';
import { CategoryGrid } from '@/components/categories/category-grid';
import { CategoryDialog } from '@/components/categories/category-dialog';
import { useCategories } from '@/lib/hooks/use-categories';
import type { CategoryResponseDTO } from '@/types/api.types';

export default function CategoriesPage() {
  const [dialogOpen, setDialogOpen] = useState(false);
  const [selectedCategory, setSelectedCategory] = useState<CategoryResponseDTO | undefined>();
  const { data: categories = [], isLoading } = useCategories();

  function handleEdit(category: CategoryResponseDTO) {
    setSelectedCategory(category);
    setDialogOpen(true);
  }

  function handleCreate() {
    setSelectedCategory(undefined);
    setDialogOpen(true);
  }

  return (
    <div>
      <PageHeader
        title="Categorias"
        description="Gerencie suas categorias de transações"
        action={
          <Button onClick={handleCreate} className='cursor-pointer'>
            <Plus className="mr-2 size-4" />
            Nova Categoria
          </Button>
        }
      />

      <CategoryGrid categories={categories} isLoading={isLoading} onEdit={handleEdit} />

      <CategoryDialog
        open={dialogOpen}
        onOpenChange={setDialogOpen}
        category={selectedCategory}
      />
    </div>
  );
}
