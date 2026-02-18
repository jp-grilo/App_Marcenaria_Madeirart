# Instruções do Frontend - React (Madeirart)

## 🏗️ Estrutura por Views (Feature-Based)

```
frontend/src/
├── assets/           # Imagens, ícones, CSS global
├── components/       # Componentes globais reutilizáveis
├── hooks/            # Custom hooks globais
├── services/         # API (axios instance + serviços por módulo)
├── utils/            # formatters.js, validators.js, constants.js
├── views/            # Páginas/telas
│   ├── {View}/
│   │   ├── components/  # Componentes EXCLUSIVOS desta view
│   │   └── {View}.jsx   # Componente principal
├── App.jsx
└── main.jsx
```

---

## 📋 Regras Obrigatórias

### 1. **NUNCA crie componentes órfãos na pasta raiz**
- ❌ ERRADO: `src/CalendarioFinanceiro.jsx`
- ✅ CORRETO: `src/views/Dashboard/components/CalendarioFinanceiro.jsx`
- **Exceção:** Componentes verdadeiramente globais vão em `src/components/`

### 2. **Use Material UI (MUI) como biblioteca padrão**
- SEMPRE use componentes MUI quando disponíveis
- Priorize: `Button`, `TextField`, `Card`, `Modal`, `Table`, `CircularProgress`, `LinearProgress`, etc.
- Customize via `sx` prop ou `styled()` apenas quando necessário
- Importe assim: `import { Button, TextField } from '@mui/material';`

### 3. **Separação Clara: Global vs. Específico**
- **Global** (`src/components/`): Componentes usados em 3+ telas diferentes
- **Específico** (`src/views/{View}/components/`): Componentes usados só naquela view
- Se um componente específico for usado em outra view, mova para `src/components/`

### 4. **Custom Hooks para Lógica Reutilizável**
- Extraia lógica complexa/repetitiva para custom hooks
- Nomeie com prefixo `use` (ex: `useOrcamento`, `usePagination`, `useFilter`)
- Localização:
  - Global: `src/hooks/`
  - Específico: `src/views/{View}/hooks/`

### 5. **Services Isolados para API**
- TODA comunicação com backend deve estar em `src/services/`
- Um service por módulo (ex: `orcamentoService.js`, `financeiroService.js`)
- Componentes NUNCA devem fazer `axios.get()` diretamente

---

## 🛠️ Padrões Essenciais

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

## 📦 Utils Obrigatórios

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

## 🎨 Estilo e UI

### Prioridade de Uso
1. **MUI Components** → Button, Card, Table, Modal, TextField, Chip, CircularProgress, LinearProgress
2. **Tailwind CSS** → Espaçamento e ajustes (`className="mt-4 p-2 flex gap-2"`)
3. **MUI `sx` prop** → Customizações específicas apenas quando necessário

### Tema
- Configurar ThemeProvider no App.jsx
- Cores primárias: tons de marrom (#8B4513, #D2691E)
- Usar palette.success, palette.error para status

---

## 🚫 Anti-Padrões (NÃO FAÇA)

1. ❌ Fazer chamadas API diretamente no componente (`axios.get()`)
2. ❌ Criar componentes genéricos em pastas de views específicas
3. ❌ Lógica de negócio complexa no componente (extraia para hooks/utils)
4. ❌ Hardcode de valores que podem ser constantes
5. ❌ Usar `var` (sempre `const` ou `let`)
6. ❌ Props drilling excessivo (use Context API quando necessário)
7. ❌ Misturar formatação com lógica (use utils)
8. ❌ Estilos inline complexos (use MUI `sx` ou classes CSS)

---

## 📝 Nomenclatura

- **Arquivos**: PascalCase para componentes (.jsx), camelCase para utils/services (.js)
- **Componentes**: PascalCase (`OrcamentosList`, `CardSaldo`)
- **Variáveis**: camelCase (`valorTotal`, `dataPagamento`)
- **Constantes**: UPPER_SNAKE_CASE (`STATUS_ORCAMENTO`, `API_BASE_URL`)
- **Funções**: camelCase (`formatCurrency`, `calcularTotal`)
- **Event handlers**: prefixo `handle` (`handleSubmit`, `handleClick`)
- **Props booleanas**: prefixo `is/has` (`isLoading`, `hasError`)
- **Props callbacks**: prefixo `on` (`onClick`, `onSubmit`, `onClose`)

---

## 📚 Stack
React + Vite + Tailwind CSS + Material UI (MUI)
