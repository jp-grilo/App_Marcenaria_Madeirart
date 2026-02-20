-- Script para adicionar o status ATRASADO às constraints das tabelas de custos
-- Este script corrige a constraint CHECK que estava impedindo o uso do status ATRASADO

-- ===== TABELA CUSTOS_VARIAVEIS =====

-- 1. Criar tabela temporária com a nova constraint
CREATE TABLE custos_variaveis_new (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    nome VARCHAR(200) NOT NULL,
    valor DECIMAL(10, 2) NOT NULL,
    data_lancamento DATE NOT NULL,
    descricao VARCHAR(500),
    status VARCHAR(20) NOT NULL CHECK (status IN ('PENDENTE', 'PAGO', 'ATRASADO')),
    parcelado BOOLEAN DEFAULT FALSE,
    numero_parcela INTEGER,
    total_parcelas INTEGER,
    custo_origem_id BIGINT,
    created_at DATE,
    updated_at DATE
);

-- 2. Copiar dados da tabela original para a nova
INSERT INTO custos_variaveis_new 
SELECT id, nome, valor, data_lancamento, descricao, status, parcelado, 
       numero_parcela, total_parcelas, custo_origem_id, created_at, updated_at
FROM custos_variaveis;

-- 3. Remover tabela original
DROP TABLE custos_variaveis;

-- 4. Renomear tabela nova
ALTER TABLE custos_variaveis_new RENAME TO custos_variaveis;

-- ===== TABELA CUSTOS_FIXOS =====

-- 1. Criar tabela temporária com a nova constraint
CREATE TABLE custos_fixos_new (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    nome VARCHAR(200) NOT NULL,
    valor DECIMAL(10, 2) NOT NULL,
    dia_vencimento INTEGER NOT NULL CHECK (dia_vencimento BETWEEN 1 AND 31),
    descricao VARCHAR(500),
    ativo BOOLEAN NOT NULL DEFAULT TRUE,
    status VARCHAR(20) NOT NULL CHECK (status IN ('PENDENTE', 'PAGO', 'ATRASADO')),
    created_at DATE,
    updated_at DATE
);

-- 2. Copiar dados da tabela original para a nova
INSERT INTO custos_fixos_new 
SELECT id, nome, valor, dia_vencimento, descricao, ativo, status, created_at, updated_at
FROM custos_fixos;

-- 3. Remover tabela original
DROP TABLE custos_fixos;

-- 4. Renomear tabela nova
ALTER TABLE custos_fixos_new RENAME TO custos_fixos;
