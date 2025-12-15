import { PageHeader } from '@/components/layout/page-header';
import { SummaryCard } from '@/components/dashboard/summary-card';
import { RecentTransactions } from '@/components/dashboard/recent-transactions';
import { useTransactionSummary, useTransactions } from '@/lib/hooks/use-transactions';
import { Skeleton } from '@/components/ui/skeleton';

export default function DashboardPage() {
  const { data: summary, isLoading: isSummaryLoading } = useTransactionSummary();
  const { data: transactionsPage, isLoading: isTransactionsLoading } = useTransactions({
    page: 0,
    size: 5,
  });

  return (
    <div>
      <PageHeader
        title="Dashboard"
        description="Visão geral das suas finanças"
      />

      <div className="grid gap-4 md:grid-cols-3 mb-8">
        {isSummaryLoading ? (
          <>
            <Skeleton className="h-32" />
            <Skeleton className="h-32" />
            <Skeleton className="h-32" />
          </>
        ) : (
          <>
            <SummaryCard
              title="Receitas"
              value={summary?.totalIncome || 0}
              variant="income"
            />
            <SummaryCard
              title="Despesas"
              value={summary?.totalExpense || 0}
              variant="expense"
            />
            <SummaryCard
              title="Saldo"
              value={summary?.balance || 0}
              variant="balance"
            />
          </>
        )}
      </div>

      <RecentTransactions
        transactions={transactionsPage?.content || []}
        isLoading={isTransactionsLoading}
      />
    </div>
  );
}
