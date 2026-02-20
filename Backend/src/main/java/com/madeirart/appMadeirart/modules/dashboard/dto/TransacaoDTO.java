package com.madeirart.appMadeirart.modules.dashboard.dto;

import com.madeirart.appMadeirart.shared.enums.OrigemTransacao;
import com.madeirart.appMadeirart.shared.enums.TipoTransacao;

import java.math.BigDecimal;

/**
 * DTO que representa uma transação financeira (entrada ou saída)
 */
public record TransacaoDTO(
        Long id,
        TipoTransacao tipo,
        String descricao,
        BigDecimal valor,
        OrigemTransacao origem,
        String status) {
}
