package com.madeirart.appMadeirart.modules.custos.dto;

import com.madeirart.appMadeirart.shared.enums.StatusCusto;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * DTO para resposta de custo variável
 */
public record CustoVariavelResponseDTO(
        Long id,
        String nome,
        BigDecimal valor,
        LocalDate dataLancamento,
        String descricao,
        StatusCusto status,
        LocalDate createdAt,
        LocalDate updatedAt) {
}
