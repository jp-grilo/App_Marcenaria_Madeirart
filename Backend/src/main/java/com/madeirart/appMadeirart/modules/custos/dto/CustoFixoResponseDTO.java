package com.madeirart.appMadeirart.modules.custos.dto;

import com.madeirart.appMadeirart.shared.enums.StatusCusto;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * DTO para resposta de custo fixo
 */
public record CustoFixoResponseDTO(
        Long id,
        String nome,
        BigDecimal valor,
        Integer diaVencimento,
        String descricao,
        Boolean ativo,
        StatusCusto status,
        LocalDate createdAt,
        LocalDate updatedAt) {
}
