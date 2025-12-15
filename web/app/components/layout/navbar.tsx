import { Link } from "react-router";
import { LayoutDashboard, ArrowLeftRight, Tag, LogOut, UserCircle } from "lucide-react";
import {
  DropdownMenu,
  DropdownMenuContent,
  DropdownMenuItem,
  DropdownMenuLabel,
  DropdownMenuSeparator,
  DropdownMenuTrigger,
} from "@/components/ui/dropdown-menu";
import { Button } from "@/components/ui/button";
import { useAuth } from "@/lib/contexts/auth-context";

export function Navbar() {
  const { user, logout } = useAuth();

  return (
    <nav className="border-b bg-white">
      <div className="container mx-auto px-4 h-16 flex items-center justify-between">
        <div className="flex items-center gap-8">
          <Link to="/dashboard" className="text-xl font-bold text-slate-900">
            Penny
          </Link>

          <div className="hidden md:flex items-center gap-4">
            <Link to="/dashboard">
              <Button variant="ghost" size="sm" className="cursor-pointer">
                <LayoutDashboard className="mr-2 size-4" />
                Dashboard
              </Button>
            </Link>
            <Link to="/transactions">
              <Button variant="ghost" size="sm" className="cursor-pointer">
                <ArrowLeftRight className="mr-2 size-4" />
                Transações
              </Button>
            </Link>
            <Link to="/categories">
              <Button variant="ghost" size="sm" className="cursor-pointer">
                <Tag className="mr-2 size-4" />
                Categorias
              </Button>
            </Link>
          </div>
        </div>

        <DropdownMenu>
          <DropdownMenuTrigger asChild>
            <Button variant="ghost" size="sm" className="cursor-pointer">
              <UserCircle className="mr-2 size-4" />
              {user?.name}
            </Button>
          </DropdownMenuTrigger>
          <DropdownMenuContent align="end">
            <DropdownMenuLabel>Minha Conta</DropdownMenuLabel>
            <DropdownMenuLabel className="font-normal text-sm text-slate-600">
              {user?.email}
            </DropdownMenuLabel>
            <DropdownMenuSeparator />
            <DropdownMenuItem className="cursor-pointer" onClick={logout}>
              <LogOut className="mr-2 size-4" />
              Sair
            </DropdownMenuItem>
          </DropdownMenuContent>
        </DropdownMenu>
      </div>
    </nav>
  );
}
