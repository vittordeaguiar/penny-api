import { redirect } from 'react-router';
import { authService } from '@/lib/api/auth.service';
import type { Route } from './+types/home';

export function meta({}: Route.MetaArgs) {
  return [
    { title: 'Penny - Gestão Financeira' },
    { name: 'description', content: 'Gestão Financeira Inteligente' },
  ];
}

export function clientLoader() {
  if (authService.isAuthenticated()) {
    throw redirect('/dashboard');
  }
  throw redirect('/login');
}

export default function Home() {
  return null;
}
