package com.madeirart.appMadeirart.shared.enums;

/**
 * Tipo de transação financeira
 */
public enum TipoTransacao {
    ENTRADA("Entrada"),
    SAIDA("Saída");

    private final String descricao;

    TipoTransacao(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }
}
