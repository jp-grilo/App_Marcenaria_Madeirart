package com.madeirart.appMadeirart.modules.orcamento.dto;

import lombok.Builder;

import java.time.LocalDateTime;

/**
 * DTO para resposta de dados de auditoria
 */
@Builder
public record OrcamentoAuditoriaDTO(
    Long id,
    Long orcamentoId,
    String snapshotJson,
    LocalDateTime dataAlteracao,
    String descricaoAlteracao
) {}
