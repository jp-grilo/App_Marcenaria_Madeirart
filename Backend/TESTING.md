# 🧪 Testes do Backend - Módulo Orçamento

## 📊 Cobertura de Testes

###Testes Criados

| Tipo            | Arquivo                   | Qtd Testes    | Descrição         |
| --------------- | ------------------------- | ------------- | ----------------- |
| **Unit**        | `OrcamentoServiceTest`    | 17            | Service com mocks |
| **Integration** | `OrcamentoControllerTest` | 16            | Endpoints REST    |
| **Unit**        | `ParcelaServiceTest`      | 10            | Service com mocks |
| **Integration** | `ParcelaControllerTest`   | 9             | Endpoints REST    |
| **TOTAL**       | **4 arquivos**            | **52 testes** |                   |

## Como Executar

### Todos os testes

```bash
mvnw test
```

### Testes de um módulo específico

```bash
mvnw test -Dtest="OrcamentoServiceTest"
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

- Testes unitários (Service): < 6s
- Testes de integração (Controller): < 10s
- **Total (49 testes): < 16s**

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

## Por que Apenas 49 Testes?

### Pragmatismo > Cobertura Cega

**Não testamos:**

- **Entidades** - Cálculos simples (quantidade × valor) são verificados em code review
- **Repositórios** - Spring Data JPA é framework maduro e testado
- **Validações** - Bean Validation é framework maduro
- **Getters/Setters** - Lombok/Records geram código confiável

**Testamos:**

- **Service** - Nossa lógica de negócio (conversões, regras, auditoria, confirmação de pagamentos)
- **Controller** - Contrato de API (HTTP status, JSON, validações)
- **US02** - Funcionalidades de auditoria (histórico de alterações)
- **US03** - Iniciação de produção e plano de parcelamento
- **US04** - Confirmação manual de pagamentos

### Resultado

- Manutenção mais fácil (menos código de teste para atualizar)
- Build mais rápido (< 16s vs > 40s com testes excessivos)
- Foco em cenários reais de falha
- Menos duplicação (não testamos o que o framework já testa)

---

## Referências

- [JUnit 5 User Guide](https://junit.org/junit5/docs/current/user-guide/)
- [Mockito Documentation](https://javadoc.io/doc/org.mockito/mockito-core/latest/org/mockito/Mockito.html)
- [AssertJ Guide](https://assertj.github.io/doc/)
- [Spring Testing](https://docs.spring.io/spring-framework/reference/testing.html)
