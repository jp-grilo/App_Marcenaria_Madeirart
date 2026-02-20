package com.madeirart.appMadeirart.shared.enums;

/**
 * Origem da transação financeira
 */
public enum OrigemTransacao {
    PARCELA("Parcela de Orçamento"),
    CUSTO_FIXO("Custo Fixo"),
    CUSTO_VARIAVEL("Custo Variável");

    private final String descricao;

    OrigemTransacao(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }
}
