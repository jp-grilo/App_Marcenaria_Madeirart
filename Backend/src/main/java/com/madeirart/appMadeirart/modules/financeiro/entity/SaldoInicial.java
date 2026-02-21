package com.madeirart.appMadeirart.modules.financeiro.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Entidade que representa o saldo inicial do sistema
 * Só deve existir um único registro
 */
@Entity
@Table(name = "saldo_inicial")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SaldoInicial {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "Valor do saldo inicial é obrigatório")
    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal valor;

    @Column(length = 500)
    private String observacao;

    @Column(name = "data_registro", nullable = false, updatable = false)
    private LocalDateTime dataRegistro;

    @Column(name = "data_atualizacao")
    private LocalDateTime dataAtualizacao;

    @PrePersist
    protected void onCreate() {
        dataRegistro = LocalDateTime.now();
        dataAtualizacao = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        dataAtualizacao = LocalDateTime.now();
    }
}
