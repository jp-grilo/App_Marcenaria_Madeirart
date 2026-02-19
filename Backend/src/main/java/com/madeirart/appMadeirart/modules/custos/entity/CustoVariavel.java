package com.madeirart.appMadeirart.modules.custos.entity;

import com.madeirart.appMadeirart.shared.enums.StatusCusto;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Entidade que representa um custo variável pontual
 */
@Entity
@Table(name = "custos_variaveis")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CustoVariavel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Nome do custo variável é obrigatório")
    @Column(nullable = false, length = 200)
    private String nome;

    @NotNull(message = "Valor do custo variável é obrigatório")
    @Positive(message = "Valor do custo variável deve ser positivo")
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal valor;

    @NotNull(message = "Data de lançamento é obrigatória")
    @Column(name = "data_lancamento", nullable = false)
    private LocalDate dataLancamento;

    @Column(length = 500)
    private String descricao;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private StatusCusto status;

    @Column(name = "created_at", updatable = false)
    private LocalDate createdAt;

    @Column(name = "updated_at")
    private LocalDate updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDate.now();
        updatedAt = LocalDate.now();
        if (status == null) {
            status = StatusCusto.PENDENTE;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDate.now();
    }
}
