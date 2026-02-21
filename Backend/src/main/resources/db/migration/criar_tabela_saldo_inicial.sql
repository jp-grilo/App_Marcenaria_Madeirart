-- Migration para criar a tabela de saldo inicial
-- Tabela para armazenar o saldo de abertura do sistema (único registro)
CREATE TABLE
    saldo_inicial (
        id INTEGER PRIMARY KEY AUTOINCREMENT,
        valor DECIMAL(12, 2) NOT NULL,
        observacao VARCHAR(500),
        data_registro TIMESTAMP NOT NULL,
        data_atualizacao TIMESTAMP NOT NULL
    );

-- Criar índice único para garantir apenas um registro
CREATE UNIQUE INDEX idx_saldo_inicial_singleton ON saldo_inicial (id)
WHERE
    id = 1;