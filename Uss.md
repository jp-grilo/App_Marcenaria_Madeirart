# 📋 Backlog de Desenvolvimento: Gestão Financeira Marcenaria

## Épico 1: Ciclo de Vida do Orçamento (Draft & Auditoria)

### US01: Elaboração do Orçamento Técnico

**Descrição:** Como marceneiro, quero cadastrar um orçamento detalhando materiais e taxas para calcular o preço final de forma precisa.

- **Campos:** Cliente, Móveis, Data, Previsão de Entrega, Itens (Quantidade, Descrição, Valor Unitário), Fator (Mão de Obra), Custos Extras e CPC.

- **Cálculos Automáticos:**
- Esses valores não serão preenchidos manualmente, sendo calculados com base nos valores disponíveis
- Custo da obra = (Soma do valor dos materiais) \* Fator mão de obra
- Valor total = Custo da obra + custos extras + CPC

- **Tarefas Backend:**
- Entidades `Orcamento` e `ItemMaterial` com os campos de cálculo.
- Endpoint `POST /orcamentos`.

- **Tarefas Frontend:**
- Formulário dinâmico para adição de materiais.
- Cálculo em tempo real no estado do componente antes do envio.

---

### US02: Edição de Orçamentos com Histórico de Auditoria

**Descrição:** Como marceneiro, quero alterar orçamentos a qualquer momento, mantendo um registro de todas as versões anteriores para conferência.

- **Critérios de Aceite:** Toda vez que um orçamento sofrer `UPDATE`, o estado anterior deve ser salvo em uma tabela de log.
- **Tarefas Backend:**
- Entidade `OrcamentoAuditoria` para salvar snapshots (ex: JSON da versão anterior ou campos-chave).
- Lógica no Service para disparar a cópia antes de salvar a nova versão.

- **Tarefas Frontend:**
- Habilitar edição na tela de detalhamento.
- Aba "Histórico" exibindo data da alteração e valores antigos.

---

## Épico 2: Produção e Gestão de Recebimentos

### US03: Ativação de Produção e Plano de Parcelamento

**Descrição:** Como marceneiro, quero mudar o status para "Iniciada" e definir como o pagamento será feito, separando a negociação da execução.

- **Critérios de Aceite:** Ao mudar status para `INICIADA`, o sistema deve exigir o preenchimento da entrada e das datas das parcelas.
- **Tarefas Backend:**
- Enum `StatusOrcamento` (AGUARDANDO, INICIADA, FINALIZADA, CANCELADA).
- Endpoint `PATCH /orcamentos/{id}/iniciar` recebendo o plano de parcelas.
- Geração de registros na tabela `Parcela`.

- **Tarefas Frontend:**
- Modal de "Confirmação de Produção" com campos de parcelamento.

---

### US04: Confirmação Manual de Pagamentos

**Descrição:** Mesmo que uma parcela esteja planejada, quero confirmar manualmente o recebimento para que o sistema reflita o dinheiro que realmente entrou.

- **Critérios de Aceite:** As parcelas nascem com status `PENDENTE`. O sistema deve exigir um clique para mudar para `PAGO`.
- **Tarefas Backend:**
- Endpoint `PATCH /parcelas/{id}/confirmar` para atualizar data de recebimento real.

- **Tarefas Frontend:**
- Lista de parcelas no detalhamento do orçamento com botões de "Confirmar Recebimento".

---

## Épico 3: Monitoramento Financeiro

### US05: Indicador de Progresso de Pagamento no Detalhe

**Descrição:** Na tela de detalhamento do orçamento, quero ver visualmente quanto do valor total já foi efetivamente pago.

    	**Descrição:** Na tela de **Detalhamento do Orçamento**, quero ver visualmente quanto do valor total já foi efetivamente pago, com informações detalhadas e ações manuais para confirmação de recebimento.

    	**Critérios de Aceite:**
    		- Exibir uma área (estilo card) dentro do detalhamento com:
    			- "Total do Orçamento: R$ X"
    			- "Total Já Confirmado: R$ Y" (Baseado em parcelas marcadas manualmente como `PAGO`).
    			- "Progresso: [Barra de porcentagem]".
    		- Lista de parcelas com botão "Confirmar Recebimento" para efetivar o valor no caixa.

    	**Tarefas Backend:**
    		- Endpoint de detalhe deve retornar o `Soma(ParcelasPagas)`, `ValorTotalOrcamento` e status das parcelas.
    		- Endpoint `PATCH /parcelas/{id}/confirmar` para atualizar data de recebimento real.

    	**Tarefas Frontend:**
    		- Widget de Status de Recebimento (card com totais e barra de progresso).
    		- Lista de parcelas no detalhamento do orçamento com botões de "Confirmar Recebimento".
    		- Barra de progresso ou gráfico de rosca exibindo o percentual já recebido.

- Componente de Barra de Progresso ou Gráfico de Rosca na lateral do detalhamento/formulário.

---

### US06: Listagem e Filtro de Orçamentos por Status

**Descrição:** Quero ver todos os orçamentos e seus respectivos status para organizar minha oficina.

- **Tarefas Backend:** Endpoint `GET /orcamentos` com suporte a filtros de status.
- **Tarefas Frontend:** Tabela principal com Badges coloridos para os status e link para o detalhamento/edição.

Aqui estão as novas User Stories (USs) detalhadas para os módulos de custos, dashboard e relatórios, seguindo a estrutura técnica e os requisitos que você definiu.

---

## Épico 4: Gestão de Despesas (Custos)

### US07: Gestão de Custos Fixos e Variáveis

**Descrição:** Como marceneiro, quero cadastrar meus custos fixos (recorrentes) e variáveis (pontuais) para que o sistema possa prever e registrar as saídas de caixa.

### Critérios de Aceite

- Para **Custos Fixos**: Informar nome, valor e o dia do mês para débito automático no sistema
- Para **Custos Variáveis**: Informar nome, valor e data específica
- O sistema deve permitir editar ou excluir esses lançamentos

### Tarefas Backend (Spring Boot)

- [ ] Criar entidades `CustoFixo` (nome, valor, diaVencimento) e `CustoVariavel` (nome, valor, dataLancamento)
- [ ] Implementar endpoints de CRUD para `CustoFixo`
- [ ] Implementar endpoints de CRUD para `CustoVariavel`
- [ ] Implementar lógica para projetar o `CustoFixo` em todos os meses futuros no fluxo de caixa

### Tarefas Frontend (React)

- [ ] Criar tela de "Gestão de Custos" com duas abas ou seções distintas
- [ ] Implementar formulário para custo fixo com seletor numérico (1 a 31) para o dia de débito
- [ ] Implementar formulário para custo variável com seletor de data
- [ ] Criar listagens para ambos os tipos de custos com ações de editar e excluir

---

## Épico 5: Dashboard e Calendário de Fluxo de Caixa

### US08: Dashboard de Calendário Financeiro

**Descrição:**

Como marceneiro, quero visualizar no dashboard uma seção com as informações atualizadas sobre orçamentos ativos, em produção e próximos da data entrega; Quero outra seção com a projeção da receita estimada para o final do mês considerando tudo que tenho de receber depois de descontado o que preciso pagar; Quero um calendário com cores indicadoras de entradas e saídas em cada dia para entender minha movimentação financeira de forma visual e rápida.

### Critérios de Aceite

- Cada seção estará no seu próprio card separado
- Os cards presentes devem refletir as informações reais
- O calendário deve mostrar uma marcação verde para dias com entradas (parcelas pagas/a receber) e vermelha para dias com saídas (custos)
- Ao clicar em um dia, um modal deve abrir listando individualmente cada transação
- **Regra de Exibição:** O modal não deve somar os valores; se houver 3 saídas de R$ 100, deve mostrar as três linhas separadamente
- O card de projeção deve calcular: Receita Prevista do Mês - Despesas Previstas do Mês = Saldo Projetado
- O card de orçamentos deve mostrar: Total de orçamentos ativos, quantidade em produção, e lista dos próximos a vencer (5 dias)

### Tarefas Backend (Spring Boot)

**Estruturas e DTOs:**

- [ ] Criar DTO `DashboardResumoDTO` com campos: totalOrcamentosAtivos, totalEmProducao, orcamentosProximosEntrega (Lista)
- [ ] Criar DTO `ProjecaoFinanceiraDTO` com campos: receitaPrevista, despesaPrevista, saldoProjetado, mesReferencia
- [ ] Criar DTO `CalendarioDTO` com campos: ano, mes, dias (Map<Integer, DiaDadosDTO>)
- [ ] Criar DTO `DiaDadosDTO` com campos: dia, temEntradas, temSaidas, entradas (Lista<TransacaoDTO>), saidas (Lista<TransacaoDTO>)
- [ ] Criar DTO `TransacaoDTO` com campos: id, tipo (ENTRADA/SAIDA), descricao, valor, origem (PARCELA/CUSTO_FIXO/CUSTO_VARIAVEL), status

**Endpoints:**

- [ ] Criar endpoint `GET /dashboard/resumo` para retornar estatísticas de orçamentos
- [ ] Criar endpoint `GET /dashboard/projecao?mes=X&ano=Y` para retornar projeção financeira do mês
- [ ] Criar endpoint `GET /financeiro/calendario?mes=X&ano=Y` para retornar dados do calendário com transações agrupadas por dia

**Services:**

- [ ] Criar `DashboardService` com método `getResumoOrcamentos()` que busca orçamentos com status INICIADA e AGUARDANDO
- [ ] Implementar método `getOrcamentosProximosEntrega(int dias)` que filtra orçamentos pela data de previsão de entrega
- [ ] Criar `ProjecaoFinanceiraService` com método `calcularProjecaoMensal(int mes, int ano)`
- [ ] Implementar lógica para somar parcelas PENDENTES do mês como receita prevista
- [ ] Implementar lógica para somar custos fixos e variáveis do mês como despesa prevista
- [ ] Criar `CalendarioFinanceiroService` com método `getCalendarioMensal(int mes, int ano)`
- [ ] Implementar consolidação de `Parcela` (status PENDENTE e PAGO) agrupadas por data de vencimento/recebimento
- [ ] Implementar consolidação de `CustoFixo` (projetado para o dia do mês) e `CustoVariavel` agrupados por data
- [ ] Adicionar tratamento para meses sem transações (retornar calendário vazio mas estruturado)

### Tarefas Frontend (React)

**Componentes de Cards:**

- [ ] Criar componente `CardResumoOrcamentos.jsx` em `views/Dashboard/components/`
- [ ] Exibir total de orçamentos ativos, quantidade em produção com ícones e badges coloridos
- [ ] Criar lista de orçamentos próximos da entrega com cliente e data
- [ ] Criar componente `CardProjecaoFinanceira.jsx` em `views/Dashboard/components/`
- [ ] Exibir receita prevista, despesa prevista e saldo projetado com cores condicionais (verde para positivo, vermelho para negativo)
- [ ] Adicionar indicador visual de percentual de margem

**Componente de Calendário:**

- [ ] Criar componente `CalendarioFinanceiro.jsx` em `views/Dashboard/components/`
- [ ] Implementar hook customizado `useCalendario` para gerenciar estado do mês/ano atual
- [ ] Renderizar grid de calendário com cabeçalho de dias da semana
- [ ] Implementar lógica para calcular primeiro dia do mês e total de dias
- [ ] Criar componente `DiaCelula.jsx` para cada célula do calendário
- [ ] Implementar lógica de estilo condicional: borda verde para dias com entradas, borda vermelha para saídas, borda amarela para ambos
- [ ] Adicionar indicadores visuais (badges ou ícones) mostrando quantidade de transações
- [ ] Implementar navegação de mês anterior/próximo com botões e atualização de estado

**Modal de Detalhes:**

- [ ] Criar componente `ModalDetalheDia.jsx` em `views/Dashboard/components/`
- [ ] Exibir cabeçalho com data formatada do dia selecionado
- [ ] Criar seção separada para Entradas com lista de `TransacaoDTO` tipo ENTRADA
- [ ] Criar seção separada para Saídas com lista de `TransacaoDTO` tipo SAIDA
- [ ] Renderizar cada transação em linha individual mostrando descrição, origem e valor formatado
- [ ] Adicionar total por seção (Entradas e Saídas) no rodapé de cada lista
- [ ] Implementar fechamento do modal ao clicar fora ou no botão fechar

**Integração e Estado:**

- [ ] Criar service `dashboardService.js` com métodos para chamar os 3 endpoints
- [ ] Implementar estado global ou local para armazenar mês/ano selecionado
- [ ] Adicionar loading states para cada card e calendário durante fetch de dados
- [ ] Implementar tratamento de erros com mensagens amigáveis via Snackbar
- [ ] Adicionar refresh automático ou manual dos dados do dashboard

---

## Épico 6: Relatórios e Projeções

### US09: Relatório Detalhado (Extrato Financeiro)

**Descrição:**

Como marceneiro, quero uma tela de extrato detalhado para auditar todas as transações passadas e visualizar o que está planejado para o futuro.

### Critérios de Aceite

- Lista em ordem cronológica (mais recente para mais antiga por padrão)
- Cada linha deve mostrar: Data, Descrição, Tipo (Entrada/Saída), Forma de Pagamento e Valor
- Filtros obrigatórios: Intervalo de datas, Tipo (Entrada/Saída) e Forma de Pagamento (Pix, Débito, Crédito, etc.)

### Tarefas Backend (Spring Boot)

- [ ] Criar endpoint `GET /financeiro/extrato` com filtros via Query Parameters
- [ ] Implementar uso de _Spring Data JPA Specifications_ ou _Criteria API_ para os filtros dinâmicos
- [ ] Adicionar suporte para paginação e ordenação

### Tarefas Frontend (React)

- [ ] Criar view `views/Financeiro/RelatorioFinanceiro.jsx`
- [ ] Implementar componente de Filtro Lateral ou Topo
- [ ] Criar tabela de extrato com estilização distinta para entradas (+) e saídas (-)
- [ ] Adicionar controles de data range, tipo e forma de pagamento

---

### US10: Visualização de Projeções Futuras

**Descrição:**

Como marceneiro, quero poder filtrar transações futuras no meu extrato para antecipar como estará meu caixa nos próximos meses.

### Critérios de Aceite

- Ao ativar o filtro "Transações Futuras", a lista deve inverter a lógica: a transação mais distante no futuro deve aparecer no topo
- Deve incluir as parcelas de orçamentos `INICIADA` ainda não pagas e os custos fixos dos meses seguintes

### Tarefas Backend (Spring Boot)

- [ ] Implementar lógica no `FinanceiroService` para unir transações reais (passado) e transações previstas (futuro) na mesma resposta de API
- [ ] Adicionar parâmetro de ordenação `sort=desc` ou `sort=asc` baseado na data
- [ ] Implementar projeção de custos fixos para meses futuros

### Tarefas Frontend (React)

- [ ] Adicionar Toggle/Switch de "Ver Futuro" na tela de Relatório
- [ ] Implementar lógica de reordenação automática da lista ao ativar a visão de projeção
- [ ] Adicionar indicador visual diferenciando transações já efetivadas de projeções
