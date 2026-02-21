package com.madeirart.appMadeirart.modules.financeiro.dto;

import com.madeirart.appMadeirart.shared.enums.OrigemTransacao;
import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * DTO para representar um item individual da projeção (entrada ou saída)
 */
@Builder
public record ItemProjecaoDTO(
        Long id,
        String descricao,
        BigDecimal valor,
        LocalDate data,
        OrigemTransacao origem,
        String status) {
}
