package com.madeirart.appMadeirart.modules.orcamento.dto;

import lombok.Builder;

import java.math.BigDecimal;
import java.util.List;

/**
 * DTO que representa o status de recebimento de um orçamento.
 * Contém informações sobre o progresso de pagamento das parcelas.
 */
@Builder
public record StatusRecebimentoDTO(
        BigDecimal valorTotalOrcamento,
        BigDecimal totalJaConfirmado,
        BigDecimal totalPendente,
        Double percentualRecebido,
        List<ParcelaResponseDTO> parcelas) {
}
