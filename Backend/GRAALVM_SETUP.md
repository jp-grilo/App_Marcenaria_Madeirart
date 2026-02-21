# Guia de Configuração do GraalVM

## 1. Download e Instalação

1. Acesse: https://www.graalvm.org/downloads/
2. Baixe **GraalVM Community Edition 22.x** ou superior para Windows
3. Escolha a versão **Java 17** ou **Java 21**
4. Extraia para uma pasta (ex: `C:\graalvm-ce-java17-22.3.0`)

## 2. Configurar Variáveis de Ambiente

### Opção A: Via PowerShell (Admin)

```powershell
# Definir JAVA_HOME
[System.Environment]::SetEnvironmentVariable('JAVA_HOME', 'C:\graalvm-ce-java17-22.3.0', 'Machine')

# Adicionar ao PATH
$path = [System.Environment]::GetEnvironmentVariable('Path', 'Machine')
$newPath = "C:\graalvm-ce-java17-22.3.0\bin;" + $path
[System.Environment]::SetEnvironmentVariable('Path', $newPath, 'Machine')

# Reiniciar terminal para aplicar mudanças
```

### Opção B: Via Interface Gráfica

1. Pressione `Win + Pause` ou vá em **Sistema > Configurações Avançadas do Sistema**
2. Clique em **Variáveis de Ambiente**
3. Em **Variáveis do Sistema**, clique em **Novo**:
   - Nome: `JAVA_HOME`
   - Valor: `C:\graalvm-ce-java17-22.3.0` (caminho da instalação)
4. Edite a variável `Path` e adicione:
   - `%JAVA_HOME%\bin`
5. Clique em **OK** em todas as janelas

## 3. Instalar Native Image

Abra um **novo terminal** (PowerShell ou CMD) e execute:

```powershell
gu install native-image
```

Se aparecer erro "gu não encontrado", verifique se o PATH está correto.

## 4. Instalar Visual Studio Build Tools

Native Image no Windows requer ferramentas de compilação C++:

1. Baixe **Visual Studio Build Tools 2019 ou 2022**: https://visualstudio.microsoft.com/downloads/
2. Na instalação, selecione:
   - ✅ **Desenvolvimento para desktop com C++**
   - ✅ **Windows 10/11 SDK**
3. Instale e reinicie o computador

## 5. Verificar Instalação

Abra um **novo terminal** e execute:

```powershell
# Verificar Java/GraalVM
java -version
# Deve mostrar "GraalVM CE" na saída

# Verificar Native Image
native-image --version
# Deve mostrar versão do GraalVM Native Image

# Verificar compilador C++
cl
# Deve mostrar "Microsoft (R) C/C++ Optimizing Compiler"
```

### Saída Esperada

```
java version "17.0.x" 2024-xx-xx LTS
Java(TM) SE Runtime Environment GraalVM CE 22.x.x (build 17.0.x+xx-jvmci-xx)
Java HotSpot(TM) 64-Bit Server VM GraalVM CE 22.x.x (build 17.0.x+xx-jvmci-xx, mixed mode)
```

## 6. Compilar o Backend

Com tudo configurado, execute:

```powershell
cd Backend
.\build-native.bat
```

Ou manualmente:

```powershell
.\mvnw -Pnative clean package -DskipTests
```

## Troubleshooting

### "GraalVM nao encontrado"

- Verifique se `JAVA_HOME` está configurado corretamente
- Execute `echo %JAVA_HOME%` (CMD) ou `$env:JAVA_HOME` (PowerShell)
- Feche e abra um novo terminal

### "native-image not found"

- Execute: `gu install native-image`
- Verifique se `%JAVA_HOME%\bin` está no PATH

### "Visual Studio not found"

- Instale Visual Studio Build Tools com componente C++
- Execute o build em um **Developer Command Prompt for VS 2022**

### Erro de memória durante build

- Aumente a memória disponível para o Maven:
  ```powershell
  $env:MAVEN_OPTS="-Xmx8g"
  ```

## Alternativamente: Usar Docker

Se tiver problemas com a instalação local, use Docker:

```dockerfile
FROM ghcr.io/graalvm/graalvm-ce:latest

WORKDIR /app
COPY . .

RUN ./mvnw -Pnative clean package -DskipTests

CMD ["./target/madeirart-backend"]
```

```powershell
docker build -t madeirart-backend .
docker run -p 8080:8080 madeirart-backend
```

## Links Úteis

- GraalVM Downloads: https://www.graalvm.org/downloads/
- Native Image Docs: https://www.graalvm.org/latest/reference-manual/native-image/
- Spring Boot Native: https://docs.spring.io/spring-boot/docs/current/reference/html/native-image.html
- Visual Studio Build Tools: https://visualstudio.microsoft.com/downloads/#build-tools-for-visual-studio-2022
