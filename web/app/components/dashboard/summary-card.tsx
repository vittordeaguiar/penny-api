import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card";
import { formatCurrency } from "@/lib/utils/format";
import { TrendingUp, TrendingDown, Wallet } from "lucide-react";
import { cn } from "@/lib/utils";

interface SummaryCardProps {
  title: string;
  value: number;
  variant: "income" | "expense" | "balance";
}

export function SummaryCard({ title, value, variant }: SummaryCardProps) {
  const icons = {
    income: TrendingUp,
    expense: TrendingDown,
    balance: Wallet,
  };

  const colors = {
    income: "text-green-600",
    expense: "text-red-600",
    balance: "text-blue-600",
  };

  const bgColors = {
    income: "bg-green-50",
    expense: "bg-red-50",
    balance: "bg-blue-50",
  };

  const Icon = icons[variant];

  return (
    <Card>
      <CardHeader className="flex flex-row items-center justify-between space-y-0 pb-2">
        <CardTitle className="text-sm font-medium">{title}</CardTitle>
        <div className={cn("p-2 rounded-lg", bgColors[variant])}>
          <Icon className={cn("h-4 w-4", colors[variant])} />
        </div>
      </CardHeader>
      <CardContent>
        <div className={cn("text-2xl font-bold", colors[variant])}>{formatCurrency(value)}</div>
      </CardContent>
    </Card>
  );
}
