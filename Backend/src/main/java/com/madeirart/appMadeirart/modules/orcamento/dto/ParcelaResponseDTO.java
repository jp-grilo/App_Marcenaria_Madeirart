package com.madeirart.appMadeirart.modules.orcamento.dto;

import com.madeirart.appMadeirart.shared.enums.StatusParcela;
import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * DTO para resposta de dados de uma parcela
 */
@Builder
public record ParcelaResponseDTO(
    Long id,
    Integer numeroParcela,
    BigDecimal valor,
    LocalDate dataVencimento,
    LocalDate dataPagamento,
    StatusParcela status
) {}
