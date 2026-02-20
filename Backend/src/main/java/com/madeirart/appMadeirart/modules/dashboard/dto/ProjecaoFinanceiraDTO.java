package com.madeirart.appMadeirart.modules.dashboard.dto;

import java.math.BigDecimal;

/**
 * DTO que representa a projeção financeira mensal
 */
public record ProjecaoFinanceiraDTO(
        BigDecimal receitaPrevista,
        BigDecimal despesaPrevista,
        BigDecimal saldoProjetado,
        String mesReferencia) {
}
