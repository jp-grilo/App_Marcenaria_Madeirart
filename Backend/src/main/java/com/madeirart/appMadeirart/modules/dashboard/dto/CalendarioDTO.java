package com.madeirart.appMadeirart.modules.dashboard.dto;

import java.util.Map;

/**
 * DTO que representa o calendário financeiro mensal
 */
public record CalendarioDTO(
        Integer ano,
        Integer mes,
        Map<Integer, DiaDadosDTO> dias) {
}
