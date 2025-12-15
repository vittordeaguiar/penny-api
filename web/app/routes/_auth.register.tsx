import { useNavigate, Link } from 'react-router';
import { RegisterForm } from '@/components/auth/register-form';

export default function RegisterPage() {
  const navigate = useNavigate();

  return (
    <div className="space-y-4">
      <RegisterForm onSuccess={() => navigate('/dashboard')} />
      <p className="text-center text-sm text-slate-600">
        Já tem uma conta?{' '}
        <Link to="/login" className="text-slate-900 font-medium hover:underline">
          Entrar
        </Link>
      </p>
    </div>
  );
}
