# Madeirart Backend API

Sistema de gestão financeira para marcenaria - API REST desenvolvida em Spring Boot.

## 🚀 Tecnologias

- **Java 17+**
- **Spring Boot 3.x**
- **Spring Data JPA**
- **SQLite** (banco de dados)
- **Lombok**
- **Bean Validation**
- **Maven**

## 📦 Como Executar

### Pré-requisitos

- JDK 17 ou superior
- Maven 3.6+ (ou usar o wrapper incluído)

### Executar em Desenvolvimento

```bash
# Windows
mvnw.cmd spring-boot:run

# Linux/Mac
./mvnw spring-boot:run
```

A aplicação estará disponível em `http://localhost:8080`

## 💾 Banco de Dados

O sistema usa **SQLite** como banco de dados, armazenado automaticamente em:

- **Windows**: `C:\Users\{seu-usuario}\AppData\Roaming\madeirart\marcenaria.db`
- **Linux/Mac**: `~/madeirart/marcenaria.db`

### ✨ Vantagens

✅ **Zero configuração** - o diretório é criado automaticamente na primeira execução  
✅ **Portável** - ao clonar o projeto em outro computador, basta executar  
✅ **Dados separados** - o banco fica fora do projeto (não vai para o Git)  
✅ **Sem servidor** - não precisa instalar MySQL, PostgreSQL, etc.

### Schema

As tabelas são criadas **automaticamente** pelo Hibernate na primeira execução:

- `orcamentos` - Armazena os orçamentos
- `item_material` - Itens de cada orçamento

O schema é atualizado automaticamente quando as entidades mudam (`spring.jpa.hibernate.ddl-auto=update`).

## 📡 API Endpoints

A documentação completa dos endpoints está disponível no arquivo `Collection.json` (importar no Postman/Insomnia).

### Principais Endpoints

| Método | Endpoint | Descrição |
|--------|----------|-----------|
| POST | `/api/orcamentos` | Criar orçamento |
| GET | `/api/orcamentos` | Listar todos |
| GET | `/api/orcamentos/{id}` | Buscar por ID |
| GET | `/api/orcamentos?status=AGUARDANDO` | Filtrar por status |
| PUT | `/api/orcamentos/{id}` | Atualizar orçamento |
| DELETE | `/api/orcamentos/{id}` | Deletar orçamento |

## 🏗️ Estrutura do Projeto

```
src/main/java/com/madeirart/appMadeirart/
├── modules/
│   └── orcamento/
│       ├── controller/    # REST Controllers
│       ├── dto/           # Data Transfer Objects
│       ├── entity/        # Entidades JPA
│       ├── repository/    # Repositories
│       └── service/       # Lógica de negócio
└── shared/
    ├── enums/             # Enumerações
    └── exception/         # Tratamento de erros
```

## 🔧 Configuração

Edite `src/main/resources/application.properties` para customizar:

- Porta do servidor (padrão: 8080)
- Localização do banco de dados
- Logs do SQL
- Outras configurações do Spring

## 📝 Notas

- O sistema usa **Bean Validation** para validar os dados de entrada
- Tratamento de erros global via `@ControllerAdvice`
- Cross-Origin habilitado (`@CrossOrigin`) para integração com frontend
- Logs SQL habilitados para debug (`show-sql=true`)
