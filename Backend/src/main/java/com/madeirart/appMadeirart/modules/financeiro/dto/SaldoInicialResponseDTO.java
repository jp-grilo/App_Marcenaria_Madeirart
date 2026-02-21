package com.madeirart.appMadeirart.modules.financeiro.dto;

import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * DTO para resposta do saldo inicial
 */
@Builder
public record SaldoInicialResponseDTO(
        Long id,
        BigDecimal valor,
        String observacao,
        LocalDateTime dataRegistro,
        LocalDateTime dataAtualizacao) {
}
