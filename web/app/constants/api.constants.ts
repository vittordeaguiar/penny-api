export const API_BASE_URL = import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080';

export const API_ENDPOINTS = {
  AUTH: {
    LOGIN: '/api/auth/login',
    REGISTER: '/api/auth/register',
  },
  TRANSACTIONS: {
    BASE: '/api/transactions',
    SUMMARY: '/api/transactions/summary',
    BY_ID: (id: string) => `/api/transactions/${id}`,
  },
  CATEGORIES: {
    BASE: '/api/categories',
    BY_ID: (id: string) => `/api/categories/${id}`,
  },
} as const;

export const STORAGE_KEYS = {
  AUTH_TOKEN: 'penny_auth_token',
  USER_DATA: 'penny_user_data',
} as const;

export const QUERY_KEYS = {
  TRANSACTIONS: 'transactions',
  TRANSACTION_SUMMARY: 'transaction-summary',
  CATEGORIES: 'categories',
  CATEGORY: 'category',
} as const;
