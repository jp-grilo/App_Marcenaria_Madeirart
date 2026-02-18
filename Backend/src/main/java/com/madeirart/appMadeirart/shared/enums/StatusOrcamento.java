package com.madeirart.appMadeirart.shared.enums;

/**
 * Status do orçamento no ciclo de vida
 */
public enum StatusOrcamento {
    AGUARDANDO("Aguardando"),
    INICIADA("Iniciada"),
    FINALIZADA("Finalizada"),
    CANCELADA("Cancelada");

    private final String descricao;

    StatusOrcamento(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }
}
