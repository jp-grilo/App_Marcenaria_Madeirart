package com.madeirart.appMadeirart.modules.orcamento.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * DTO para criação de um novo orçamento
 */
public record OrcamentoRequestDTO(
    @NotBlank(message = "Nome do cliente é obrigatório")
    String cliente,
    
    @NotBlank(message = "Descrição dos móveis é obrigatória")
    String moveis,
    
    @NotNull(message = "Data é obrigatória")
    LocalDate data,
    
    LocalDate previsaoEntrega,
    
    @NotNull(message = "Fator de mão de obra é obrigatório")
    @PositiveOrZero(message = "Fator de mão de obra não pode ser negativo")
    BigDecimal fatorMaoDeObra,
    
    @PositiveOrZero(message = "Custos extras não podem ser negativos")
    BigDecimal custosExtras,
    
    @PositiveOrZero(message = "CPC não pode ser negativo")
    BigDecimal cpc,
    
    @NotEmpty(message = "O orçamento deve ter pelo menos um item")
    @Valid
    List<ItemMaterialDTO> itens
) {}
