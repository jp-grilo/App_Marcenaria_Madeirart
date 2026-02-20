-- Adicionar suporte a parcelamento em custos variáveis
-- Execute este script se você já tem o banco de dados criado

ALTER TABLE custos_variaveis ADD COLUMN IF NOT EXISTS parcelado BOOLEAN DEFAULT FALSE;
ALTER TABLE custos_variaveis ADD COLUMN IF NOT EXISTS numero_parcela INTEGER;
ALTER TABLE custos_variaveis ADD COLUMN IF NOT EXISTS total_parcelas INTEGER;
ALTER TABLE custos_variaveis ADD COLUMN IF NOT EXISTS custo_origem_id BIGINT;

-- Atualizar custos existentes como não parcelados
UPDATE custos_variaveis SET parcelado = FALSE WHERE parcelado IS NULL;
