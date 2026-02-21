@echo off
REM Script de compilacao nativa do Madeirart Backend
REM Requer GraalVM 22+ instalado e configurado

echo ========================================
echo Madeirart Backend - Compilacao Nativa
echo ========================================
echo.

REM Verificar se GraalVM esta instalado
echo [1/4] Verificando GraalVM...
java -version 2>&1 | findstr /C:"GraalVM" > nul
if %errorlevel% neq 0 (
    echo ERRO: GraalVM nao encontrado!
    echo Verifique se JAVA_HOME esta configurado para o GraalVM.
    exit /b 1
)

native-image --version > nul 2>&1
if %errorlevel% neq 0 (
    echo ERRO: native-image nao encontrado!
    echo Execute: gu install native-image
    exit /b 1
)

echo GraalVM detectado com sucesso!
echo.

REM Compilar projeto
echo [2/4] Compilando projeto Java...
call mvnw.cmd clean package -DskipTests
if %errorlevel% neq 0 (
    echo ERRO: Falha na compilacao do projeto
    exit /b 1
)
echo.

REM Compilacao nativa
echo [3/4] Iniciando compilacao nativa (isso pode demorar 5-15 minutos)...
echo Horario de inicio: %time%
call mvnw.cmd -Pnative package -DskipTests
if %errorlevel% neq 0 (
    echo ERRO: Falha na compilacao nativa
    exit /b 1
)
echo.

REM Verificar saida
echo [4/4] Verificando executavel...
if exist target\madeirart-backend.exe (
    echo.
    echo ========================================
    echo SUCESSO! Executavel criado em:
    echo target\madeirart-backend.exe
    echo ========================================
    echo.
    
    REM Informacoes do arquivo
    for %%A in (target\madeirart-backend.exe) do (
        echo Tamanho: %%~zA bytes
    )
    
    echo Horario de conclusao: %time%
    echo.
    echo Para testar, execute:
    echo   cd target
    echo   madeirart-backend.exe
    echo.
) else (
    echo ERRO: Executavel nao foi criado!
    exit /b 1
)

exit /b 0
