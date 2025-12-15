export interface LoginDTO {
  email: string;
  password: string;
}

export interface RegisterDTO {
  name: string;
  email: string;
  password: string;
}

export interface LoginResponseDTO {
  token: string;
  type: string;
  email: string;
  name: string;
}

export interface UserData {
  email: string;
  name: string;
}

export enum TransactionType {
  INCOME = 'INCOME',
  EXPENSE = 'EXPENSE',
}

export interface CategoryResponseDTO {
  id: string;
  name: string;
  icon: string;
  color: string;
  createdAt: string;
  updatedAt: string;
}

export interface CreateCategoryDTO {
  name: string;
  icon: string;
  color: string;
}

export interface UpdateCategoryDTO {
  name: string;
  icon: string;
  color: string;
}

export interface TransactionResponseDTO {
  id: string;
  description: string;
  amount: number;
  type: TransactionType;
  date: string;
  category: CategoryResponseDTO;
  createdAt: string;
  updatedAt: string;
}

export interface CreateTransactionDTO {
  description: string;
  amount: number;
  type: TransactionType;
  date: string;
  categoryId: string;
}

export interface UpdateTransactionDTO {
  description: string;
  amount: number;
  type: TransactionType;
  date: string;
  categoryId: string;
}

export interface TransactionSummaryDTO {
  totalIncome: number;
  totalExpense: number;
  balance: number;
}

export interface PageResponse<T> {
  content: T[];
  page: number;
  size: number;
  totalElements: number;
  totalPages: number;
  first: boolean;
  last: boolean;
}

export interface ErrorResponseDTO {
  message: string;
  status: number;
  timestamp: string;
  errors?: Record<string, string>;
}

export interface TransactionFilters {
  page?: number;
  size?: number;
  startDate?: string;
  endDate?: string;
}
