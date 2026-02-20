package com.madeirart.appMadeirart.modules.dashboard.dto;

import java.util.ArrayList;
import java.util.List;

/**
 * DTO que representa os dados de um dia específico no calendário
 */
public record DiaDadosDTO(
        Integer dia,
        Boolean temEntradas,
        Boolean temSaidas,
        List<TransacaoDTO> entradas,
        List<TransacaoDTO> saidas) {
    public DiaDadosDTO(Integer dia) {
        this(dia, false, false, new ArrayList<>(), new ArrayList<>());
    }
}
