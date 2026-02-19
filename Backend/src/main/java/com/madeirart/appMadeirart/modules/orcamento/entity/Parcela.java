package com.madeirart.appMadeirart.modules.orcamento.entity;

import com.madeirart.appMadeirart.shared.enums.StatusParcela;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Entidade que representa uma parcela de pagamento de um orçamento
 */
@Entity
@Table(name = "parcelas")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Parcela {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "orcamento_id", nullable = false)
    private Orcamento orcamento;

    @NotNull(message = "Número da parcela é obrigatório")
    @Positive(message = "Número da parcela deve ser positivo")
    @Column(name = "numero_parcela", nullable = false)
    private Integer numeroParcela;

    @NotNull(message = "Valor da parcela é obrigatório")
    @Positive(message = "Valor da parcela deve ser positivo")
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal valor;

    @NotNull(message = "Data de vencimento é obrigatória")
    @Column(name = "data_vencimento", nullable = false)
    private LocalDate dataVencimento;

    @Column(name = "data_pagamento")
    private LocalDate dataPagamento;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private StatusParcela status;

    @Column(name = "created_at", updatable = false)
    private LocalDate createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDate.now();
        if (status == null) {
            status = StatusParcela.PENDENTE;
        }
    }
}
