import { apiClient } from './client';
import { API_ENDPOINTS } from '@/constants/api.constants';
import type {
  TransactionResponseDTO,
  CreateTransactionDTO,
  UpdateTransactionDTO,
  TransactionSummaryDTO,
  PageResponse,
  TransactionFilters,
} from '@/types/api.types';

export const transactionsService = {
  async getAll(filters?: TransactionFilters): Promise<PageResponse<TransactionResponseDTO>> {
    const params = new URLSearchParams();

    if (filters?.page !== undefined) {
      params.append('page', filters.page.toString());
    }
    if (filters?.size !== undefined) {
      params.append('size', filters.size.toString());
    }
    if (filters?.startDate) {
      params.append('startDate', filters.startDate);
    }
    if (filters?.endDate) {
      params.append('endDate', filters.endDate);
    }

    const response = await apiClient.get<PageResponse<TransactionResponseDTO>>(
      `${API_ENDPOINTS.TRANSACTIONS.BASE}?${params.toString()}`
    );
    return response.data;
  },

  async getSummary(startDate?: string, endDate?: string): Promise<TransactionSummaryDTO> {
    const params = new URLSearchParams();
    if (startDate) params.append('startDate', startDate);
    if (endDate) params.append('endDate', endDate);

    const response = await apiClient.get<TransactionSummaryDTO>(
      `${API_ENDPOINTS.TRANSACTIONS.SUMMARY}?${params.toString()}`
    );
    return response.data;
  },

  async getById(id: string): Promise<TransactionResponseDTO> {
    const response = await apiClient.get<TransactionResponseDTO>(
      API_ENDPOINTS.TRANSACTIONS.BY_ID(id)
    );
    return response.data;
  },

  async create(data: CreateTransactionDTO): Promise<TransactionResponseDTO> {
    const response = await apiClient.post<TransactionResponseDTO>(
      API_ENDPOINTS.TRANSACTIONS.BASE,
      data
    );
    return response.data;
  },

  async update(id: string, data: UpdateTransactionDTO): Promise<TransactionResponseDTO> {
    const response = await apiClient.put<TransactionResponseDTO>(
      API_ENDPOINTS.TRANSACTIONS.BY_ID(id),
      data
    );
    return response.data;
  },

  async delete(id: string): Promise<void> {
    await apiClient.delete(API_ENDPOINTS.TRANSACTIONS.BY_ID(id));
  },
};
