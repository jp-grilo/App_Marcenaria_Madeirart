package com.madeirart.appMadeirart.modules.financeiro.dto;

import lombok.Builder;

import java.math.BigDecimal;
import java.util.List;

/**
 * DTO para representar a projeção de um mês específico
 */
@Builder
public record MesProjecaoDTO(
        Integer mesReferencia,
        Integer anoReferencia,
        BigDecimal saldoInicial,
        BigDecimal totalEntradasPrevistas,
        BigDecimal totalSaidasPrevistas,
        BigDecimal saldoFinalProjetado,
        List<ItemProjecaoDTO> detalhesEntradas,
        List<ItemProjecaoDTO> detalhesSaidas) {
}
