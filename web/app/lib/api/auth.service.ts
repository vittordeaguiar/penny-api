import { apiClient } from './client';
import { API_ENDPOINTS, STORAGE_KEYS } from '@/constants/api.constants';
import type { LoginDTO, RegisterDTO, LoginResponseDTO, UserData } from '@/types/api.types';

// Helper para verificar se está no ambiente do navegador
const isBrowser = typeof window !== 'undefined' && typeof localStorage !== 'undefined';

export const authService = {
  async login(credentials: LoginDTO): Promise<LoginResponseDTO> {
    const response = await apiClient.post<LoginResponseDTO>(
      API_ENDPOINTS.AUTH.LOGIN,
      credentials
    );

    const { token, email, name } = response.data;

    if (isBrowser) {
      localStorage.setItem(STORAGE_KEYS.AUTH_TOKEN, token);
      localStorage.setItem(STORAGE_KEYS.USER_DATA, JSON.stringify({ email, name }));
    }

    return response.data;
  },

  async register(data: RegisterDTO): Promise<LoginResponseDTO> {
    const response = await apiClient.post<LoginResponseDTO>(
      API_ENDPOINTS.AUTH.REGISTER,
      data
    );

    const { token, email, name } = response.data;

    if (isBrowser) {
      localStorage.setItem(STORAGE_KEYS.AUTH_TOKEN, token);
      localStorage.setItem(STORAGE_KEYS.USER_DATA, JSON.stringify({ email, name }));
    }

    return response.data;
  },

  logout(): void {
    if (isBrowser) {
      localStorage.removeItem(STORAGE_KEYS.AUTH_TOKEN);
      localStorage.removeItem(STORAGE_KEYS.USER_DATA);
    }
  },

  getToken(): string | null {
    if (!isBrowser) return null;
    return localStorage.getItem(STORAGE_KEYS.AUTH_TOKEN);
  },

  getUserData(): UserData | null {
    if (!isBrowser) return null;
    const data = localStorage.getItem(STORAGE_KEYS.USER_DATA);
    return data ? JSON.parse(data) : null;
  },

  isAuthenticated(): boolean {
    return !!this.getToken();
  },
};
