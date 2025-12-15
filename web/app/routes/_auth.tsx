import { Outlet, redirect } from 'react-router';
import { authService } from '@/lib/api/auth.service';
import type { Route } from './+types/_auth';

export function clientLoader({ request }: Route.ClientLoaderArgs) {
  if (authService.isAuthenticated()) {
    throw redirect('/dashboard');
  }
  return null;
}

export default function AuthLayout() {
  return (
    <div className="min-h-screen flex items-center justify-center bg-slate-50">
      <div className="w-full max-w-md p-6">
        <div className="mb-8 text-center">
          <h1 className="text-3xl font-bold text-slate-900">Penny</h1>
          <p className="text-slate-600 mt-2">Gestão Financeira Inteligente</p>
        </div>
        <Outlet />
      </div>
    </div>
  );
}
