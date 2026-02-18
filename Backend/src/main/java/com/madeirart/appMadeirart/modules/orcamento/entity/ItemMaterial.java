package com.madeirart.appMadeirart.modules.orcamento.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * Entidade que representa um item de material do orçamento
 */
@Entity
@Table(name = "itens_material")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ItemMaterial {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "Quantidade é obrigatória")
    @Positive(message = "Quantidade deve ser maior que zero")
    @Column(nullable = false)
    private BigDecimal quantidade;

    @NotBlank(message = "Descrição é obrigatória")
    @Column(nullable = false, length = 500)
    private String descricao;

    @NotNull(message = "Valor unitário é obrigatório")
    @Positive(message = "Valor unitário deve ser maior que zero")
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal valorUnitario;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "orcamento_id", nullable = false)
    private Orcamento orcamento;

    /**
     * Calcula o subtotal do item (quantidade x valor unitário)
     */
    public BigDecimal calcularSubtotal() {
        if (quantidade == null || valorUnitario == null) {
            return BigDecimal.ZERO;
        }
        return quantidade.multiply(valorUnitario);
    }
}
