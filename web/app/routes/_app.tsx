import { Outlet, redirect } from 'react-router';
import { authService } from '@/lib/api/auth.service';
import { Navbar } from '@/components/layout/navbar';
import type { Route } from './+types/_app';

export function clientLoader({ request }: Route.ClientLoaderArgs) {
  if (!authService.isAuthenticated()) {
    throw redirect('/login');
  }
  return null;
}

export default function AppLayout() {
  return (
    <div className="min-h-screen bg-slate-50">
      <Navbar />
      <main className="container mx-auto px-4 py-8">
        <Outlet />
      </main>
    </div>
  );
}
