package com.madeirart.appMadeirart.modules.custos.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * DTO para criação/edição de custo variável
 */
public record CustoVariavelRequestDTO(
        @NotBlank(message = "Nome do custo variável é obrigatório") 
        String nome,

        @NotNull(message = "Valor do custo variável é obrigatório")
        @Positive(message = "Valor do custo variável deve ser positivo") 
        BigDecimal valor,

        @NotNull(message = "Data de lançamento é obrigatória") 
        LocalDate dataLancamento,

        String descricao,

        @Positive(message = "Quantidade de parcelas deve ser positiva")
        Integer quantidadeParcelas) {
}
