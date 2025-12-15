import { createContext, useContext, useState, useEffect, type ReactNode } from 'react';
import { authService } from '@/lib/api/auth.service';
import type { LoginDTO, RegisterDTO, UserData } from '@/types/api.types';

interface AuthContextType {
  user: UserData | null;
  isAuthenticated: boolean;
  isLoading: boolean;
  login: (credentials: LoginDTO) => Promise<void>;
  register: (data: RegisterDTO) => Promise<void>;
  logout: () => void;
}

const AuthContext = createContext<AuthContextType | undefined>(undefined);

export function AuthProvider({ children }: { children: ReactNode }) {
  const [user, setUser] = useState<UserData | null>(null);
  const [isLoading, setIsLoading] = useState(true);

  useEffect(() => {
    const userData = authService.getUserData();
    setUser(userData);
    setIsLoading(false);
  }, []);

  const login = async (credentials: LoginDTO) => {
    const response = await authService.login(credentials);
    setUser({ email: response.email, name: response.name });
  };

  const register = async (data: RegisterDTO) => {
    const response = await authService.register(data);
    setUser({ email: response.email, name: response.name });
  };

  const logout = () => {
    authService.logout();
    setUser(null);
    window.location.href = '/login';
  };

  const value = {
    user,
    isAuthenticated: !!user,
    isLoading,
    login,
    register,
    logout,
  };

  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>;
}

export function useAuth() {
  const context = useContext(AuthContext);
  if (context === undefined) {
    throw new Error('useAuth must be used within an AuthProvider');
  }
  return context;
}
