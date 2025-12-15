import { apiClient } from './client';
import { API_ENDPOINTS } from '@/constants/api.constants';
import type {
  CategoryResponseDTO,
  CreateCategoryDTO,
  UpdateCategoryDTO,
} from '@/types/api.types';

export const categoriesService = {
  async getAll(): Promise<CategoryResponseDTO[]> {
    const response = await apiClient.get<CategoryResponseDTO[]>(
      API_ENDPOINTS.CATEGORIES.BASE
    );
    return response.data;
  },

  async getById(id: string): Promise<CategoryResponseDTO> {
    const response = await apiClient.get<CategoryResponseDTO>(
      API_ENDPOINTS.CATEGORIES.BY_ID(id)
    );
    return response.data;
  },

  async create(data: CreateCategoryDTO): Promise<CategoryResponseDTO> {
    const response = await apiClient.post<CategoryResponseDTO>(
      API_ENDPOINTS.CATEGORIES.BASE,
      data
    );
    return response.data;
  },

  async update(id: string, data: UpdateCategoryDTO): Promise<CategoryResponseDTO> {
    const response = await apiClient.put<CategoryResponseDTO>(
      API_ENDPOINTS.CATEGORIES.BY_ID(id),
      data
    );
    return response.data;
  },

  async delete(id: string): Promise<void> {
    await apiClient.delete(API_ENDPOINTS.CATEGORIES.BY_ID(id));
  },
};
