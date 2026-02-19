# 🧪 Testes do Backend

## Cobertura de Testes

### Testes Criados

#### Módulo Orçamento

| Tipo            | Arquivo                   | Qtd Testes | Descrição         |
| --------------- | ------------------------- | ---------- | ----------------- |
| **Unit**        | `OrcamentoServiceTest`    | 17         | Service com mocks |
| **Integration** | `OrcamentoControllerTest` | 16         | Endpoints REST    |
| **Unit**        | `ParcelaServiceTest`      | 10         | Service com mocks |
| **Integration** | `ParcelaControllerTest`   | 9          | Endpoints REST    |
| **Subtotal**    | **4 arquivos**            | **52**     |                   |

#### Módulo Custos

| Tipo            | Arquivo                       | Qtd Testes | Descrição         |
| --------------- | ----------------------------- | ---------- | ----------------- |
| **Unit**        | `CustoVariavelServiceTest`    | 13         | Service com mocks |
| **Integration** | `CustoVariavelControllerTest` | 10         | Endpoints REST    |
| **Unit**        | `CustoFixoServiceTest`        | 18         | Service com mocks |
| **Integration** | `CustoFixoControllerTest`     | 17         | Endpoints REST    |
| **Subtotal**    | **4 arquivos**                | **58**     |                   |

#### Total Geral

| **TOTAL** | **8 arquivos** | **110 testes** |
| --------- | -------------- | -------------- |

## Como Executar

### Todos os testes

```bash
mvnw test
```

### Testes de um módulo específico

```bash
# Orçamento
mvnw test -Dtest="OrcamentoServiceTest"

# Custos
mvnw test -Dtest="CustoFixoServiceTest"
mvnw test -Dtest="CustoVariavelServiceTest"
```

### Modo watch (reexecutar ao salvar)

```bash
mvnw test -Dsurefire.failIfNoTests=false
```

---

## Estrutura dos Testes

### **Testes Unitários do Service** (`OrcamentoServiceTest`)

**Stack:** JUnit 5 + Mockito + AssertJ  
**Total:** 17 testes

**O que testa:**

- Criação de orçamento
- Busca por ID (sucesso)
- Busca por ID (erro - orçamento inexistente)
- Listagem por status
- Atualização (sucesso)
- Atualização (erro - orçamento inexistente)
- Deleção
- Salvamento de auditoria ao atualizar
- Busca de histórico de auditoria
- Erro ao buscar histórico de orçamento inexistente
- Histórico vazio para orçamento sem alterações
- **Iniciar produção com sucesso**
- **Iniciar produção com pagamento integral (sem parcelas)**
- **Erro ao iniciar produção de orçamento inexistente**
- **Erro ao iniciar produção com status incorreto**
- **Erro quando soma das parcelas não corresponde ao valor total**

**Exemplo:**

```java
@Test
@DisplayName("Deve criar orçamento com sucesso")
void deveCriarOrcamento() {
    when(orcamentoRepository.save(any(Orcamento.class))).thenReturn(orcamento);

    OrcamentoResponseDTO response = orcamentoService.criarOrcamento(requestDTO);

    assertThat(response).isNotNull();
    assertThat(response.id()).isEqualTo(1L);
    verify(orcamentoRepository).save(any(Orcamento.class));
}
```

---

### **Testes de Integração do Controller** (`OrcamentoControllerTest`)

**Stack:** Spring MockMvc + @WebMvcTest  
**Total:** 16 testes

**O que testa:**

- **POST `/api/orcamentos` - Criação com sucesso (201)**
- **POST `/api/orcamentos` - Validação de dados inválidos (400)**
- **GET `/api/orcamentos/{id}` - Busca com sucesso (200)**
- **GET `/api/orcamentos/{id}` - Orçamento não encontrado (404)**
- **GET `/api/orcamentos` - Listagem completa**
- **GET `/api/orcamentos?status=X` - Filtro por status**
- **PUT `/api/orcamentos/{id}` - Atualização**
- **DELETE `/api/orcamentos/{id}` - Deleção (204)**
- **GET `/api/orcamentos/{id}/historico` - Busca histórico de auditoria**
- **GET `/api/orcamentos/{id}/historico` - Erro 404 para orçamento inexistente**
- **GET `/api/orcamentos/{id}/historico` - Lista vazia quando não há histórico**
- **PATCH `/api/orcamentos/{id}/iniciar` - Deve iniciar produção**
- **PATCH `/api/orcamentos/{id}/iniciar` - Deve iniciar produção com pagamento integral**
- **PATCH `/api/orcamentos/{id}/iniciar` - Erro 404 para orçamento inexistente**
- **PATCH `/api/orcamentos/{id}/iniciar` - Erro 400 para status inválido**
- **PATCH `/api/orcamentos/{id}/iniciar` - Erro 400 quando soma não corresponde ao total**

**Exemplo:**

```java
@Test
@DisplayName("POST /api/orcamentos - Deve criar orçamento")
void deveCriarOrcamento() throws Exception {
    when(orcamentoService.criarOrcamento(any())).thenReturn(responseDTO);

    mockMvc.perform(post("/api/orcamentos")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(requestDTO)))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.id").value(1))
        .andExpect(jsonPath("$.cliente").value("João Silva"));
}
```

---

### **Testes de Auditoria - US02** (Novos)

**Adicionados:** 8 testes (5 Service + 3 Controller)

**Cobertura:**
-Salvamento automático de snapshot ao atualizar orçamento
-Busca de histórico ordenado por data (mais recente primeiro)
-Validação de orçamento existente antes de buscar histórico
-Tratamento de orçamento sem histórico (lista vazia)
-Endpoint REST `/api/orcamentos/{id}/historico`
-Tratamento de erros (404 para orçamento inexistente)

---

### **Testes de Iniciação de Produção - US03** (Novos)

**Adicionados:** 10 testes (5 Service + 5 Controller)

**Cobertura:**

- Iniciação de produção com sucesso (entrada + parcelas)
- Iniciação com pagamento integral (sem parcelas subsequentes)
- Validação de status AGUARDANDO antes de iniciar
- Validação de soma entrada + parcelas = valor total do orçamento
- Criação automática de parcelas com numeração sequencial
- Salvamento de auditoria ao mudar status para INICIADA
- Endpoint REST `PATCH /api/orcamentos/{id}/iniciar`
- Tratamento de erros (404, 400 para status inválido, 400 para soma incorreta)

---

### **Testes de Confirmação de Pagamentos - US04**

**Adicionados:** 19 testes (10 Service + 9 Controller)

**Cobertura:**

**Service (ParcelaServiceTest):**

- Listar parcelas por orçamento (ordenadas por número)
- Buscar parcela por ID com sucesso
- Lançar exceção ao buscar parcela inexistente
- Confirmar pagamento com sucesso (atualiza status e data)
- Lançar exceção ao confirmar parcela já paga
- Lançar exceção ao confirmar parcela inexistente
- Retornar lista vazia quando orçamento não tem parcelas
- **Atualizar parcelas pendentes com vencimento vencido para ATRASADO**
- **Retornar zero quando não há parcelas atrasadas**
- **Não atualizar parcelas pendentes com vencimento futuro**

**Controller (ParcelaControllerTest):**

- `GET /api/parcelas/orcamento/{id}` - Lista parcelas de um orçamento
- `GET /api/parcelas/orcamento/{id}` - Retorna lista vazia quando não há parcelas
- `GET /api/parcelas/{id}` - Busca parcela por ID (200)
- `GET /api/parcelas/{id}` - Retorna 404 quando parcela não existe
- `PATCH /api/parcelas/{id}/confirmar` - Confirma pagamento (200)
- `PATCH /api/parcelas/{id}/confirmar` - Retorna 404 quando parcela não existe
- `PATCH /api/parcelas/{id}/confirmar` - Retorna 400 quando parcela já está paga

**Exemplo:**

```java
@Test
@DisplayName("Deve confirmar pagamento de parcela com sucesso")
void deveConfirmarPagamento() {
    when(parcelaRepository.findById(1L)).thenReturn(Optional.of(parcela));
    when(parcelaRepository.save(any(Parcela.class))).thenReturn(parcela);

    ParcelaResponseDTO resultado = parcelaService.confirmarPagamento(1L);

    assertThat(resultado).isNotNull();
    assertThat(resultado.status()).isEqualTo(StatusParcela.PAGO);
    assertThat(resultado.dataPagamento()).isEqualTo(LocalDate.now());
    verify(parcelaRepository).save(parcela);
}
```

**Exemplo de teste de atualização automática:**

```java
@Test
@DisplayName("Deve atualizar parcelas pendentes com vencimento vencido para ATRASADO")
void deveAtualizarParcelasAtrasadas() {
    Parcela parcela1 = Parcela.builder()
            .dataVencimento(LocalDate.now().minusDays(5))
            .status(StatusParcela.PENDENTE)
            .build();

    when(parcelaRepository.findByStatusAndDataVencimentoBefore(
            eq(StatusParcela.PENDENTE), any(LocalDate.class)))
            .thenReturn(List.of(parcela1));

    int quantidade = parcelaService.atualizarParcelasAtrasadas();

    assertThat(quantidade).isEqualTo(1);
    assertThat(parcela1.getStatus()).isEqualTo(StatusParcela.ATRASADO);
    verify(parcelaRepository).saveAll(any());
}
```

---

### **Rotina de Inicialização**

**Componente:** `ParcelaStartupTask`

**Funcionalidade:**

- Executa automaticamente ao iniciar a aplicação (`@PostConstruct`)
- Verifica todas as parcelas pendentes (`PENDENTE`)
- Atualiza status para `ATRASADO` quando `dataVencimento < hoje`
- Registra logs de execução e quantidade de parcelas atualizadas
- Tratamento de exceções para não impedir a inicialização da aplicação

**Exemplo de log:**

```
Iniciando verificação de parcelas atrasadas...
Total de 3 parcela(s) atualizada(s) para status ATRASADO
Verificação de parcelas atrasadas concluída. Total de parcelas atualizadas: 3
```

---

## Módulo Custos - US07

### **Testes Unitários do Service** (`CustoVariavelServiceTest` e `CustoFixoServiceTest`)

**Stack:** JUnit 5 + Mockito + AssertJ  
**Total:** 31 testes (13 CustoVariável + 18 CustoFixo)

**O que testa:**

**CustoVariavelServiceTest (13 testes):**

- Criação de custo variável com sucesso
- Busca por ID com sucesso
- Lançar exceção ao buscar custo inexistente
- Atualização com sucesso
- Lançar exceção ao atualizar custo inexistente
- Exclusão com sucesso
- Lançar exceção ao excluir custo inexistente
- Listagem de todos os custos (ordenados por data de lançamento)
- Conversão de entidade para DTO (incluindo campo status)
- Listagem por período de datas (filtro por data de lançamento)
- Retornar lista vazia quando não há custos no período
- Inicialização do status como PENDENTE em novos custos
- Preservação do status existente ao atualizar

**CustoFixoServiceTest (18 testes):**

- Criação de custo fixo com sucesso
- Inicialização de ativo=true por padrão
- Busca por ID com sucesso
- Lançar exceção ao buscar custo inexistente
- Atualização com sucesso
- Lançar exceção ao atualizar custo inexistente
- Desativação (soft delete) de custo
- Lançar exceção ao desativar custo inexistente
- Reativação de custo desativado
- Lançar exceção ao reativar custo inexistente
- Exclusão (hard delete) com sucesso
- Lançar exceção ao excluir custo inexistente
- Listagem de todos os custos (ordenados por nome)
- Listagem de custos ativos (apenas ativo=true)
- Listagem por período de dias do mês (filtro por diaVencimento)
- Listagem de custos ativos ordenados por dia de vencimento
- Inicialização do status como PENDENTE em novos custos
- Preservação do status existente ao atualizar

**Exemplo:**

```java
@Test
@DisplayName("Deve criar custo variável com sucesso")
void deveCriarCustoVariavel() {
    when(custoVariavelRepository.save(any(CustoVariavel.class))).thenReturn(custoVariavel);

    CustoVariavelResponseDTO response = custoVariavelService.criar(requestDTO);

    assertThat(response).isNotNull();
    assertThat(response.id()).isEqualTo(1L);
    assertThat(response.status()).isEqualTo(StatusCusto.PENDENTE);
    verify(custoVariavelRepository).save(any(CustoVariavel.class));
}
```

---

### **Testes de Integração do Controller** (`CustoVariavelControllerTest` e `CustoFixoControllerTest`)

**Stack:** Spring MockMvc + @WebMvcTest  
**Total:** 27 testes (10 CustoVariável + 17 CustoFixo)

**O que testa:**

**CustoVariavelControllerTest (10 testes):**

- **POST `/api/custos-variaveis` - Criação com sucesso (201)**
- **POST `/api/custos-variaveis` - Validação de dados inválidos (400)**
- **GET `/api/custos-variaveis/{id}` - Busca com sucesso (200)**
- **GET `/api/custos-variaveis/{id}` - Custo não encontrado (404)**
- **GET `/api/custos-variaveis` - Listagem completa**
- **GET `/api/custos-variaveis?dataInicio=X&dataFim=Y` - Filtro por período**
- **PUT `/api/custos-variaveis/{id}` - Atualização com sucesso (200)**
- **PUT `/api/custos-variaveis/{id}` - Atualização de custo inexistente (404)**
- **DELETE `/api/custos-variaveis/{id}` - Deleção com sucesso (204)**
- **DELETE `/api/custos-variaveis/{id}` - Deleção de custo inexistente (404)**

**CustoFixoControllerTest (17 testes):**

- **POST `/api/custos-fixos` - Criação com sucesso (201)**
- **POST `/api/custos-fixos` - Validação de dados inválidos (400)**
- **GET `/api/custos-fixos/{id}` - Busca com sucesso (200)**
- **GET `/api/custos-fixos/{id}` - Custo não encontrado (404)**
- **GET `/api/custos-fixos` - Listagem completa**
- **GET `/api/custos-fixos?apenasAtivos=true` - Filtro de custos ativos**
- **GET `/api/custos-fixos?orderByDiaVencimento=true` - Ordenação por dia de vencimento**
- **GET `/api/custos-fixos?apenasAtivos=true&orderByDiaVencimento=true` - Filtros combinados**
- **GET `/api/custos-fixos?diaInicio=X&diaFim=Y` - Filtro por período de dias do mês**
- **PUT `/api/custos-fixos/{id}` - Atualização com sucesso (200)**
- **PUT `/api/custos-fixos/{id}` - Atualização de custo inexistente (404)**
- **PATCH `/api/custos-fixos/{id}/desativar` - Desativação (soft delete) com sucesso (200)**
- **PATCH `/api/custos-fixos/{id}/desativar` - Desativação de custo inexistente (404)**
- **PATCH `/api/custos-fixos/{id}/reativar` - Reativação com sucesso (200)**
- **PATCH `/api/custos-fixos/{id}/reativar` - Reativação de custo inexistente (404)**
- **DELETE `/api/custos-fixos/{id}` - Deleção permanente com sucesso (204)**
- **DELETE `/api/custos-fixos/{id}` - Deleção de custo inexistente (404)**

**Exemplo:**

```java
@Test
@DisplayName("GET /api/custos-fixos?apenasAtivos=true - Deve listar apenas custos ativos")
void deveListarApenasAtivos() throws Exception {
    when(custoFixoService.listarAtivos()).thenReturn(List.of(responseDTO));

    mockMvc.perform(get("/api/custos-fixos")
            .param("apenasAtivos", "true"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$[0].id").value(1))
        .andExpect(jsonPath("$[0].ativo").value(true));

    verify(custoFixoService).listarAtivos();
}
```

---

### **Funcionalidades Testadas - US07**

**Cobertura:**

**Custo Variável:**

- CRUD completo (Create, Read, Update, Delete)
- Listagem ordenada por data de lançamento (mais recente primeiro)
- Filtro por período de datas completo (dataInicio até dataFim)
- Inicialização e gerenciamento do campo status (PENDENTE/PAGO)
- Validação de campos obrigatórios e regras de negócio

**Custo Fixo:**

- CRUD completo com soft delete (desativar/reativar)
- Hard delete (exclusão permanente) disponível
- Listagem ordenada por nome ou por dia de vencimento
- Filtro de custos ativos (ativo=true)
- Filtro por período de dias do mês (diaInicio até diaFim, valores 1-31)
- Validação do campo diaVencimento (1-31)
- Inicialização automática de ativo=true e status=PENDENTE
- Gerenciamento do campo status (PENDENTE/PAGO)

---

## Padrões e Boas Práticas Aplicadas

### Nomenclatura Clara

- Prefixo `deve` + ação + condição
- Ex: `deveCriarOrcamento`, `deveLancarExcecaoQuandoNaoEncontrado`

### AAA Pattern (Arrange-Act-Assert)

```java
// Arrange - preparação
when(repository.save(any())).thenReturn(orcamento);

// Act - execução
OrcamentoResponseDTO response = service.criarOrcamento(dto);

// Assert - verificação
assertThat(response.id()).isEqualTo(1L);
```

### Uso de AssertJ

- Fluent assertions mais legíveis
- `assertThat(list).hasSize(2)`
- `isEqualByComparingTo()` para BigDecimal

### Mocks Apropriados

- Service: usa `@Mock` do repository
- Controller: usa `@MockitoBean` do service

### Isolamento de Testes

- Cada teste é independente
- `@BeforeEach` reseta estado
- Sem side effects entre testes

### DisplayName Descritivo

```java
@DisplayName("Deve criar orçamento com sucesso")
```

---

## Métricas

### Performance

- Testes unitários (Service): < 8s
- Testes de integração (Controller): < 12s
- **Total (110 testes): < 20s**

---

## Debugging de Testes

### Ver stack trace completo

```bash
mvnw test -X
```

### Rodar teste específico

```bash
mvnw test -Dtest="OrcamentoServiceTest#deveCriarOrcamento"
```

### Pular testes durante build

```bash
mvnw package -DskipTests
```

---

## Dependências de Teste

Todas incluídas via `spring-boot-starter-test`:

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-test</artifactId>
    <scope>test</scope>
</dependency>
```

**Inclui:**
-JUnit 5 (testes)
-Mockito (mocks)
-AssertJ (assertions fluentes)
-Spring Test + MockMvc (integração)

---

## Por que Apenas 110 Testes?

### Pragmatismo > Cobertura Cega

**Não testamos:**

- **Entidades** - Cálculos simples (quantidade × valor) são verificados em code review
- **Repositórios** - Spring Data JPA é framework maduro e testado
- **Validações** - Bean Validation é framework maduro
- **Getters/Setters** - Lombok/Records geram código confiável

**Testamos:**

- **Service** - Nossa lógica de negócio (conversões, regras, auditoria, confirmação de pagamentos, gestão de custos)
- **Controller** - Contrato de API (HTTP status, JSON, validações)
- **US02** - Funcionalidades de auditoria (histórico de alterações)
- **US03** - Iniciação de produção e plano de parcelamento
- **US04** - Confirmação manual de pagamentos
- **US07** - Gestão de custos fixos e variáveis (CRUD, soft delete, filtros por período)

### Resultado

- Manutenção mais fácil (menos código de teste para atualizar)
- Foco em cenários reais de falha
- Menos duplicação (não testamos o que o framework já testa)

---

## Referências

- [JUnit 5 User Guide](https://junit.org/junit5/docs/current/user-guide/)
- [Mockito Documentation](https://javadoc.io/doc/org.mockito/mockito-core/latest/org/mockito/Mockito.html)
- [AssertJ Guide](https://assertj.github.io/doc/)
- [Spring Testing](https://docs.spring.io/spring-framework/reference/testing.html)
