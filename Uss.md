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

---

### US11: Projeção de Fluxo de Caixa com Saldo Acumulado

**Descrição:**

Como marceneiro, quero visualizar uma tela de projeção de caixa que mostre o saldo acumulado desde o início e projete o fluxo para os próximos dois meses.

### Critérios de Aceite

- Exibir o **saldo atual** = (Saldo Inicial + Entradas Confirmadas - Saídas) desde o início do sistema
- Mostrar projeção detalhada para os **próximos 2 meses** com: Saldo Inicial, Entradas Previstas, Saídas Previstas e Saldo Final
- Visualização em tabela e gráfico de linha
- Permitir cadastrar um **saldo inicial** do sistema

### Tarefas Backend (Spring Boot)

- [ ] Criar entidade `SaldoInicial` para armazenar o saldo de abertura do sistema
- [ ] Criar DTOs para resposta: `ProjecaoCaixaDTO`, `MesProjecaoDTO` e `ItemProjecaoDTO`
- [ ] Endpoint `GET /financeiro/projecao-caixa` retornando saldo atual e projeção dos próximos 2 meses
- [ ] Endpoint `POST /financeiro/saldo-inicial` para cadastrar/atualizar saldo inicial
- [ ] Criar `ProjecaoCaixaService` com lógica de:
  - Cálculo do saldo atual (Saldo Inicial + Parcelas PAGAS - Custos até hoje)
  - Projeção de entradas (parcelas PENDENTES por mês)
  - Projeção de saídas (custos fixos projetados + custos variáveis cadastrados)
  - Cálculo cascata: saldo final de um mês é saldo inicial do próximo

### Tarefas Frontend (React)

- [ ] Adicionar item "Projeção de Caixa" no menu de navegação
- [ ] Criar view `views/Financeiro/ProjecaoCaixa.jsx`
- [ ] Card exibindo saldo atual com cores condicionais e botão para definir saldo inicial
- [ ] Modal para cadastro/edição do saldo inicial
- [ ] Tabela com os meses projetados (colunas: Mês, Saldo Inicial, Entradas, Saídas, Saldo Final)
- [ ] Linhas expansíveis na tabela mostrando detalhes das transações
- [ ] Gráfico de linha mostrando evolução do saldo
- [ ] Criar `projecaoCaixaService.js` com métodos de integração à API

---

## Épico 7: Transformação em Aplicação Desktop Nativa para Windows

### US12: Compilação do Backend para Executável Nativo (.exe)

**Descrição:**

Como desenvolvedor, quero compilar o backend Spring Boot para um executável nativo do Windows (.exe) usando GraalVM Native Image, eliminando a necessidade de JRE instalado e garantindo boot instantâneo com baixo consumo de memória.

### Critérios de Aceite

- O backend deve ser compilado para um executável nativo `.exe` sem dependências externas de Java
- O executável deve iniciar em menos de 3 segundos
- Consumo de memória RAM deve ser inferior a 100MB em repouso
- Todas as funcionalidades existentes devem funcionar corretamente no binário nativo
- O banco SQLite deve ser acessado corretamente pelo executável

### Tarefas Backend (Spring Boot + GraalVM)

**Configuração de Ambiente:**

- [ ] Baixar e instalar GraalVM 22+ para Windows (https://www.graalvm.org/downloads/)
- [ ] Configurar variáveis de ambiente `JAVA_HOME` apontando para GraalVM
- [ ] Adicionar GraalVM `bin` ao `PATH` do sistema
- [ ] Verificar instalação com `native-image --version`

**Configuração do Projeto:**

- [ ] Atualizar `pom.xml` para Spring Boot 3.2.0+ (compatível com Native Image)
- [ ] Adicionar dependência `spring-boot-starter-native` no `pom.xml`
- [ ] Configurar plugin `native-maven-plugin` do GraalVM no `pom.xml`
- [ ] Adicionar profile Maven específico para compilação nativa

**Configuração de Reflection e Resources:**

- [ ] Criar arquivo `src/main/resources/META-INF/native-image/reflect-config.json`
- [ ] Mapear todas as entidades JPA para reflection (Orcamento, Parcela, CustoFixo, CustoVariavel, etc.)
- [ ] Mapear todos os DTOs para serialização/deserialização
- [ ] Criar `resource-config.json` para incluir arquivos de migração do Flyway/Liquibase
- [ ] Criar `serialization-config.json` para classes serializadas via JSON

**Ajustes de Código:**

- [ ] Substituir uso de reflection dinâmica por alternativas compatíveis com GraalVM
- [ ] Validar que não há uso de `Class.forName()` ou `ClassLoader` dinâmico
- [ ] Adicionar hints de Native Image para bibliotecas de terceiros (se necessário)
- [ ] Testar geração de queries do Hibernate em tempo de build

**Compilação e Testes:**

- [ ] Executar `mvnw -Pnative native:compile` para gerar o executável
- [ ] Verificar geração do arquivo `target\madeirart-backend.exe`
- [ ] Testar inicialização do executável standalone
- [ ] Validar todos os endpoints REST com ferramentas como Postman
- [ ] Verificar criação e acesso ao banco SQLite
- [ ] Medir tempo de boot e consumo de memória

### Tarefas Frontend (Preparação para Electron)

- [ ] Adicionar script de build para produção que gere assets otimizados
- [ ] Configurar `vite.config.js` para output compatível com Electron
- [ ] Testar build local com `npm run build`

---

### US13: Encapsulamento do Frontend em Aplicação Desktop com Electron

**Descrição:**

Como desenvolvedor, quero encapsular o frontend React em uma aplicação desktop usando Electron, permitindo que a interface rode nativamente sem navegador externo e integrando com o backend nativo.

### Critérios de Aceite

- O frontend deve abrir em uma janela nativa do Windows (não em navegador)
- A aplicação deve iniciar automaticamente o backend `.exe` ao abrir
- Ao fechar a janela, o processo do backend deve ser encerrado gracefully
- A aplicação deve ter ícone personalizado e nome "Madeirart"
- Não deve haver dependência de navegadores instalados

### Tarefas de Configuração (Electron)

**Instalação e Setup Inicial:**

- [ ] Instalar dependências: `npm install --save-dev electron electron-builder`
- [ ] Criar arquivo `electron.js` na raiz do projeto frontend
- [ ] Atualizar `package.json` definindo `"main": "electron.js"`

**Implementação do Main Process:**

- [ ] Implementar função `startBackend()` para spawn do processo `marcenaria-backend.exe`
- [ ] Configurar path do backend usando `path.join(__dirname, 'resources', 'marcenaria-backend.exe')`
- [ ] Adicionar listeners para `stdout` e `stderr` do backend para logging
- [ ] Implementar função `createWindow()` com dimensões 1200x800
- [ ] Configurar `BrowserWindow` com segurança adequada (`nodeIntegration: false`, `contextIsolation: true`)
- [ ] Adicionar delay de 3 segundos antes de carregar `http://localhost:8080`
- [ ] Implementar handler `app.on('window-all-closed')` para matar processo backend
- [ ] Adicionar handler `app.on('before-quit')` para shutdown graceful

**Configuração de Build:**

- [ ] Adicionar seção `build` no `package.json` com configurações do electron-builder
- [ ] Configurar `appId: "com.madeirart.desktop"`
- [ ] Definir `productName: "Madeirart"`
- [ ] Configurar target `nsis` para instalador Windows
- [ ] Adicionar ícone da aplicação em `assets/icon.ico`
- [ ] Configurar `extraResources` para incluir o backend `.exe` da pasta `../Backend/target/`

**Scripts e Automação:**

- [ ] Adicionar script `"electron:dev": "electron ."` para desenvolvimento
- [ ] Adicionar script `"electron:build": "electron-builder"` para build de produção
- [ ] Criar script de build completo que compila backend e frontend em sequência

### Tarefas de Integração e Testes

- [ ] Copiar `marcenaria-backend.exe` para pasta `resources/` do projeto frontend
- [ ] Testar inicialização em modo desenvolvimento com `npm run electron:dev`
- [ ] Validar que backend inicia corretamente e aceita conexões
- [ ] Verificar carregamento do frontend na janela Electron
- [ ] Testar funcionalidades completas da aplicação (CRUD de orçamentos, custos, etc.)
- [ ] Validar que o backend é encerrado ao fechar a aplicação
- [ ] Testar em máquina Windows limpa (sem Node.js instalado)

---

### US14: Configuração de Persistência Local com SQLite

**Descrição:**

Como desenvolvedor, quero configurar o banco de dados SQLite para armazenar dados localmente na pasta `AppData` do usuário, garantindo portabilidade e facilitando backups.

### Critérios de Aceite

- O arquivo de banco de dados deve ser criado em `%APPDATA%/Madeirart/marcenaria.db`
- O diretório deve ser criado automaticamente se não existir
- As migrações do banco devem executar automaticamente na primeira inicialização
- O arquivo de banco deve ser acessível para backup manual pelo usuário
- Não deve haver conflitos de permissão de escrita

### Tarefas Backend (Spring Boot)

**Configuração do DataSource:**

- [ ] Atualizar `application.properties` com `spring.datasource.url=jdbc:sqlite:${user.home}/AppData/Local/Madeirart/marcenaria.db`
- [ ] Configurar `spring.datasource.driver-class-name=org.sqlite.JDBC`
- [ ] Definir `spring.jpa.hibernate.ddl-auto=update` para criar tabelas automaticamente
- [ ] Adicionar propriedades específicas do SQLite (dialect, etc.)

**Dependências:**

- [ ] Adicionar dependência `sqlite-jdbc` versão 3.44.1.0+ no `pom.xml`
- [ ] Validar compatibilidade do driver com GraalVM Native Image
- [ ] Adicionar hints de Native Image se necessário para o driver JDBC

**Inicialização e Migração:**

- [ ] Criar bean de inicialização que verifica/cria o diretório `AppData/Local/Madeirart`
- [ ] Configurar Flyway ou Liquibase para migrações de schema (se aplicável)
- [ ] Criar scripts SQL de migração em `src/main/resources/db/migration/`
- [ ] Implementar dados de seed opcionais para primeira inicialização

**Testes de Persistência:**

- [ ] Validar criação automática do arquivo de banco
- [ ] Testar operações CRUD em todas as entidades
- [ ] Verificar integridade referencial (foreign keys)
- [ ] Validar queries complexas (joins, aggregations)
- [ ] Testar performance com volume médio de dados (100+ orçamentos)

---

### US15: Geração de Instalador Windows com Build Final

**Descrição:**

Como desenvolvedor, quero gerar um instalador Windows (.exe) auto-contido que empacote backend nativo, frontend Electron e todas as dependências, permitindo distribuição simplificada para usuários finais.

### Critérios de Aceite

- O instalador deve ser um único arquivo `.exe` (NSIS)
- A instalação não deve exigir permissões de administrador
- O usuário deve poder escolher o diretório de instalação
- Atalho no Menu Iniciar e Desktop devem ser criados automaticamente
- O desinstalador deve remover todos os arquivos (exceto dados em AppData)
- Tamanho final do instalador deve ser inferior a 150MB

### Tarefas de Build e Empacotamento

**Configuração do Electron Builder:**

- [ ] Configurar seção `win` no `package.json` com target `nsis`
- [ ] Definir ícone do instalador e da aplicação
- [ ] Configurar metadados (nome do publisher, versão, copyright)
- [ ] Adicionar configurações de compressão para reduzir tamanho do instalador

**Inclusão de Recursos:**

- [ ] Garantir que `dist/` do Vite está incluído nos arquivos empacotados
- [ ] Configurar `extraResources` para incluir `marcenaria-backend.exe`
- [ ] Adicionar arquivos de licença e documentação (README, CHANGELOG)

**Build Automatizado:**

- [ ] Criar script `build-all.sh` ou `build-all.bat` que:
  - Compila o backend nativo com Maven
  - Faz build do frontend com Vite
  - Copia o backend para resources
  - Executa electron-builder
- [ ] Adicionar validação de pré-requisitos (GraalVM instalado, Node.js, etc.)
- [ ] Implementar logging de progresso do build

**Teste de Instalação:**

- [ ] Executar `npm run electron:build` para gerar instalador
- [ ] Localizar instalador em `dist/Madeirart Setup 1.0.0.exe`
- [ ] Testar instalação em máquina Windows limpa (VM recomendada)
- [ ] Validar criação de atalhos no Menu Iniciar e Desktop
- [ ] Testar execução completa da aplicação após instalação
- [ ] Verificar que todas as funcionalidades funcionam (CRUD completo)
- [ ] Testar processo de desinstalação
- [ ] Validar que dados em AppData são preservados após desinstalação

**Otimizações Finais:**

- [ ] Minificar assets do frontend para reduzir tamanho
- [ ] Configurar tree-shaking no Vite para eliminar código não utilizado
- [ ] Testar compressão do instalador (UPX ou similar)
- [ ] Medir tempo de inicialização e memória consumida

---

### US16: Implementação de Rotina de Backup Automático

**Descrição:**

Como usuário, quero que o sistema faça backup automático do meu banco de dados sempre que eu fechar a aplicação, salvando em uma pasta de fácil acesso para sincronização com nuvem.

### Critérios de Aceite

- Ao fechar a aplicação, uma cópia do arquivo `marcenaria.db` deve ser criada
- Os backups devem ser salvos em `%USERPROFILE%/Documents/Madeirart Backups/`
- O nome do arquivo de backup deve incluir data e hora: `marcenaria_backup_2026-02-21_14-30.db`
- Deve manter os últimos 10 backups, removendo os mais antigos automaticamente
- O processo de backup não deve impedir o fechamento da aplicação

### Tarefas Backend (Spring Boot)

**Service de Backup:**

- [ ] Criar `BackupService` com método `createBackup()`
- [ ] Implementar lógica para copiar arquivo SQLite usando `Files.copy()`
- [ ] Gerar nome de arquivo com timestamp formatado
- [ ] Criar diretório de backup se não existir
- [ ] Implementar rotação de backups (manter apenas últimos 10)
- [ ] Adicionar tratamento de erros e logging

**Hook de Shutdown:**

- [ ] Criar `@Component` implementando `DisposableBean` ou usar `@PreDestroy`
- [ ] Registrar `Runtime.getRuntime().addShutdownHook()` para executar backup
- [ ] Executar backup de forma assíncrona para não bloquear shutdown

### Tarefas Frontend (Electron)

**Integração com Electron:**

- [ ] Adicionar endpoint REST `POST /backup/execute` no backend
- [ ] Chamar endpoint de backup no evento `before-quit` do Electron
- [ ] Aguardar resposta do backup antes de encerrar processo backend (timeout de 5s)
- [ ] Exibir notificação de sucesso/falha do backup (opcional)

**Interface de Gerenciamento:**

- [ ] Criar tela de "Configurações" no frontend
- [ ] Adicionar seção de Backup com botão "Criar Backup Agora"
- [ ] Exibir localização da pasta de backups
- [ ] Listar backups disponíveis com datas
- [ ] Implementar botão para abrir pasta de backups no Explorer
- [ ] Adicionar opção de restaurar backup (com confirmação)

### Tarefas de Testes

- [ ] Testar criação de backup manual via interface
- [ ] Validar backup automático ao fechar aplicação
- [ ] Verificar integridade do arquivo de backup (abrir com SQLite Browser)
- [ ] Testar rotação de backups (criar mais de 10 e verificar exclusão)
- [ ] Validar funcionamento em cenários de erro (disco cheio, sem permissões)
- [ ] Testar restauração de backup em nova instalação

---

## Épico 8: Migração de SQLite para PostgreSQL no Docker

### US17: Configuração de PostgreSQL no Docker Compartilhado

**Descrição:**

Como desenvolvedor, quero migrar o sistema de SQLite local para PostgreSQL no Docker, compartilhando a mesma instância de banco de dados com outro projeto (GiborBallet) através de schemas separados, para permitir deploy online da aplicação.

### Critérios de Aceite

- Um único container PostgreSQL deve rodar com dois schemas: `giborballet` e `madeirart`
- O schema `madeirart` deve ser criado automaticamente na inicialização do container
- Todas as entidades e relacionamentos devem funcionar corretamente no PostgreSQL
- Os dados de desenvolvimento devem ser facilmente limpos/resetados durante testes
- O sistema deve iniciar sem erros com o PostgreSQL no Docker
- Migrations/esquema do banco devem ser gerenciados automaticamente (Flyway ou Hibernate)

### Tarefas de Infraestrutura (Docker)

**Atualização do docker-compose.yml:**

- [ ] Adicionar volume para script de inicialização: `./init-db.sql:/docker-entrypoint-initdb.d/init-db.sql`
- [ ] Manter porta `5432:5432` exposta para acesso local
- [ ] Verificar se variáveis de ambiente estão corretas (POSTGRES_USER=gibor, POSTGRES_PASSWORD=secret)

**Criação do Script de Inicialização:**

- [ ] Criar arquivo `init-db.sql` na raiz do projeto Madeirart (não no GiborBallet)
- [ ] Adicionar comando para criar schema: `CREATE SCHEMA IF NOT EXISTS madeirart;`
- [ ] Garantir permissões ao usuário gibor: `GRANT ALL PRIVILEGES ON SCHEMA madeirart TO gibor;`
- [ ] Configurar search_path para incluir o schema madeirart
- [ ] Adicionar grants para tabelas e sequences no schema

### Tarefas Backend (Spring Boot)

**Atualização de Dependências:**

- [ ] Adicionar dependência `org.postgresql:postgresql` no `pom.xml`
- [ ] Remover dependência `sqlite-jdbc` se existir
- [ ] Adicionar dependência `org.flywaydb:flyway-core` para gerenciar migrations (opcional mas recomendado)
- [ ] Verificar compatibilidade de versões (Spring Boot 3.x + PostgreSQL driver)

**Configuração do application.properties:**

- [ ] Atualizar `spring.datasource.url` para `jdbc:postgresql://localhost:5432/giborballet?currentSchema=madeirart`
- [ ] Configurar `spring.datasource.username=gibor`
- [ ] Configurar `spring.datasource.password=secret`
- [ ] Alterar `spring.datasource.driver-class-name` para `org.postgresql.Driver`
- [ ] Mudar `spring.jpa.database-platform` para `org.hibernate.dialect.PostgreSQLDialect`
- [ ] Adicionar `spring.jpa.properties.hibernate.default_schema=madeirart`
- [ ] Configurar `spring.jpa.hibernate.ddl-auto=update` (ou `validate` em produção)
- [ ] Adicionar configurações de connection pool (HikariCP)

**Criação de Perfis de Ambiente:**

- [ ] Criar `application-dev.properties` com configurações de desenvolvimento (DDL auto-create)
- [ ] Criar `application-prod.properties` com configurações de produção (DDL validate)
- [ ] Configurar variáveis de ambiente para dados sensíveis (senha do BD)
- [ ] Documentar perfis no README.md do projeto

**Ajustes em Entidades JPA:**

- [ ] Revisar todas as entidades (`@Entity`) e adicionar `schema = "madeirart"` na anotação `@Table`
- [ ] Exemplo: `@Table(name = "orcamento", schema = "madeirart")`
- [ ] Fazer o mesmo para todas as entidades: `Parcela`, `CustoFixo`, `CustoVariavel`, etc.
- [ ] Verificar estratégias de geração de ID (PostgreSQL usa `IDENTITY` ou `SEQUENCE`)
- [ ] Atualizar `@GeneratedValue(strategy = GenerationType.IDENTITY)` se necessário
- [ ] Remover anotações específicas de SQLite, se houver

**Migrations com Flyway (Recomendado):**

- [ ] Criar pasta `src/main/resources/db/migration/`
- [ ] Criar migration inicial `V1__criar_schema_inicial.sql` com DDL de todas as tabelas
- [ ] Incluir criação de índices e constraints (foreign keys, unique, etc.)
- [ ] Adicionar migration para dados de seed se necessário: `V2__dados_iniciais.sql`
- [ ] Configurar Flyway no `application.properties`:
  - `spring.flyway.enabled=true`
  - `spring.flyway.baseline-on-migrate=true`
  - `spring.flyway.locations=classpath:db/migration`
  - `spring.flyway.schemas=madeirart`

**Ajustes de Tipos de Dados:**

- [ ] Verificar compatibilidade de tipos entre SQLite e PostgreSQL:
  - `TEXT` → `VARCHAR` ou `TEXT`
  - `INTEGER` → `INTEGER` ou `BIGINT`
  - `REAL` → `NUMERIC` ou `DECIMAL` (para valores monetários)
  - `BOOLEAN` → `BOOLEAN` (nativo no PostgreSQL)
  - `DATETIME` → `TIMESTAMP`
- [ ] Atualizar anotações JPA se necessário (`@Column(columnDefinition = "...")`)
- [ ] Usar `BigDecimal` para valores monetários ao invés de `Double`

**Configuração de Backup (Atualização):**

- [ ] Atualizar `BackupService` para usar `pg_dump` ao invés de cópia de arquivo
- [ ] Implementar comando: `docker exec madeirart-db pg_dump -U gibor -d giborballet --schema=madeirart`
- [ ] Atualizar lógica de restore para importar via `psql`
- [ ] Testar backup e restore com PostgreSQL

### Tarefas de Teste e Validação

**Testes de Conexão:**

- [ ] Iniciar container Docker: `docker-compose up -d`
- [ ] Verificar criação do schema: `docker exec -it [container] psql -U gibor -d giborballet -c "\dn"`
- [ ] Conectar ao banco via cliente SQL (DBeaver, pgAdmin) e verificar schema `madeirart`
- [ ] Testar inicialização do backend Spring Boot

**Testes de Funcionalidades:**

- [ ] Executar aplicação e verificar criação de tabelas no schema correto
- [ ] Testar CRUD de Orçamentos (criar, listar, editar, deletar)
- [ ] Testar CRUD de Custos Fixos e Variáveis
- [ ] Testar criação e confirmação de Parcelas
- [ ] Verificar queries do Dashboard (projeções, calendário)
- [ ] Validar cálculos financeiros com valores decimais (precisão monetária)
- [ ] Testar relacionamentos JPA (joins, lazy/eager loading)

**Testes de Performance:**

- [ ] Inserir volume médio de dados (100+ orçamentos, 500+ parcelas)
- [ ] Medir tempo de resposta das principais queries
- [ ] Verificar uso de índices (explain analyze no PostgreSQL)
- [ ] Validar que não há N+1 queries

**Testes de Integração:**

- [ ] Validar que os dois schemas (giborballet e madeirart) coexistem sem conflitos
- [ ] Testar conexões simultâneas de ambos os projetos
- [ ] Verificar isolamento de dados entre schemas
- [ ] Testar backup de schema específico sem afetar o outro

### Tarefas de Documentação


### Tarefas de Migração de Dados (Se houver dados existentes)

**Se já existem dados em SQLite:**

- [ ] Criar script de export de dados do SQLite para SQL
- [ ] Ajustar script para syntax do PostgreSQL (aspas, tipos, etc.)
- [ ] Criar migration Flyway com os dados exportados
- [ ] Validar integridade referencial após importação
- [ ] Testar aplicação com dados migrados

---

## Épico 9: Autenticação e Segurança

### US18: Cadastro e Gestão de Usuários

**Descrição:**

Como administrador do sistema, quero cadastrar usuários com credenciais seguras para controlar quem tem acesso à aplicação, garantindo que apenas pessoas autorizadas possam visualizar e manipular os dados financeiros.

### Critérios de Aceite

- Cada usuário deve ter: nome, senha (criptografada) e status (ativo/inativo)
- Senhas devem ser criptografadas com BCrypt antes de serem armazenadas
- O sistema deve validar força mínima da senha (8+ caracteres, letras e números)
- Primeiro usuário criado no sistema deve automaticamente ter privilégios de admin
- Admin deve poder ativar/desativar usuários sem excluí-los do banco

### Tarefas Backend (Spring Boot)

**Entidades e Repositórios:**

- [ ] Criar entidade `Usuario` com campos: id, nomeCompleto, username(unique), senha, ativo (boolean), dataCriacao, dataUltimoAcesso
- [ ] Adicionar enum `PerfilAcesso` (ADMIN, USUARIO) para controle futuro de permissões
- [ ] Criar campo `perfil` na entidade Usuario
- [ ] Criar `UsuarioRepository` extends `JpaRepository<Usuario, Long>`
- [ ] Adicionar método customizado `Optional<Usuario> findByUsername(String username)`
- [ ] Adicionar método `boolean existsByUsername(String username)`

**DTOs:**

- [ ] Criar `UsuarioCadastroDTO` com campos: nomeCompleto, username, senha, confirmacaoSenha
- [ ] Criar `UsuarioResponseDTO` com campos: id, nomeCompleto, username, ativo, perfil, dataCriacao (sem senha)
- [ ] Criar validações com Bean Validation (@NotBlank, @Size, etc.)
- [ ] Implementar validação customizada para força de senha

**Services:**

- [ ] Criar `UsuarioService` com injeção de `PasswordEncoder` (BCrypt)
- [ ] Implementar método `cadastrarUsuario(UsuarioCadastroDTO dto)` que:
  - Verifica se senha e confirmação são iguais
  - Valida força da senha
  - Criptografa a senha com BCrypt
  - Se for o primeiro usuário, define perfil como ADMIN
  - Salva o usuário no banco
- [ ] Implementar método `listarUsuarios()` retornando lista de `UsuarioResponseDTO`
- [ ] Implementar método `ativarDesativarUsuario(Long id, boolean ativo)`

**Endpoints:**

- [ ] Criar `UsuarioController` com mapeamento `/api/usuarios`
- [ ] Endpoint `POST /api/usuarios/cadastro` para criar novo usuário
- [ ] Endpoint `GET /api/usuarios` para listar todos usuários (apenas admin)
- [ ] Endpoint `PATCH /api/usuarios/{id}/status` para ativar/desativar (apenas admin)
- [ ] Endpoint `GET /api/usuarios/me` para retornar dados do usuário logado

**Configuração de Segurança:**

- [ ] Adicionar dependência `spring-boot-starter-security` no pom.xml
- [ ] Criar bean `PasswordEncoder` usando `BCryptPasswordEncoder`
- [ ] Configurar endpoints públicos (login, primeiro cadastro) e protegidos

### Tarefas Frontend (React)

**Componentes e Views:**

- [ ] Criar view `views/Auth/CadastroUsuario.jsx` com formulário de cadastro
- [ ] Implementar campos: Nome, Senha, Confirmar Senha
- [ ] Adicionar validação de força de senha com indicador visual (barra de progresso)
- [ ] Mostrar mensagens de erro específicas
- [ ] Criar view `views/Usuarios/UsuariosList.jsx` (apenas para admin)
- [ ] Implementar tabela com colunas: Nome, Perfil, Status, Ações
- [ ] Adicionar botão de ativar/desativar com confirmação

**Services:**

- [ ] Criar `authService.js` com métodos:
  - `cadastrarUsuario(dados)`
  - `listarUsuarios()`
  - `alterarStatusUsuario(id, ativo)`

**Validações:**

- [ ] Implementar hook customizado `usePasswordStrength` para validação de senha
- [ ] Adicionar feedback visual de força (fraca/média/forte)

---

### US19: Sistema de Login e Autenticação JWT

**Descrição:**

Como usuário cadastrado, quero fazer login com meu username e senha para acessar o sistema de forma segura, recebendo um token de autenticação que identifique minhas ações.

### Critérios de Aceite

- O usuário deve poder fazer login com username e senha
- Após login bem-sucedido, o sistema deve retornar um token JWT válido por 12 horas
- O token deve conter: id do usuário, username, perfil de acesso e data de expiração
- Credenciais inválidas devem retornar erro 401 com mensagem clara
- Usuários inativos não devem conseguir fazer login
- O sistema deve atualizar o campo `dataUltimoAcesso` a cada login bem-sucedido

### Tarefas Backend (Spring Boot)

**Configuração de JWT:**

- [ ] Adicionar <dependency> <groupId>com.auth0</groupId> <artifactId>java-jwt</artifactId> <version>4.4.0</version> </dependency> no pom.xml
- [ ] Criar classe `JwtUtil` com métodos:
  - `generateToken(Usuario usuario)` - gera JWT com claims customizados
  - `extractUsername(String token)` - extrai username do token
  - `validateToken(String token, UserDetails userDetails)` - valida token
  - `extractExpiration(String token)` - verifica se token expirou
- [ ] Configurar chave secreta do JWT no `application.properties` (usar variável de ambiente em produção)
- [ ] Definir tempo de expiração de 8 horas

**DTOs:**

- [ ] Criar `LoginRequestDTO` com campos: username, senha
- [ ] Criar `LoginResponseDTO` com campos: token, tipo ("Bearer"), usuario (UsuarioResponseDTO)
- [ ] Adicionar validações com Bean Validation

**Services:**

- [ ] Criar `AuthService` com método `autenticar(LoginRequestDTO dto)` que:
  - Busca usuário por username
  - Verifica se usuário existe e está ativo
  - Valida senha usando `PasswordEncoder.matches()`
  - Atualiza campo `dataUltimoAcesso`
  - Gera token JWT
  - Retorna LoginResponseDTO
- [ ] Implementar tratamento de exceções específicas (UsernameNaoEncontradoException, UsuarioInativoException, SenhaInvalidaException)

**Filtros de Segurança:**

- [ ] Criar `JwtAuthenticationFilter` extends `OncePerRequestFilter` que:
  - Extrai token do header Authorization
  - Valida token usando JwtUtil
  - Carrega UserDetails do usuário
  - Define autenticação no SecurityContextHolder
- [ ] Criar `CustomUserDetailsService` implements `UserDetailsService` que carrega usuário por username

**Endpoints:**

- [ ] Criar `AuthController` com mapeamento `/api/auth`
- [ ] Endpoint `POST /api/auth/login` (público) para autenticação
- [ ] Endpoint `POST /api/auth/logout` para invalidar token (opcional)
- [ ] Endpoint `POST /api/auth/refresh` para renovar token próximo da expiração (opcional)

**Configuração de Segurança:**

- [ ] Criar classe `SecurityConfig` com `@EnableWebSecurity`
- [ ] Configurar chain de filtros adicionando `JwtAuthenticationFilter`
- [ ] Definir endpoints públicos: `/api/auth/login`, `/api/usuarios/cadastro` (apenas se for primeiro)
- [ ] Proteger todos os demais endpoints exigindo autenticação
- [ ] Configurar CORS para permitir requisições do frontend
- [ ] Desabilitar CSRF (usando JWT stateless)

### Tarefas Frontend (React)

**Componentes de Login:**

- [ ] Criar view `views/Auth/Login.jsx` com formulário de login
- [ ] Implementar campos: username e Senha
- [ ] Adicionar checkbox "Lembrar-me" (opcional)
- [ ] Mostrar mensagens de erro específicas retornadas pela API
- [ ] Adicionar loading state durante autenticação
- [ ] Redirecionar para Dashboard após login bem-sucedido

**Gerenciamento de Token:**

- [ ] Criar `authService.js` com métodos:
  - `login(username, senha)` - chama API e armazena token
  - `logout()` - remove token do storage
  - `getToken()` - recupera token armazenado
  - `isAuthenticated()` - verifica se há token válido
  - `getUsuarioLogado()` - decodifica token e retorna dados do usuário
- [ ] Armazenar token no `localStorage` ou `sessionStorage`
- [ ] Adicionar token automaticamente em todas as requisições via interceptor do Axios

**Context e Estado Global:**

- [ ] Criar `AuthContext` com Provider para gerenciar estado de autenticação
- [ ] Implementar hook `useAuth()` para acessar contexto
- [ ] Armazenar: `usuario`, `token`, `isAuthenticated`, `login()`, `logout()`
- [ ] Verificar token ao carregar aplicação e restaurar sessão se válido

**Interceptor Axios:**

- [ ] Criar interceptor de request para adicionar `Authorization: Bearer {token}` em todos os requests
- [ ] Criar interceptor de response para capturar erro 401 (não autorizado)
- [ ] Redirecionar para login ao receber 401 e limpar token expirado

**Proteção de Rotas:**

- [ ] Criar componente `ProtectedRoute` ou `PrivateRoute`
- [ ] Verificar autenticação antes de renderizar rota protegida
- [ ] Redirecionar para `/login` se não autenticado
- [ ] Aplicar proteção em todas as rotas exceto login e cadastro

---

### US20: Controle de Sessão e Renovação de Token

**Descrição:**

Como usuário logado, quero que o sistema mantenha minha sessão ativa enquanto eu estiver usando a aplicação e me avise quando minha sessão estiver próxima de expirar, permitindo renovação sem perder meu trabalho.

### Critérios de Aceite

- O sistema deve detectar quando o token está próximo de expirar (últimos 30 minutos)
- Um modal deve aparecer alertando o usuário com opções: "Renovar Sessão" ou "Fazer Logout"
- Ao escolher "Renovar Sessão", um novo token deve ser gerado sem perder contexto
- Após 8 horas de inatividade total, o usuário deve ser deslogado automaticamente
- Ao detectar token expirado em qualquer requisição, redirecionar para login com mensagem apropriada

### Tarefas Backend (Spring Boot)

**Endpoint de Refresh:**

- [ ] Criar endpoint `POST /api/auth/refresh` que:
  - Valida token atual (mesmo que próximo da expiração)
  - Extrai dados do usuário do token
  - Gera novo token com validade renovada
  - Retorna novo token no mesmo formato do login
- [ ] Adicionar validação para garantir que token não esteja completamente expirado

**Configuração:**

- [ ] Definir janela de refresh (ex: aceitar tokens até 1 hora após expiração para renovação)
- [ ] Adicionar log de renovações de token para auditoria

### Tarefas Frontend (React)

**Detecção de Expiração:**

- [ ] Criar hook `useTokenExpiration()` que:
  - Decodifica token e extrai data de expiração
  - Calcula tempo restante
  - Retorna `isExpiringSoon` (< 30 min) e `isExpired`
- [ ] Implementar timer que verifica expiração a cada 1 minuto

**Modal de Renovação:**

- [ ] Criar componente `SessionExpirationModal.jsx`
- [ ] Exibir countdown mostrando tempo restante
- [ ] Implementar botão "Renovar Sessão" que chama `/api/auth/refresh`
- [ ] Implementar botão "Sair" que faz logout
- [ ] Ao renovar com sucesso, atualizar token no storage e context
- [ ] Fechar modal automaticamente após renovação

**Integração:**

- [ ] Adicionar modal no componente raiz (App.jsx ou MainLayout.jsx)
- [ ] Conectar com `useTokenExpiration` para controlar exibição
- [ ] Atualizar interceptor Axios para usar refresh token em caso de 401

---
