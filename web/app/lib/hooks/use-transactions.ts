import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import { transactionsService } from '@/lib/api/transactions.service';
import { QUERY_KEYS } from '@/constants/api.constants';
import type { CreateTransactionDTO, UpdateTransactionDTO, TransactionFilters } from '@/types/api.types';
import { toast } from 'sonner';

export function useTransactionSummary(startDate?: string, endDate?: string) {
  return useQuery({
    queryKey: [QUERY_KEYS.TRANSACTION_SUMMARY, startDate, endDate],
    queryFn: () => transactionsService.getSummary(startDate, endDate),
  });
}

export function useTransactions(filters?: TransactionFilters) {
  return useQuery({
    queryKey: [QUERY_KEYS.TRANSACTIONS, filters],
    queryFn: () => transactionsService.getAll(filters),
  });
}

export function useTransaction(id: string) {
  return useQuery({
    queryKey: [QUERY_KEYS.TRANSACTIONS, id],
    queryFn: () => transactionsService.getById(id),
    enabled: !!id,
  });
}

export function useCreateTransaction() {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: (data: CreateTransactionDTO) => transactionsService.create(data),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: [QUERY_KEYS.TRANSACTIONS] });
      queryClient.invalidateQueries({ queryKey: [QUERY_KEYS.TRANSACTION_SUMMARY] });
      toast.success('Transação criada com sucesso!');
    },
    onError: (error: any) => {
      toast.error(error.response?.data?.message || 'Erro ao criar transação');
    },
  });
}

export function useUpdateTransaction() {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: ({ id, data }: { id: string; data: UpdateTransactionDTO }) =>
      transactionsService.update(id, data),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: [QUERY_KEYS.TRANSACTIONS] });
      queryClient.invalidateQueries({ queryKey: [QUERY_KEYS.TRANSACTION_SUMMARY] });
      toast.success('Transação atualizada com sucesso!');
    },
    onError: (error: any) => {
      toast.error(error.response?.data?.message || 'Erro ao atualizar transação');
    },
  });
}

export function useDeleteTransaction() {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: (id: string) => transactionsService.delete(id),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: [QUERY_KEYS.TRANSACTIONS] });
      queryClient.invalidateQueries({ queryKey: [QUERY_KEYS.TRANSACTION_SUMMARY] });
      toast.success('Transação excluída com sucesso!');
    },
    onError: (error: any) => {
      toast.error(error.response?.data?.message || 'Erro ao excluir transação');
    },
  });
}
