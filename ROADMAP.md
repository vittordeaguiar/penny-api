# Penny API - Roadmap de Desenvolvimento

Última atualização: 2025-12-12 00:40

## 📋 Issues Abertas
### #12 - Documentação com Swagger/OpenAPI

### Descrição:
<!-- Descreva de forma clara e objetiva o que precisa ser implementado -->
Adicionar dependência `springdoc-openapi`. Configurar classe `OpenApiConfig`. Adicionar anotações nos Controllers e DTOs para descrever endpoints e esquemas

### Critérios de Aceite:
<!-- Marque cada item quando completado -->
- [ ] Interface acessível em `/swagger-ui.html`
- [ ] Botão "Authorize" configurado para aceitar o Token JWT

## Dependências
<!-- Issues que precisam estar concluídas antes desta -->
Depende de: Todas as anteriores


---

### #11 - Endpoint de Resumo Financeiro (Dashboard)

### Descrição:
<!-- Descreva de forma clara e objetiva o que precisa ser implementado -->
Implementar `GET /api/transactions/summary`. Criar Query JPQL ou Criteria API no Repository para somar receitas e despesas do mês atual/período.

### Critérios de Aceite:
<!-- Marque cada item quando completado -->
- [ ] Retorna objeto com `totalIncome`, `totalExpense` e `balance`
- [ ] Filtragem por período (opcional, mas recomendado)

## Dependências
<!-- Issues que precisam estar concluídas antes desta -->
Depende de: #8 


---

### #10 - Validações de Negócio Avançadas

### Descrição:
<!-- Descreva de forma clara e objetiva o que precisa ser implementado -->
Implementar validações usando Bean Validation e lógica no Service:
- Data da transação não pode ser futura.
- Valor monetário deve ser positivo.
- Validação de formato de e-mail.

### Critérios de Aceite:
<!-- Marque cada item quando completado -->
- [ ] Tentativa de criar transação futura retorna 400 Bad Request com mensagem descritiva.

## Dependências
<!-- Issues que precisam estar concluídas antes desta -->
Depende de: #8 e #9


---

### #9 - Handler Global de Exceções

### Descrição:
<!-- Descreva de forma clara e objetiva o que precisa ser implementado -->
Implementar `@ControllerAdvice` para capturar exceções (EntityNotFound, MethodArgumentNotValid, etc.). Retornar JSON padronizado com timestamp, status, erro e mensagem .

### Critérios de Aceite:
<!-- Marque cada item quando completado -->
- [ ] Erros de validação retornam lista de campos inválidos
- [ ] Erro 404 retornado quando recurso não é encontrado

## Dependências
<!-- Issues que precisam estar concluídas antes desta -->
Depende de: Sprint 1 e 2

---

### #8 - CRUD de Transações e Paginação

### Descrição:
<!-- Descreva de forma clara e objetiva o que precisa ser implementado -->
Implementar `TransactionController` e `TransactionService`. Endpoints de CRUD básico. Implementar paginação no endpoint `GET /api/transactions` usando `Pageable` do Spring Data.

### Critérios de Aceite:
<!-- Marque cada item quando completado -->
- [ ] Endpoint de listagem suporta parâmetros `?page=0&size=10`
- [ ] Criação de transação exige categoria existente e pertencente ao usuário

## Dependências
<!-- Issues que precisam estar concluídas antes desta -->
Depende de: #3 


---

### #7 - Implementação do Domínio de Transações

### Descrição:
<!-- Descreva de forma clara e objetiva o que precisa ser implementado -->
Criar Enum **TransactionType** (INCOME, EXPENSE). Criar entidade **Transaction** (description, amount, type, date, categoryId, userId). Configurar relacionamentos com User e Category.

### Critérios de Aceite:
<!-- Marque cada item quando completado -->
- [ ] Campos mapeados corretamente no banco (Amout como BigDecimal)
- [ ] Relacionamentos FK configurados

## Dependências
<!-- Issues que precisam estar concluídas antes desta -->
Depende de: #1 


---

### #6 - CRUD de Categorias

### Descrição:
Implementar `CategoryController` e `CategoryService`. Endpoints: GET (listar), GET (por id), POST, PUT, DELETE.

### Critérios de Aceite:
- [ ] Listagem retorna apenas categorias do usuário logado
- [ ] Validação de campos obrigatórios (nome, cor, etc)

## Dependências
<!-- Issues que precisam estar concluídas antes desta -->
Depende de: #1 



---

### #5 - Implementação do Domínio de Categorias

### Descrição:
Criar entidade `Category` (id, name, icon, color, userId). Configurar relacionamento `@ManyToOne` com `User`. Criar DTOs para entrada e saída.

### Critérios de Aceite:
<!-- Marque cada item quando completado -->
- [ ] Usuário só pode acessar/criar categorias vinculadas ao seu ID

## Dependências
<!-- Issues que precisam estar concluídas antes desta -->
Depende de: #2 


---

## ✅ Issues Concluídas
- [x] #4 - Endpoints de Autenticação (Auth Controller)
- [x] #3 - Configuração de Segurança (Spring Security + JWT)
- [x] #2 - Implementação da Entidade User e Repository
- [x] #1 - Inicialização do Projeto e Configuração do Ambiente
