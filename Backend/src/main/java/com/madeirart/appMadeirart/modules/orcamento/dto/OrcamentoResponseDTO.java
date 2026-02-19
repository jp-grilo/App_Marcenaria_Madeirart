package com.madeirart.appMadeirart.modules.orcamento.dto;

import com.madeirart.appMadeirart.shared.enums.StatusOrcamento;
import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * DTO para resposta de um orçamento
 */
@Builder
public record OrcamentoResponseDTO(
    Long id,
    String cliente,
    String moveis,
    LocalDate data,
    LocalDate previsaoEntrega,
    BigDecimal fatorMaoDeObra,
    BigDecimal custosExtras,
    BigDecimal cpc,
    StatusOrcamento status,
    List<ItemMaterialDTO> itens,
    
    // Campos calculados
    BigDecimal subtotalMateriais,
    BigDecimal valorMaoDeObra,
    BigDecimal valorTotal,
    
    LocalDate createdAt,
    LocalDate updatedAt
) {}
