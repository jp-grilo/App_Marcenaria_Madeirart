package com.madeirart.appMadeirart.modules.orcamento.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

/**
 * DTO para representar um item de material
 */
public record ItemMaterialDTO(
    Long id,
    
    @NotNull(message = "Quantidade é obrigatória")
    @Positive(message = "Quantidade deve ser maior que zero")
    BigDecimal quantidade,
    
    @NotNull(message = "Descrição é obrigatória")
    String descricao,
    
    @NotNull(message = "Valor unitário é obrigatório")
    @Positive(message = "Valor unitário deve ser maior que zero")
    BigDecimal valorUnitario,
    
    BigDecimal subtotal
) {}
