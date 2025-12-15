import {
  Dialog,
  DialogContent,
  DialogDescription,
  DialogHeader,
  DialogTitle,
} from "@/components/ui/dialog";
import { TransactionForm } from "./transaction-form";
import { useCreateTransaction, useUpdateTransaction } from "@/lib/hooks/use-transactions";
import type { TransactionResponseDTO } from "@/types/api.types";

interface TransactionDialogProps {
  open: boolean;
  onOpenChange: (open: boolean) => void;
  transaction?: TransactionResponseDTO;
}

export function TransactionDialog({ open, onOpenChange, transaction }: TransactionDialogProps) {
  const createMutation = useCreateTransaction();
  const updateMutation = useUpdateTransaction();

  const isLoading = createMutation.isPending || updateMutation.isPending;

  async function handleSubmit(values: any) {
    if (transaction) {
      await updateMutation.mutateAsync({ id: transaction.id, data: values });
    } else {
      await createMutation.mutateAsync(values);
    }
    onOpenChange(false);
  }

  return (
    <Dialog open={open} onOpenChange={onOpenChange}>
      <DialogContent>
        <DialogHeader>
          <DialogTitle>{transaction ? "Editar" : "Nova"} Transação</DialogTitle>
          <DialogDescription>
            {transaction
              ? "Atualize as informações da transação"
              : "Crie uma nova transação financeira"}
          </DialogDescription>
        </DialogHeader>
        <TransactionForm transaction={transaction} onSubmit={handleSubmit} isLoading={isLoading} />
      </DialogContent>
    </Dialog>
  );
}
