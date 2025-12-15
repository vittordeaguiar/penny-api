# Penny Web - Plataforma de Gestão Financeira

Aplicação web moderna desenvolvida com React Router v7 para consumir a API Penny (gestão financeira).

## Stack Tecnológica

- **React 19** + **React Router 7** (SSR habilitado)
- **TypeScript 5.9.2**
- **Tailwind CSS 4**
- **TanStack Query** - Gerenciamento de estado e cache HTTP
- **shadcn/ui** - Componentes UI
- **React Hook Form** + **Zod** - Validação de formulários
- **Axios** - Cliente HTTP

## Funcionalidades

- Sistema de Login/Registro com JWT
- Dashboard Financeiro (resumo de receitas, despesas e saldo)
- Gestão Completa de Transações (CRUD + paginação)
- Gestão Completa de Categorias (CRUD com cores e ícones)
- Loading states e tratamento de erros
- Confirmações de exclusão
- Interface responsiva

## Pré-requisitos

- Node.js 18+
- API Penny rodando em `http://localhost:8080`

## Instalação

```bash
npm install
```

## Configuração

Crie um arquivo `.env` na raiz do projeto (se não existir):

```env
VITE_API_BASE_URL=http://localhost:8080
```

## Executar em Desenvolvimento

```bash
npm run dev
```

A aplicação estará disponível em `http://localhost:5173`

## Build para Produção

```bash
npm run build
npm run start
```

## Type Checking

```bash
npm run typecheck
```

## Estrutura do Projeto

```
/web/app/
├── routes/                          # Rotas (file-based routing)
│   ├── _auth.tsx                    # Layout público
│   ├── _auth.login.tsx              # Página de login
│   ├── _auth.register.tsx           # Página de registro
│   ├── _app.tsx                     # Layout autenticado
│   ├── _app.dashboard.tsx           # Dashboard
│   ├── _app.transactions._index.tsx # Lista de transações
│   └── _app.categories._index.tsx   # Lista de categorias
├── components/
│   ├── ui/                          # Componentes shadcn/ui
│   ├── layout/                      # Navbar, PageHeader
│   ├── auth/                        # LoginForm, RegisterForm
│   ├── dashboard/                   # SummaryCard, RecentTransactions
│   ├── transactions/                # TransactionTable, TransactionForm, TransactionDialog
│   └── categories/                  # CategoryGrid, CategoryCard, CategoryForm
├── lib/
│   ├── api/                         # Services (client, auth, transactions, categories)
│   ├── hooks/                       # TanStack Query hooks
│   ├── contexts/                    # AuthContext
│   └── utils/                       # Utilitários (format, cn)
├── types/
│   └── api.types.ts                 # DTOs da API
└── constants/
    └── api.constants.ts             # Constantes (URLs, endpoints, keys)
```

## Rotas

- `/` - Redireciona para `/dashboard` ou `/login`
- `/login` - Página de login
- `/register` - Página de registro
- `/dashboard` - Dashboard financeiro
- `/transactions` - Gestão de transações
- `/categories` - Gestão de categorias

## Autenticação

O sistema utiliza JWT armazenado no `localStorage`:
- Token: `penny_auth_token`
- Dados do usuário: `penny_user_data`

Todas as requisições autenticadas incluem o header:
```
Authorization: Bearer {token}
```

## Formatação

- **Moeda**: R$ 1.234,56 (pt-BR)
- **Data**: dd/MM/yyyy (pt-BR)
- **Cores**: Formato hexadecimal (#FF5733)

## Validações

### Frontend (Zod)
- Email válido
- Senha mínima de 6 caracteres
- Valores monetários > 0.01
- Data não pode ser no futuro
- Cor em formato hexadecimal

### Backend
- Todas as regras de negócio são validadas pela API
- Categoria e transação devem pertencer ao usuário autenticado
