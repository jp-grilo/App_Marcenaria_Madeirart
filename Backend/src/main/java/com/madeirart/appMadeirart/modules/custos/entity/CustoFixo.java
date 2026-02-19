package com.madeirart.appMadeirart.modules.custos.entity;

import com.madeirart.appMadeirart.shared.enums.StatusCusto;
import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
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
 * Entidade que representa um custo fixo recorrente
 */
@Entity
@Table(name = "custos_fixos")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CustoFixo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Nome do custo fixo é obrigatório")
    @Column(nullable = false, length = 200)
    private String nome;

    @NotNull(message = "Valor do custo fixo é obrigatório")
    @Positive(message = "Valor do custo fixo deve ser positivo")
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal valor;

    @NotNull(message = "Dia de vencimento é obrigatório")
    @Min(value = 1, message = "Dia de vencimento deve ser entre 1 e 31")
    @Max(value = 31, message = "Dia de vencimento deve ser entre 1 e 31")
    @Column(name = "dia_vencimento", nullable = false)
    private Integer diaVencimento;

    @Column(length = 500)
    private String descricao;

    @Builder.Default
    @Column(nullable = false)
    private Boolean ativo = true;

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
        if (ativo == null) {
            ativo = true;
        }
        if (status == null) {
            status = StatusCusto.PENDENTE;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDate.now();
    }
}
