package com.madeirart.appMadeirart.modules.custos.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

/**
 * DTO para criação/edição de custo fixo
 */
public record CustoFixoRequestDTO(
        @NotBlank(message = "Nome do custo fixo é obrigatório") 
        String nome,

        @NotNull(message = "Valor do custo fixo é obrigatório") 
        @Positive(message = "Valor do custo fixo deve ser positivo") 
        BigDecimal valor,

        @NotNull(message = "Dia de vencimento é obrigatório") 
        @Min(value = 1, message = "Dia de vencimento deve ser entre 1 e 31") 
        @Max(value = 31, message = "Dia de vencimento deve ser entre 1 e 31") 
        Integer diaVencimento,

        String descricao) {
}
