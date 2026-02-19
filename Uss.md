# 📋 Backlog de Desenvolvimento: Gestão Financeira Marcenaria

## Épico 1: Ciclo de Vida do Orçamento (Draft & Auditoria)

### US01: Elaboração do Orçamento Técnico

**Descrição:** Como marceneiro, quero cadastrar um orçamento detalhando materiais e taxas para calcular o preço final de forma precisa.

- **Campos:** Cliente, Móveis, Data, Previsão de Entrega, Itens (Quantidade, Descrição, Valor Unitário), Fator (Mão de Obra), Custos Extras e CPC.

- **Cálculos Automáticos:**
- Esses valores não serão preenchidos manualmente, sendo calculados com base nos valores disponíveis
- Custo da obra = (Soma do valor dos materiais) * Fator mão de obra 
- Valor total =  Custo da obra + custos extras + CPC

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

- **Critérios de Aceite:**
- Para **Custos Fixos**: Informar nome, valor e o dia do mês para débito automático no sistema.
- Para **Custos Variáveis**: Informar nome, valor e data específica.
- O sistema deve permitir editar ou excluir esses lançamentos.

- **Tarefas Backend (Spring Boot):**
- Criar entidades `CustoFixo` (nome, valor, diaVencimento) e `CustoVariavel` (nome, valor, dataLancamento).
- Implementar endpoints de CRUD para ambas as entidades.
- Lógica para projetar o `CustoFixo` em todos os meses futuros no fluxo de caixa.

- **Tarefas Frontend (React):**
- Tela de "Gestão de Custos" com duas abas ou seções distintas.
- Formulário para custo fixo com seletor numérico (1 a 31) para o dia de débito.
- Formulário para custo variável com seletor de data.

---

## Épico 5: Dashboard e Calendário de Fluxo de Caixa

### US08: Dashboard de Calendário Financeiro

**Descrição:** Como marceneiro, quero visualizar um calendário com indicadores de entradas e saídas diárias para entender minha movimentação financeira de forma visual e rápida.

- **Critérios de Aceite:**
- O calendário deve mostrar uma marcação verde para dias com entradas (parcelas pagas/a receber) e vermelha para dias com saídas (custos).
- Ao clicar em um dia, um modal deve abrir listando individualmente cada transação.
- **Regra de Exibição:** O modal não deve somar os valores; se houver 3 saídas de R$ 100, deve mostrar as três linhas separadamente.

- **Tarefas Backend (Spring Boot):**
- Endpoint `GET /financeiro/calendario?mes=X&ano=Y`.
- Lógica para consolidar dados de `Parcela` (Entradas) e `Custos` (Saídas) agrupados por dia.

- **Tarefas Frontend (React):**
- Implementar componente de Calendário em `views/Dashboard/components/`.
- Lógica de estilo condicional (CSS) para os indicadores de cores.
- Componente `ModalDetalheDia` que mapeia a lista de transações do dia selecionado.

---

## Épico 6: Relatórios e Projeções

### US09: Relatório Detalhado (Extrato Financeiro)

**Descrição:** Como marceneiro, quero uma tela de extrato detalhado para auditar todas as transações passadas e visualizar o que está planejado para o futuro.

- **Critérios de Aceite:**
- Lista em ordem cronológica (mais recente para mais antiga por padrão).
- Cada linha deve mostrar: Data, Descrição, Tipo (Entrada/Saída), Forma de Pagamento e Valor.
- Filtros obrigatórios: Intervalo de datas, Tipo (Entrada/Saída) e Forma de Pagamento (Pix, Débito, Crédito, etc.).

- **Tarefas Backend (Spring Boot):**
- Endpoint `GET /financeiro/extrato` com filtros via Query Parameters.
- Uso de _Spring Data JPA Specifications_ ou _Criteria API_ para os filtros dinâmicos.

- **Tarefas Frontend (React):**
- View `views/Financeiro/RelatorioFinanceiro.jsx`.
- Componente de Filtro Lateral ou Topo.
- Tabela de extrato com estilização distinta para entradas (+) e saídas (-).

---

### US10: Visualização de Projeções Futuras

**Descrição:** Como marceneiro, quero poder filtrar transações futuras no meu extrato para antecipar como estará meu caixa nos próximos meses.

- **Critérios de Aceite:**
- Ao ativar o filtro "Transações Futuras", a lista deve inverter a lógica: a transação mais distante no futuro deve aparecer no topo.
- Deve incluir as parcelas de orçamentos `INICIADA` ainda não pagas e os custos fixos dos meses seguintes.

- **Tarefas Backend (Spring Boot):**
- Lógica no `FinanceiroService` para unir transações reais (passado) e transações previstas (futuro) na mesma resposta de API.
- Parâmetro de ordenação `sort=desc` ou `sort=asc` baseado na data.

- **Tarefas Frontend (React):**
- Toggle/Switch de "Ver Futuro" na tela de Relatório.
- Lógica de reordenação automática da lista ao ativar a visão de projeção.

---

### 💡 Indicação Visual de Pagamento (Adição à US de Detalhe)

Para a tela de **Detalhamento do Orçamento**, incluiremos:

- **Widget de Status de Recebimento:** Uma área (estilo card) dentro do detalhamento que exibe:
- "Total do Orçamento: R$ X"
- "Total Já Confirmado: R$ Y" (Baseado em parcelas marcadas manualmente como `PAGO`).
- "Progresso: [Barra de porcentagem]".

- **Ação Manual:** Lista de parcelas com o botão "Confirmar Recebimento" que o usuário deve clicar para efetivar o valor no caixa.