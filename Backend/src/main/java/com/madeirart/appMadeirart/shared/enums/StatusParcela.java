package com.madeirart.appMadeirart.shared.enums;

/**
 * Status da parcela de pagamento
 */
public enum StatusParcela {
    PENDENTE("Pendente"),
    PAGO("Pago"),
    ATRASADO("Atrasado");

    private final String descricao;

    StatusParcela(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }
}
