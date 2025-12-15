import { useNavigate, Link } from 'react-router';
import { LoginForm } from '@/components/auth/login-form';

export default function LoginPage() {
  const navigate = useNavigate();

  return (
    <div className="space-y-4">
      <LoginForm onSuccess={() => navigate('/dashboard')} />
      <p className="text-center text-sm text-slate-600">
        Não tem uma conta?{' '}
        <Link to="/register" className="text-slate-900 font-medium hover:underline">
          Criar conta
        </Link>
      </p>
    </div>
  );
}
