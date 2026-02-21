package com.madeirart.appMadeirart.modules.financeiro.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;

import java.math.BigDecimal;

/**
 * DTO para criação/atualização do saldo inicial
 */
@Builder
public record SaldoInicialRequestDTO(
        @NotNull(message = "Valor do saldo inicial é obrigatório") 
        BigDecimal valor,
        String observacao) {
}
