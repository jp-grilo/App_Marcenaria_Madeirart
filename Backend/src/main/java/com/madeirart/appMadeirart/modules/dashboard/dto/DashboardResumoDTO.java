package com.madeirart.appMadeirart.modules.dashboard.dto;

import java.util.List;

/**
 * DTO que representa o resumo de orçamentos no dashboard
 */
public record DashboardResumoDTO(
        Long totalOrcamentosAtivos,
        Long totalEmProducao,
        List<OrcamentoResumoDTO> orcamentosProximosEntrega) {
}
