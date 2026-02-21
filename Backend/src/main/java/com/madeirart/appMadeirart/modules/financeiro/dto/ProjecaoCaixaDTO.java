package com.madeirart.appMadeirart.modules.financeiro.dto;

import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * DTO para resposta completa da projeção de caixa
 */
@Builder
public record ProjecaoCaixaDTO(
        BigDecimal saldoAtual,
        LocalDateTime dataCalculo,
        BigDecimal saldoInicialCadastrado,
        List<MesProjecaoDTO> mesesProjetados) {
}
