package com.madeirart.appMadeirart.modules.orcamento.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * DTO para dados de uma parcela no plano de parcelamento
 */
public record ParcelaDTO(
    @NotNull(message = "Valor da parcela é obrigatório")
    @Positive(message = "Valor da parcela deve ser positivo")
    BigDecimal valor,

    @NotNull(message = "Data de vencimento é obrigatória")
    LocalDate dataVencimento
) {}
