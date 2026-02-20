package com.madeirart.appMadeirart.shared.enums;

/**
 * Status do custo (fixo ou variável)
 */
public enum StatusCusto {
    PENDENTE("Pendente"),
    PAGO("Pago"),
    ATRASADO("Atrasado");

    private final String descricao;

    StatusCusto(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }
}
