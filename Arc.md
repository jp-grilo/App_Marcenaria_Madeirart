Este documento detalha a arquitetura do sistema de gestão financeira para marcenaria, projetado para ser uma aplicação desktop de alta performance, rodando localmente no Windows.

---

## 1. Visão Geral e Estratégia de Runtime
O sistema é uma aplicação Desktop Nativa para Windows, otimizada para execução local:

Backend: Spring Boot 3.x compilado para binário nativo (.exe) via GraalVM Native Image. Isso elimina a necessidade de JRE instalado e garante boot instantâneo com baixo consumo de memória.

Frontend: React encapsulado em uma WebView nativa (WebView2), permitindo uma interface moderna com desempenho de aplicação local.

---

## 2. Stack Tecnológica

A escolha das tecnologias visa equilibrar a produtividade do desenvolvedor (aproveitando seu conhecimento em Spring Boot e React) com a performance exigida de um app nativo.

| Camada                | Tecnologia                  | Motivo da Escolha                                                               |
| --------------------- | --------------------------- | ------------------------------------------------------------------------------- |
| **Frontend**          | React + Vite + Tailwind CSS + MUI (Material UI) | UI moderna, reativa, desenvolvimento rápido e componentes visuais robustos e acessíveis. |
| **Backend**           | Spring Boot 3.x (Java)      | Sólida experiência do dev e ecossistema maduro.                                 |
| **Runtime**           | **GraalVM Native Image**    | Transforma o JAR em um `.exe` nativo (baixo consumo de RAM e boot instantâneo). |
| **Banco de Dados**    | SQLite                      | Serverless, arquivo único e fácil portabilidade.                                |
| **Interface Desktop** | J-WebView ou Electron-light | Para encapsular o React em uma janela nativa sem depender do browser.           |

---

## 3. Modelo de Dados (Entidades Principais)


O coração do sistema é o cruzamento entre o que foi **planejado** no orçamento e o que **realmente aconteceu** no banco. O modelo foi desenhado para suportar as principais necessidades levantadas nas User Stories, como:
- Controle de status de recebimento manual (parcela PENDENTE/PAGO)
- Indicadores visuais de progresso de pagamento
- Conciliação bancária semi-automática (matching de transações)
- Filtros e projeções de fluxo de caixa

### Entidades Core:

- **Orcamento:** Cabeçalho do projeto (Cliente, Valor Total, Status).
- **Parcela:** Desdobramento do orçamento. Contém `valorPrevisto` e `dataVencimento`.
- **TransacaoBancaria:** Dados brutos vindos do CSV/OFX (Data, Descrição, Valor, Tipo).
- **Conciliacao:** Tabela de ligação que associa uma `TransacaoBancaria` a uma `Parcela`.

---

## 4. Fluxo de Funcionamento (Conciliação)


O sistema opera em um ciclo de três etapas para garantir que o caixa reflita a realidade:
1. **Planejamento:** Criação do orçamento e geração das parcelas previstas.
2. **Importação:** Upload do extrato bancário (CSV/OFX) e parsing automático.
3. **Matching:** Sugestão de conciliação entre transações e parcelas, com possibilidade de ajuste manual pelo usuário.

**Questões previstas e desafios mapeados nas USs:**
- O usuário pode precisar confirmar manualmente o recebimento de parcelas, mesmo após conciliação automática.
- Diferenças de datas/valores entre o planejado e o realizado podem exigir tolerância configurável no matching.
- O sistema deve permitir visualizar claramente o progresso de recebimento e o que ainda está pendente.
- Projeções futuras e filtros de status são essenciais para o controle financeiro.

---

## 5. Padrões de Projeto e Princípios


Para garantir a qualidade do código e facilitar manutenção e evolução:
- **Service Layer:** Lógica de parsing, cálculo e conciliação isolada em serviços.
- **Strategy Pattern:** Parsing de extratos bancários flexível para múltiplos formatos.
- **Diferenciação de Contexto:** Filtro para separar transações pessoais e profissionais.

---

## 6. Persistência e Segurança Local

- **Database:** O arquivo `marcenaria.db` será armazenado na pasta `%APPDATA%` do usuário.
- **Backup:** Implementação de uma rotina simples de cópia do arquivo SQLite para uma pasta de backup ou nuvem (Google Drive/Dropbox) sempre que o app fechar.

---

## Apêndice: Estrutura de Pastas Refinada (Domain-Driven)

### 1. Backend (Spring Boot - Organização por Entidade)

No backend, adotamos uma estrutura onde cada domínio possui seu próprio subpacote. Isso isola a lógica de Orçamentos da lógica de Conciliação Bancária.

```text
backend/src/main/java/com/marcenaria/
├── core/                        # Configurações globais e Exceptions
│   ├── config/                  # SQLite, GraalVM, Security
│   └── exception/               # GlobalExceptionHandler e Custom Exceptions
├── modules/                     # Módulos de negócio divididos por entidade
│   ├── orcamento/
│   │   ├── controller/          # OrcamentoController
│   │   ├── service/             # OrcamentoService, GeradorParcelaService
│   │   ├── dto/                 # OrcamentoRequestDTO, OrcamentoResponseDTO
│   │   ├── repository/          # OrcamentoRepository
│   │   └── model/               # Orcamento.java (Entity)
│   ├── financeiro/
│   │   ├── controller/          # LancamentoController, ConciliacaoController
│   │   ├── service/             # ConciliacaoService, ExtratoParserService
│   │   ├── dto/                 # TransacaoDTO, FluxoCaixaDTO
│   │   ├── repository/          # LancamentoRepository
│   │   └── model/               # Lancamento.java, Transacao.java
│   └── cliente/
│       ├── controller/
│       ├── service/
│       └── model/

```

---

### 2. Frontend (React - Organização por Views)


No frontend, mantemos a separação entre o que é global e o que é específico de cada tela, evitando que componentes "órfãos" poluam a pasta raiz. O uso do MUI (Material UI) será padrão para garantir consistência visual, acessibilidade e agilidade no desenvolvimento dos componentes de interface, especialmente para widgets de status, tabelas, modais e barras de progresso.

```text
frontend/src/
├── assets/                      # Imagens, ícones e CSS global
├── components/                  # UI Kit Reutilizável (Botão, Input, Modal, Sidebar)
├── hooks/                       # Custom hooks (ex: useAuth, useFinance)
├── services/                    # Integração com API (Axios instances)
├── utils/                       # Formatadores de moeda, datas, etc.
├── views/                       # Páginas da aplicação
│   ├── Dashboard/
│   │   ├── components/          # Widgets exclusivos do Dashboard (Cards de Saldo)
│   │   └── Dashboard.jsx
│   ├── Orcamentos/
│   │   ├── components/          # FormularioOrcamento, TabelaMateriais
│   │   └── OrcamentosList.jsx
│   ├── Conciliacao/
│   │   ├── components/          # AreaUploadExtrato, ListaMatchesSugeridos
│   │   └── ConciliacaoPage.jsx
│   └── FluxoCaixa/
│       ├── components/          # GraficoMensal, FiltroPeriodo
│       └── FluxoCaixaView.jsx
├── App.jsx
└── main.jsx

```

---

Com certeza, João Paulo. Agora o fluxo está bem robusto: temos a separação clara entre o **documento técnico** (orçamento), o **contrato financeiro** (plano de pagamento) e a **realidade do caixa** (confirmação manual).

Aqui estão as User Stories (USs) atualizadas e detalhadas para o seu sistema:

---
