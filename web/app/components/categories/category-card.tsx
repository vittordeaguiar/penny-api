import { useState } from "react";
import { Pencil, Trash2 } from "lucide-react";
import { Card, CardContent } from "@/components/ui/card";
import { Button } from "@/components/ui/button";
import {
  AlertDialog,
  AlertDialogAction,
  AlertDialogCancel,
  AlertDialogContent,
  AlertDialogDescription,
  AlertDialogFooter,
  AlertDialogHeader,
  AlertDialogTitle,
} from "@/components/ui/alert-dialog";
import { useDeleteCategory } from "@/lib/hooks/use-categories";
import type { CategoryResponseDTO } from "@/types/api.types";

interface CategoryCardProps {
  category: CategoryResponseDTO;
  onEdit: (category: CategoryResponseDTO) => void;
}

export function CategoryCard({ category, onEdit }: CategoryCardProps) {
  const [showDeleteDialog, setShowDeleteDialog] = useState(false);
  const deleteMutation = useDeleteCategory();

  async function handleDelete() {
    await deleteMutation.mutateAsync(category.id);
    setShowDeleteDialog(false);
  }

  return (
    <>
      <Card className="hover:shadow-md transition-shadow">
        <CardContent className="p-6">
          <div className="flex items-center justify-between mb-4">
            <div
              className="w-12 h-12 rounded-lg flex items-center justify-center text-2xl"
              style={{ backgroundColor: category.color + "20" }}
            >
              <span>{category.icon}</span>
            </div>
            <div className="flex gap-2">
              <Button
                variant="ghost"
                size="icon"
                className="cursor-pointer"
                onClick={() => onEdit(category)}
              >
                <Pencil className="size-4" />
              </Button>
              <Button
                variant="ghost"
                size="icon"
                className="cursor-pointer"
                onClick={() => setShowDeleteDialog(true)}
              >
                <Trash2 className="size-4 text-red-600" />
              </Button>
            </div>
          </div>
          <h3 className="font-semibold text-lg">{category.name}</h3>
          <div className="flex items-center gap-2 mt-2">
            <div className="w-4 h-4 rounded" style={{ backgroundColor: category.color }} />
            <p className="text-sm text-slate-600">{category.color}</p>
          </div>
        </CardContent>
      </Card>

      <AlertDialog open={showDeleteDialog} onOpenChange={setShowDeleteDialog}>
        <AlertDialogContent>
          <AlertDialogHeader>
            <AlertDialogTitle>Confirmar exclusão</AlertDialogTitle>
            <AlertDialogDescription>
              Tem certeza que deseja excluir a categoria "{category.name}"? Esta ação não pode ser
              desfeita.
            </AlertDialogDescription>
          </AlertDialogHeader>
          <AlertDialogFooter>
            <AlertDialogCancel className="cursor-pointer">Cancelar</AlertDialogCancel>
            <AlertDialogAction className="cursor-pointer" onClick={handleDelete}>
              Excluir
            </AlertDialogAction>
          </AlertDialogFooter>
        </AlertDialogContent>
      </AlertDialog>
    </>
  );
}
