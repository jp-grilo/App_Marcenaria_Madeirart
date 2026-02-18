# Instruções do Backend - Spring Boot (Madeirart)

## 🏗️ Estrutura DDD Modular

```
Backend/src/main/java/com/madeirart/appMadeirart/
├── shared/           # config/, exception/, util/
├── modules/          # Módulos de negócio por domínio
│   ├── {modulo}/
│   │   ├── controller/
│   │   ├── service/
│   │   ├── dto/
│   │   ├── repository/
│   │   └── model/
```

---

## 📋 Regras Obrigatórias

### 1. **NUNCA crie classes fora da estrutura modular**
- ❌ ERRADO: `com.madeirart.appMadeirart.OrcamentoService`
- ✅ CORRETO: `com.madeirart.appMadeirart.modules.orcamento.service.OrcamentoService`

### 2. **Sempre use DTOs para comunicação externa**
- Controllers DEVEM receber e retornar DTOs, NUNCA entidades diretas
- **DTOs DEVEM ser `record`** (imutáveis, concisos, idiomáticos desde Java 14+)
- Nomeie DTOs com sufixos claros: `OrcamentoRequestDTO`, `OrcamentoResponseDTO`
- Use `@Valid` para validação automática de DTOs
- Bean Validation funciona normalmente em records (`@NotNull`, `@Positive`, etc.)

### 3. **Isolamento de Lógica de Negócio**
- Lógica de negócio DEVE estar nos Services, NUNCA nos Controllers
- Controllers são apenas roteadores (recebem requisição → chamam service → retornam resposta)
- Use `@Transactional` em métodos de service que alteram dados

### 4. **Repositórios Minimalistas**
- Repositories devem conter apenas queries customizadas quando necessário
- Prefira usar métodos derivados do Spring Data JPA (ex: `findByStatus`, `findByClienteId`)
- Para queries complexas, use `@Query` com JPQL

### 5. **Tratamento de Exceções Centralizado**
- Crie exceções customizadas em `shared/exception/` (ex: `OrcamentoNotFoundException`)
- Use `@ControllerAdvice` no `GlobalExceptionHandler` para tratamento global
- NUNCA retorne stack traces ao cliente em produção

---

## 🛠️ Padrões Obrigatórios

### Service Layer Pattern
- Todo módulo tem um Service principal anotado com `@Service` + `@RequiredArgsConstructor`
- Métodos de escrita/atualização devem ter `@Transactional`
- Services retornam DTOs, nunca entidades

### Strategy Pattern
- Use interfaces para comportamentos intercambiáveis (ex: parsers de extratos)
- Implemente com `@Component` para injeção automática

### Enums para Status
- SEMPRE use Enums para estados (StatusOrcamento, StatusParcela, etc.)
- Nunca use Strings para representar status

### Auditoria
- Use `@PreUpdate` para snapshots antes de alterações
- Salve em tabela dedicada (ex: OrcamentoAuditoria)

---

## 🎯 Lombok (OBRIGATÓRIO)

### Anotações Padrão por Tipo de Classe

**Entidades JPA:**
```java
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Orcamento { ... }
```

**DTOs (USAR RECORDS):**
```java
// ✅ CORRETO: DTOs como records (imutáveis, concisos)
public record OrcamentoRequestDTO(
    @NotBlank(message = "Cliente é obrigatório")
    String cliente,
    
    @NotNull(message = "Data é obrigatória")
    LocalDate data,
    
    @Valid
    List<ItemMaterialDTO> itens
) {}

// Para DTOs de resposta com builder pattern:
@Builder
public record OrcamentoResponseDTO(
    Long id,
    String cliente,
    BigDecimal valorTotal,
    StatusOrcamento status
) {}
```

**Por que records?**
- ✅ Imutabilidade por padrão (thread-safe)
- ✅ Código mais conciso (~70% menos linhas)
- ✅ Intenção clara: é apenas transportador de dados
- ✅ Getters sem prefixo `get` (`dto.cliente()` ao invés de `dto.getCliente()`)
- ✅ `equals()`, `hashCode()`, `toString()` gerados automaticamente
- ✅ Bean Validation funciona normalmente

**Services/Components:**
```java
@Service
@RequiredArgsConstructor  // Gera construtor com campos final
public class OrcamentoService {
    private final OrcamentoRepository repository;
    // NÃO use @Autowired em campos!
}
```
    private final OrcamentoRepository repository;
    // NÃO use @Autowired em campos!
}
```

### Regras de Construtores
- **SEMPRE** use `@NoArgsConstructor` e `@AllArgsConstructor` em entidades JPA
- Records geram construtor canônico automaticamente (não precisam de anotações)
- Se precisar de construtor customizado em entidades, mantenha as anotações acima:
```java
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Orcamento {
    // Construtor customizado para conversão de DTO (record)
    public Orcamento(OrcamentoRequestDTO dto) {
        this.cliente = dto.cliente();      // Records usam getters sem "get"
        this.moveis = dto.moveis();
        // ...
    }
}
```

### Injeção de Dependência
- ✅ **USE**: `@RequiredArgsConstructor` + campos `final` (injeção por construtor)
- ❌ **NÃO USE**: `@Autowired` em campos (dificulta testes unitários e uso de mocks)
- Injeção por construtor permite criar instâncias facilmente em testes sem reflection

### Outras Anotações Úteis
- `@Builder`: ParaPattern Builder em entidades/DTOs complexos
- `@Data`: Gera getters, setters, toString, equals, hashCode
- `@Slf4j`: Para logging automático (`log.info()`, `log.error()`)

---

## 🗄️ Banco de Dados (SQLite)

- Database: `marcenaria.db` em `%APPDATA%/Madeirart/`
- Dev: `ddl-auto=update` | Prod: Flyway/Liquibase
- Relacionamentos: `fetch = FetchType.LAZY` SEMPRE para coleções
- Use DTOs para evitar loops JSON (NUNCA `@JsonIgnore` em entidades)

---

## 📦 Utils Obrigatórios

- **Localização**: `shared/util/` (globais) ou `modules/{modulo}/util/` (específicos)
- **DateUtil**: Formatação/parsing (LocalDate, LocalDateTime)
- **CurrencyUtil**: BigDecimal → String formatado (pt-BR)
- **ValidationUtil**: CPF, CNPJ, etc.
- **Padrão**: Classes finais com construtor privado + métodos static

---

## 🔐 Validações e Performance

- Bean Validation nos DTOs: `@NotNull`, `@NotBlank`, `@Min`, `@Valid`
- Mensagens de erro em português e descritivas
- GraalVM: evite reflection, prefira config explícita
- Teste builds nativos frequentemente

---

## 🚫 Anti-Padrões (NÃO FAÇA)

1. ❌ Expor entidades JPA diretamente nos endpoints
2. ❌ Lógica de negócio nos Controllers
3. ❌ Usar Strings para status (use Enums)
4. ❌ Queries N+1 (sempre use `JOIN FETCH` ou DTOs projetados)
5. ❌ Exceções genéricas (`throw new Exception("erro")`)
6. ❌ Hardcode de valores (use `application.properties`)
7. ❌ Misturar responsabilidades (ex: Service fazendo parsing de arquivo)
8. ❌ `@Autowired` em campos (use `@RequiredArgsConstructor` + campos final)
9. ❌ Entidades JPA sem `@NoArgsConstructor` e `@AllArgsConstructor`
10. ❌ DTOs como classes com Lombok (use `record` ao invés de `@Data`)

---

## 📝 Nomenclatura

- **Classes**: `{Entidade}Controller`, `{Entidade}Service`, `{Entidade}{Tipo}DTO`, `{Entidade}Repository`
- **Métodos**: Controllers (verbos HTTP), Services (ações de negócio), Repositories (queries derivadas)
- **Pacotes**: minúsculo, singular (`orcamento`, não `orcamentos`)
- **Exceptions**: `{Entidade}{Erro}Exception` (ex: `OrcamentoNotFoundException`)

---

## 📚 Stack
Spring Boot 3.x + SQLite + GraalVM Native Image
