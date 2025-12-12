# Penny API - Roadmap de Desenvolvimento

Última atualização: 2025-12-12 15:33

## 📋 Issues Abertas
### #18 - Documentação com Swagger

### Descrição:
Configurar documentação do projeto realizada com Swagger

### Critérios de Aceite:
- [ ] Endpoint /docs acessa UI do Swagger
- [ ] Todos os endpoints atuais do projeto mapeados para o Swagger

## Dependências
Depende de: Projeto Finalizado


---

### #17 - Preparação para Deploy (Docker)

### Descrição:
Criar Dockerfile para a aplicação (Build Multi-stage com Maven). Criar docker-compose.yml orquestrando API e PostgreSQL.

### Critérios de Aceite:
- [ ] Comando docker-compose up sobe a aplicação completa pronta para uso.

## Dependências
Depende de: Projeto Finalizado


---

### #16 - Documentação Final (README)

### Descrição:
Elaborar README.md na raiz do projeto contendo: stack tecnológica, pré-requisitos (Java 21, Docker), como rodar a aplicação, como rodar os testes e link para o Swagger.

### Critérios de Aceite:
- [ ] Um desenvolvedor novo deve conseguir rodar o projeto apenas lendo o README.


---

### #15 - Testes de Integração

### Descrição:
<!-- Descreva de forma clara e objetiva o que precisa ser implementado -->
Implementar testes usando TestContainers (subindo container PostgreSQL) e MockMvc para testar os endpoints REST de ponta a ponta.

### Critérios de Aceite:
<!-- Marque cada item quando completado -->
- [ ] Teste do fluxo de Registro -> Login -> Criar Categoria -> Criar Transação passando com sucesso.

## Dependências
<!-- Issues que precisam estar concluídas antes desta -->
Depende de: #9


---

## ✅ Issues Concluídas
- [x] #14 - Testes Unitários (Service Layer)
- [x] #12 - Documentação com Swagger/OpenAPI
- [x] #11 - Endpoint de Resumo Financeiro (Dashboard)
- [x] #10 - Validações de Negócio Avançadas
- [x] #9 - Handler Global de Exceções
- [x] #8 - CRUD de Transações e Paginação
- [x] #7 - Implementação do Domínio de Transações
- [x] #6 - CRUD de Categorias
- [x] #5 - Implementação do Domínio de Categorias
- [x] #4 - Endpoints de Autenticação (Auth Controller)
- [x] #3 - Configuração de Segurança (Spring Security + JWT)
- [x] #2 - Implementação da Entidade User e Repository
- [x] #1 - Inicialização do Projeto e Configuração do Ambiente
