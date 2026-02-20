package com.madeirart.appMadeirart.modules.dashboard.dto;

import java.time.LocalDate;

/**
 * DTO que representa o resumo de um orçamento próximo da entrega
 */
public record OrcamentoResumoDTO(
        Long id,
        String cliente,
        String moveis,
        LocalDate previsaoEntrega,
        String status) {
}
