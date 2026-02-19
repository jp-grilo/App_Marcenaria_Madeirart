# Instruções do Frontend - React (Madeirart)

## Estrutura por Views (Feature-Based)

```
frontend/src/
├── assets/           # Imagens, ícones, CSS global
├── components/       # Componentes globais reutilizáveis
├── contexts/         # React Contexts (separados dos Providers)
├── hooks/            # Custom hooks globais
├── services/         # API (axios instance + serviços por módulo)
├── utils/            # formatters.js, validators.js, constants.js
├── views/            # Páginas/telas
│   ├── {View}/
│   │   ├── index.js         # Exportações centralizadas (opcional)
│   │   ├── {View}.jsx       # Página principal
│   │   ├── {View}Form.jsx   # Outras páginas da view
│   │   ├── components/      # Componentes EXCLUSIVOS desta view
│   │   │   └── *.jsx
│   │   ├── dialogs/         # Janelas de diálogo específicas (opcional)
│   │   │   └── *.jsx
│   │   └── hooks/           # Custom hooks específicos (opcional)
│   │       └── *.js
│   │
│   └── Orcamentos/          # Exemplo real
│       ├── index.js                    # export { OrcamentosList, OrcamentoForm, ... }
│       ├── OrcamentosList.jsx          # Página de listagem
│       ├── OrcamentoForm.jsx           # Página de formulário
│       ├── OrcamentoDetalhes.jsx       # Página de detalhes
│       ├── components/
│       │   ├── ParcelasList.jsx
│       │   ├── ParcelasAutomaticas.jsx
│       │   └── ParcelasManuais.jsx
│       └── dialogs/
│           └── IniciarProducaoDialog.jsx
├── App.jsx
└── main.jsx
```

---

## Organização Interna de Views

Cada view pode conter várias páginas relacionadas e deve organizar seus arquivos em subpastas:

### Estrutura Recomendada

```
views/{View}/
├── index.js              # Exportações centralizadas (facilita imports)
├── {View}List.jsx        # Página de listagem
├── {View}Form.jsx        # Página de formulário
├── {View}Detalhes.jsx    # Página de detalhes
├── components/           # Componentes específicos desta view
│   └── *.jsx
├── dialogs/              # Modais/Dialogs específicos (opcional)
│   └── *Dialog.jsx
└── hooks/                # Custom hooks específicos (opcional)
    └── use*.js
```

### Benefícios do index.js

```javascript
// views/Orcamentos/index.js
export { default as OrcamentosList } from "./OrcamentosList";
export { default as OrcamentoForm } from "./OrcamentoForm";
export { default as OrcamentoDetalhes } from "./OrcamentoDetalhes";

// Em App.jsx - importação simplificada
import {
  OrcamentosList,
  OrcamentoForm,
  OrcamentoDetalhes,
} from "./views/Orcamentos";
```

### Subpastas Opcionais

- **`/components`** - Componentes usados apenas nesta view
- **`/dialogs`** - Janelas de diálogo/modais específicos
- **`/hooks`** - Custom hooks específicos da view
- **`/utils`** - Utilitários específicos (se houver lógica complexa isolada)

---

## Regras Obrigatórias

### 1. **NUNCA crie componentes órfãos na pasta raiz**

- ERRADO: `src/CalendarioFinanceiro.jsx`, `src/views/Orcamentos/ParcelaCard.jsx`
- CORRETO:
  - Componentes globais: `src/components/CalendarioFinanceiro.jsx`
  - Componentes específicos: `src/views/Orcamentos/components/ParcelaCard.jsx`
  - Dialogs específicos: `src/views/Orcamentos/dialogs/IniciarProducaoDialog.jsx`
- **Exceção:** Páginas principais da view ficam na raiz da view (ex: `OrcamentosList.jsx`)
- **Regra:** Se múltiplos arquivos ficam na raiz da view, organize em subpastas

### 2. **Use Material UI (MUI) como biblioteca padrão**

- SEMPRE use componentes MUI quando disponíveis
- Priorize: `Button`, `TextField`, `Card`, `Modal`, `Table`, `CircularProgress`, `LinearProgress`, etc.
- Customize via `sx` prop ou `styled()` apenas quando necessário
- Importe assim: `import { Button, TextField } from '@mui/material';`

### 3. **Separação Clara: Global vs. Específico**

- **Global** (`src/components/`): Componentes usados em 3+ views diferentes
- **Específico** (`src/views/{View}/components/` ou `/dialogs/`): Componentes/dialogs usados só naquela view
- Se um componente específico for usado em outra view, mova para `src/components/`
- **Exemplos:**
  - `StatusRecebimentoCard` → usado em Dashboard + Orcamentos = `src/components/`
  - `ParcelasList` → usado só em Orcamentos = `src/views/Orcamentos/components/`
  - `IniciarProducaoDialog` → usado só em Orcamentos = `src/views/Orcamentos/dialogs/`

### 4. **Custom Hooks para Lógica Reutilizável**

- Extraia lógica complexa/repetitiva para custom hooks
- Nomeie com prefixo `use` (ex: `useOrcamento`, `usePagination`, `useFilter`)
- Localização:
  - Global: `src/hooks/`
  - Específico: `src/views/{View}/hooks/`

### 5. **Fast Refresh: Separe Componentes de Hooks/Utilitários**

- **REGRA:** Arquivos .jsx devem exportar **APENAS componentes React** para Fast Refresh funcionar
- **REGRA:** Contexts devem estar em arquivos .js separados (não junto com Providers)
- ERRADO: `useSnackbar.jsx` exportando `SnackbarProvider` + `SnackbarContext` + `useSnackbar`
- CORRETO (3 arquivos separados):
  ```
  src/contexts/SnackbarContext.js    → export const SnackbarContext = createContext()
  src/components/SnackbarProvider.jsx → export default SnackbarProvider (componente)
  src/hooks/useSnackbar.jsx           → export const useSnackbar (hook)
  ```
- **Mensagem de erro:** "Fast refresh only works when a file only exports components"
- **Benefício:** Hot Module Replacement (HMR) funciona corretamente durante desenvolvimento

### 6. **Services Isolados para API**

- TODA comunicação com backend deve estar em `src/services/`
- Um service por módulo (ex: `orcamentoService.js`, `financeiroService.js`)
- Componentes NUNCA devem fazer `axios.get()` diretamente

---

## Padrões Essenciais

### Services (API)

- Um arquivo por módulo: `orcamentoService.js`, `financeiroService.js`
- Exportar objeto com métodos async: `listar()`, `buscarPorId()`, `criar()`, `atualizar()`
- Sempre importar de `api.js` (axios instance configurada)
- Retornar apenas `data` da resposta

### Custom Hooks

- Prefixo `use` + nome descritivo
- Retornar objeto: `{ data, loading, error }`
- useEffect com dependências corretas (usar `JSON.stringify()` para objetos)
- Encapsular chamadas a services

### Componentes

- Importar MUI components no topo
- Usar custom hooks para dados da API
- Aplicar formatters (moeda, data) via utils
- Loading/Error states sempre visíveis

---

## Utils Obrigatórios

### formatters.js

- `formatCurrency(value)` → "R$ X.XXX,XX" (Intl.NumberFormat pt-BR)
- `formatDate(dateString)` → "DD/MM/YYYY" (Intl.DateTimeFormat pt-BR)
- `formatCPF(cpf)` → "XXX.XXX.XXX-XX"
- `formatCNPJ(cnpj)` → "XX.XXX.XXX/XXXX-XX"

### validators.js

- `isValidEmail(email)` → boolean
- `isValidCPF(cpf)` → boolean (com algoritmo completo)
- `isValidCNPJ(cnpj)` → boolean

### constants.js

- `STATUS_ORCAMENTO` → objeto com AGUARDANDO, INICIADA, FINALIZADA, CANCELADA
- `STATUS_PARCELA` → objeto com PENDENTE, PAGO
- `FORMAS_PAGAMENTO` → array ['PIX', 'Dinheiro', 'Débito', 'Crédito', 'Boleto']

---

## Estilo e UI

### Prioridade de Uso

1. **MUI Components** → Button, Card, Table, Modal, TextField, Chip, CircularProgress, LinearProgress
2. **Tailwind CSS** → Espaçamento e ajustes (`className="mt-4 p-2 flex gap-2"`)
3. **MUI `sx` prop** → Customizações específicas apenas quando necessário

### Tema

- Configurar ThemeProvider no App.jsx
- Cores primárias: tons de marrom (#8B4513, #D2691E)
- Usar palette.success, palette.error para status

---

## Anti-Padrões (NÃO FAÇA)

1. Fazer chamadas API diretamente no componente (`axios.get()`)
2. Criar componentes genéricos em pastas de views específicas
3. Deixar vários arquivos na raiz de uma view sem organizar em subpastas (`/components`, `/dialogs`)
4. Lógica de negócio complexa no componente (extraia para hooks/utils)
5. Hardcode de valores que podem ser constantes
6. Usar `var` (sempre `const` ou `let`)
7. Props drilling excessivo (use Context API quando necessário)
8. Misturar formatação com lógica (use utils)
9. Estilos inline complexos (use MUI `sx` ou classes CSS)
10. **Exportar componentes + hooks/funções no mesmo arquivo .jsx** (quebra Fast Refresh)

---

## Nomenclatura

- **Arquivos**: PascalCase para componentes (.jsx), camelCase para utils/services (.js)
- **Componentes**: PascalCase (`OrcamentosList`, `CardSaldo`)
- **Páginas**: Sufixo `List/Form/Detalhes` (`OrcamentosList.jsx`, `OrcamentoForm.jsx`)
- **Dialogs**: Sufixo `Dialog` (`IniciarProducaoDialog.jsx`, `ConfirmarExclusaoDialog.jsx`)
- **Variáveis**: camelCase (`valorTotal`, `dataPagamento`)
- **Constantes**: UPPER_SNAKE_CASE (`STATUS_ORCAMENTO`, `API_BASE_URL`)
- **Funções**: camelCase (`formatCurrency`, `calcularTotal`)
- **Event handlers**: prefixo `handle` (`handleSubmit`, `handleClick`)
- **Props booleanas**: prefixo `is/has` (`isLoading`, `hasError`)
- **Props callbacks**: prefixo `on` (`onClick`, `onSubmit`, `onClose`)

---

## Stack

React + Vite + Tailwind CSS + Material UI (MUI)
