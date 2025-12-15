import { useState } from "react";
import { Plus, ChevronLeft, ChevronRight } from "lucide-react";
import { Button } from "@/components/ui/button";
import { PageHeader } from "@/components/layout/page-header";
import { TransactionTable } from "@/components/transactions/transaction-table";
import { TransactionDialog } from "@/components/transactions/transaction-dialog";
import { useTransactions } from "@/lib/hooks/use-transactions";
import type { TransactionResponseDTO } from "@/types/api.types";

export default function TransactionsPage() {
  const [page, setPage] = useState(0);
  const [dialogOpen, setDialogOpen] = useState(false);
  const [selectedTransaction, setSelectedTransaction] = useState<
    TransactionResponseDTO | undefined
  >();

  const { data: transactionsPage, isLoading } = useTransactions({ page, size: 10 });

  function handleEdit(transaction: TransactionResponseDTO) {
    setSelectedTransaction(transaction);
    setDialogOpen(true);
  }

  function handleCreate() {
    setSelectedTransaction(undefined);
    setDialogOpen(true);
  }

  return (
    <div>
      <PageHeader
        title="Transações"
        description="Gerencie suas transações financeiras"
        action={
          <Button onClick={handleCreate} className="cursor-pointer">
            <Plus className="mr-2 h-4 w-4" />
            Nova Transação
          </Button>
        }
      />

      <TransactionTable
        transactions={transactionsPage?.content || []}
        isLoading={isLoading}
        onEdit={handleEdit}
      />

      {transactionsPage && transactionsPage.totalPages > 1 && (
        <div className="flex items-center justify-between mt-4">
          <p className="text-sm text-slate-600">
            Página {page + 1} de {transactionsPage.totalPages} ({transactionsPage.totalElements}{" "}
            transações)
          </p>
          <div className="flex gap-2">
            <Button
              variant="outline"
              size="sm"
              onClick={() => setPage((p) => Math.max(0, p - 1))}
              disabled={transactionsPage.first}
            >
              <ChevronLeft className="h-4 w-4 mr-2" />
              Anterior
            </Button>
            <Button
              variant="outline"
              size="sm"
              onClick={() => setPage((p) => p + 1)}
              disabled={transactionsPage.last}
            >
              Próxima
              <ChevronRight className="h-4 w-4 ml-2" />
            </Button>
          </div>
        </div>
      )}

      <TransactionDialog
        open={dialogOpen}
        onOpenChange={setDialogOpen}
        transaction={selectedTransaction}
      />
    </div>
  );
}
