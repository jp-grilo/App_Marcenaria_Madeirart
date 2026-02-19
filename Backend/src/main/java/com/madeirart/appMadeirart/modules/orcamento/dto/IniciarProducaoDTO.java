package com.madeirart.appMadeirart.modules.orcamento.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * DTO para iniciar produção de um orçamento com plano de parcelamento
 */
public record IniciarProducaoDTO(
    @NotNull(message = "Valor de entrada é obrigatório")
    @PositiveOrZero(message = "Valor de entrada não pode ser negativo")
    BigDecimal valorEntrada,

    @NotNull(message = "Data de entrada é obrigatória")
    LocalDate dataEntrada,

    @Valid
    List<ParcelaDTO> parcelas
) {}
