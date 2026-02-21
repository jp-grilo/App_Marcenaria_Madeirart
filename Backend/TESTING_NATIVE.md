# Checklist de Testes - Executável Nativo

## Testes de Inicialização

- [ ] Executável inicia sem erros
- [ ] Tempo de inicialização < 3 segundos
- [ ] Consumo de memória < 100MB
- [ ] Log de inicialização mostra todas as dependências carregadas
- [ ] Porta 8080 está acessível

## Testes de Banco de Dados

- [ ] Arquivo `marcenaria.db` é criado em `%APPDATA%\Roaming\madeirart\`
- [ ] Tabelas são criadas automaticamente
- [ ] Foreign keys funcionam corretamente
- [ ] Queries complexas executam sem erros

## Testes de Endpoints - Orçamentos

```powershell
# Listar todos os orçamentos
curl http://localhost:8080/api/orcamentos

# Criar novo orçamento
curl -X POST http://localhost:8080/api/orcamentos `
  -H "Content-Type: application/json" `
  -d '{\"cliente\": \"Cliente Teste\", \"moveis\": \"Mesa\", \"fatorMaoDeObra\": 2.0}'

# Buscar orçamento por ID
curl http://localhost:8080/api/orcamentos/1

# Atualizar orçamento
curl -X PUT http://localhost:8080/api/orcamentos/1 `
  -H "Content-Type: application/json" `
  -d '{\"cliente\": \"Cliente Atualizado\", \"moveis\": \"Mesa e Cadeiras\", \"fatorMaoDeObra\": 2.5}'

# Deletar orçamento
curl -X DELETE http://localhost:8080/api/orcamentos/1
```

### Checklist
- [ ] GET /api/orcamentos retorna lista vazia inicialmente
- [ ] POST /api/orcamentos cria novo orçamento
- [ ] GET /api/orcamentos/:id retorna orçamento criado
- [ ] PUT /api/orcamentos/:id atualiza orçamento
- [ ] DELETE /api/orcamentos/:id remove orçamento
- [ ] Validações funcionam (campos obrigatórios)

## Testes de Endpoints - Parcelas

```powershell
# Listar parcelas
curl http://localhost:8080/api/parcelas

# Confirmar parcela
curl -X PATCH http://localhost:8080/api/parcelas/1/confirmar
```

### Checklist
- [ ] GET /api/parcelas retorna lista de parcelas
- [ ] PATCH /api/parcelas/:id/confirmar atualiza status
- [ ] Status da parcela muda de PENDENTE para PAGO

## Testes de Endpoints - Custos Fixos

```powershell
# Listar custos fixos
curl http://localhost:8080/api/custos-fixos

# Criar custo fixo
curl -X POST http://localhost:8080/api/custos-fixos `
  -H "Content-Type: application/json" `
  -d '{\"nome\": \"Aluguel\", \"valor\": 1500.00, \"diaVencimento\": 10}'

# Atualizar custo fixo
curl -X PUT http://localhost:8080/api/custos-fixos/1 `
  -H "Content-Type: application/json" `
  -d '{\"nome\": \"Aluguel Atualizado\", \"valor\": 1600.00, \"diaVencimento\": 10}'

# Deletar custo fixo
curl -X DELETE http://localhost:8080/api/custos-fixos/1
```

### Checklist
- [ ] Operações CRUD funcionam corretamente
- [ ] Validação do dia de vencimento (1-31)
- [ ] Valores decimais são salvos corretamente

## Testes de Endpoints - Custos Variáveis

```powershell
# Listar custos variáveis
curl http://localhost:8080/api/custos-variaveis

# Criar custo variável
curl -X POST http://localhost:8080/api/custos-variaveis `
  -H "Content-Type: application/json" `
  -d '{\"nome\": \"Manutenção\", \"valor\": 300.00, \"dataLancamento\": \"2026-02-21\"}'
```

### Checklist
- [ ] Operações CRUD funcionam corretamente
- [ ] Datas são salvas corretamente
- [ ] Valores decimais são salvos corretamente

## Testes de Endpoints - Dashboard

```powershell
# Resumo do dashboard
curl http://localhost:8080/api/dashboard/resumo

# Projeção financeira
curl "http://localhost:8080/api/dashboard/projecao?mes=2&ano=2026"

# Calendário financeiro
curl "http://localhost:8080/api/financeiro/calendario?mes=2&ano=2026"
```

### Checklist
- [ ] Dashboard retorna estatísticas corretas
- [ ] Projeção calcula corretamente receitas e despesas
- [ ] Calendário agrupa transações por dia

## Testes de Endpoints - Backup

```powershell
# Executar backup manual
curl -X POST http://localhost:8080/api/backup/execute

# Listar backups
curl http://localhost:8080/api/backup/list

# Obter diretório de backups
curl http://localhost:8080/api/backup/directory
```

### Checklist
- [ ] Backup cria arquivo em `%USERPROFILE%\Documents\Madeirart Backups\`
- [ ] Nome do arquivo contém timestamp
- [ ] Lista de backups retorna todos os arquivos
- [ ] Rotação mantém apenas últimos 10 backups

## Testes de Performance

### Inicialização
```powershell
Measure-Command { .\madeirart-backend.exe }
```
- [ ] Tempo < 3 segundos

### Memória
```powershell
# No PowerShell, após iniciar o executável
Get-Process madeirart-backend | Select-Object WorkingSet64
```
- [ ] WorkingSet < 100MB (104857600 bytes)

### Carga de Requisições
```powershell
# Fazer 100 requisições
1..100 | ForEach-Object {
  curl -s http://localhost:8080/api/orcamentos
}
```
- [ ] Todas as requisições respondem em < 500ms
- [ ] Sem vazamento de memória (memória estável)

## Testes de Integridade

- [ ] Shutdown graceful funciona (Ctrl+C)
- [ ] Backup automático é criado no shutdown
- [ ] Arquivo de banco permanece íntegro após shutdown
- [ ] Reinicialização carrega dados anteriores

## Testes de Erro

- [ ] Mensagens de erro são claras
- [ ] Stack traces aparecem corretamente
- [ ] Validações retornam 400 Bad Request
- [ ] Recursos não encontrados retornam 404 Not Found

## Comparação com JAR Tradicional

| Métrica | JAR (java -jar) | Native (.exe) |
|---------|----------------|---------------|
| Tempo de inicialização | ~5-10s | < 3s |
| Memória (repouso) | ~150-200MB | < 100MB |
| Tamanho do arquivo | ~50MB | ~80-100MB |
| Dependências | JRE 17+ | Nenhuma |

## Observações

Data do teste: ______________
Versão do executável: ______________
Sistema operacional: ______________
GraalVM versão: ______________

Bugs encontrados:
- 
- 
- 

Observações gerais:
- 
- 
- 
