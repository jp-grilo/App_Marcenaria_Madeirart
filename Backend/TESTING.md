# 🧪 Testes do Backend - Módulo Orçamento

Testes essenciais e pragmáticos para a US01 (Elaboração do Orçamento Técnico).

## 📊 Cobertura de Testes

### ✅ Testes Criados

| Tipo | Arquivo | Qtd Testes | Descrição |
|------|---------|------------|-----------|
| **Unit** | `OrcamentoServiceTest` | 7 | Service com mocks |
| **Integration** | `OrcamentoControllerTest` | 8 | Endpoints REST |
| **TOTAL** | **2 arquivos** | **15 testes** | |

## 🚀 Como Executar

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

## 📋 Estrutura dos Testes

### 1️⃣ **Testes Unitários do Service** (`OrcamentoServiceTest`)

**Stack:** JUnit 5 + Mockito + AssertJ  
**Total:** 7 testes

**O que testa:**
- ✅ Criação de orçamento
- ✅ Busca por ID (sucesso)
- ✅ Busca por ID (erro - orçamento inexistente)
- ✅ Listagem por status
- ✅ Atualização (sucesso)
- ✅ Atualização (erro - orçamento inexistente)
- ✅ Deleção

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

### 2️⃣ **Testes de Integração do Controller** (`OrcamentoControllerTest`)

**Stack:** Spring MockMvc + @WebMvcTest  
**Total:** 8 testes

**O que testa:**
- ✅ POST `/api/orcamentos` - Criação com sucesso (201)
- ✅ POST `/api/orcamentos` - Validação de dados inválidos (400)
- ✅ GET `/api/orcamentos/{id}` - Busca com sucesso (200)
- ✅ GET `/api/orcamentos/{id}` - Orçamento não encontrado (404)
- ✅ GET `/api/orcamentos` - Listagem completa
- ✅ GET `/api/orcamentos?status=X` - Filtro por status
- ✅ PUT `/api/orcamentos/{id}` - Atualização
- ✅ DELETE `/api/orcamentos/{id}` - Deleção (204)

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

## 🎯 Padrões e Boas Práticas Aplicadas

### ✅ Nomenclatura Clara
- Prefixo `deve` + ação + condição
- Ex: `deveCriarOrcamento`, `deveLancarExcecaoQuandoNaoEncontrado`

### ✅ AAA Pattern (Arrange-Act-Assert)
```java
// Arrange - preparação
when(repository.save(any())).thenReturn(orcamento);

// Act - execução
OrcamentoResponseDTO response = service.criarOrcamento(dto);

// Assert - verificação
assertThat(response.id()).isEqualTo(1L);
```

### ✅ Uso de AssertJ
- Fluent assertions mais legíveis
- `assertThat(list).hasSize(2)`
- `isEqualByComparingTo()` para BigDecimal

### ✅ Mocks Apropriados
- Service: usa `@Mock` do repository
- Controller: usa `@MockBean` do service

### ✅ Isolamento de Testes
- Cada teste é independente
- `@BeforeEach` reseta estado
- Sem side effects entre testes

### ✅ DisplayName Descritivo
```java
@DisplayName("Deve criar orçamento com sucesso")
```

---

## 📈 Métricas

### Performance
- Testes unitários (Service): < 2s
- Testes de integração (Controller): < 5s
- **Total (15 testes): < 7s**

### Cobertura
- Focamos em **qualidade**, não quantidade
- Testes cobrem cenários de sucesso e falha principais
- Confiar em frameworks maduros (Spring, Hibernate) reduz necessidade de testes

---

## 🐛 Debugging de Testes

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

## 📚 Dependências de Teste

Todas incluídas via `spring-boot-starter-test`:

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-test</artifactId>
    <scope>test</scope>
</dependency>
```

**Inclui:**
- ✅ JUnit 5 (testes)
- ✅ Mockito (mocks)
- ✅ AssertJ (assertions fluentes)
- ✅ Spring Test + MockMvc (integração)

---

## 💡 Por que Apenas 15 Testes?

### Pragmatismo > Cobertura Cega

**Não testamos:**
- ❌ **Entidades** - Cálculos simples (quantidade × valor) são verificados em code review
- ❌ **Repositórios** - Spring Data JPA é framework maduro e testado
- ❌ **Validações** - Bean Validation é framework maduro
- ❌ **Getters/Setters** - Lombok/Records geram código confiável

**Testamos:**
- ✅ **Service** - Nossa lógica de negócio (conversões, regras)
- ✅ **Controller** - Contrato de API (HTTP status, JSON, validações)

### Resultado
- ✅ Manutenção mais fácil (menos código de teste para atualizar)
- ✅ Build mais rápido (< 7s vs > 20s)
- ✅ Foco em cenários reais de falha
- ✅ Menos duplicação (não testamos o que o framework já testa)

---

## 📖 Referências

- [JUnit 5 User Guide](https://junit.org/junit5/docs/current/user-guide/)
- [Mockito Documentation](https://javadoc.io/doc/org.mockito/mockito-core/latest/org/mockito/Mockito.html)
- [AssertJ Guide](https://assertj.github.io/doc/)
- [Spring Testing](https://docs.spring.io/spring-framework/reference/testing.html)

