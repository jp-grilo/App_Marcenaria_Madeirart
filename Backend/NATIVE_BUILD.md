# Compilação Nativa com GraalVM

Este guia descreve como compilar o backend Madeirart para um executável nativo do Windows usando GraalVM Native Image.

## Pré-requisitos

1. **GraalVM 22+** instalado e configurado
   - `JAVA_HOME` apontando para o diretório do GraalVM
   - GraalVM `bin` no `PATH` do sistema
   - Visual Studio Build Tools 2019+ (necessário para compilação no Windows)

2. Verificar instalação:
```powershell
java -version
native-image --version
```

## Compilação

### Etapa 1: Build JAR tradicional (teste)

```powershell
cd Backend
.\mvnw clean package -DskipTests
```

### Etapa 2: Compilação Nativa

```powershell
.\mvnw -Pnative clean package -DskipTests
```

**Opções do comando:**
- `-Pnative`: Ativa o profile de compilação nativa
- `clean package`: Limpa e compila o projeto
- `-DskipTests`: Pula a execução de testes para acelerar o build

**Tempo estimado:** 5-15 minutos (dependendo do hardware)

**Saída:** `target\madeirart-backend.exe`

### Etapa 3: Testar o Executável

```powershell
cd target
.\madeirart-backend.exe
```

O backend deve iniciar em menos de 3 segundos.

## Verificação de Funcionalidades

Teste os endpoints principais:

```powershell
# Health check
curl http://localhost:8080/actuator/health

# Listar orçamentos
curl http://localhost:8080/api/orcamentos

# Listar custos fixos
curl http://localhost:8080/api/custos-fixos

# Dashboard
curl http://localhost:8080/api/dashboard/resumo

# Backup
curl -X POST http://localhost:8080/api/backup/execute
```

## Métricas Esperadas

- **Tempo de inicialização:** < 3 segundos
- **Consumo de memória (repouso):** < 100MB
- **Tamanho do executável:** ~70-100MB

## Troubleshooting

### Erro: "native-image not found"

Instale o componente native-image no GraalVM:
```powershell
gu install native-image
```

### Erro: "Visual Studio not found"

Instale Visual Studio Build Tools 2019 ou superior com componentes C++.

### Erro de reflection em runtime

Se alguma classe não estiver funcionando por reflection:
1. Adicione a classe em `src/main/resources/META-INF/native-image/reflect-config.json`
2. Ou adicione em `NativeImageConfiguration.java`

### Banco de dados SQLite não encontrado

O banco será criado em: `%APPDATA%\Roaming\madeirart\marcenaria.db`

Verifique permissões de escrita na pasta.

## Configurações Avançadas

### Adicionar mais memória ao build

Edite `pom.xml` no profile `native`, adicione:
```xml
<buildArg>-J-Xmx8g</buildArg>
```

### Habilitar debug

```xml
<buildArg>-H:GenerateDebugInfo=1</buildArg>
```

### Ver classes incluídas no build

```xml
<buildArg>--trace-class-initialization=com.madeirart</buildArg>
```

## Arquivos de Configuração

- `reflect-config.json`: Configuração de reflection para entidades e DTOs
- `resource-config.json`: Recursos incluídos no executável (properties, migrations)
- `serialization-config.json`: Classes serializadas via JSON
- `NativeImageConfiguration.java`: Hints programáticos para runtime

## Notas Importantes

1. **DevTools desabilitado**: Spring Boot DevTools não é compatível com Native Image
2. **Hibernate**: Usa SQLite dialect do hibernate-community-dialects
3. **Lombok**: Funciona normalmente no build nativo (processado em compile-time)
4. **Records**: Java Records são totalmente compatíveis
