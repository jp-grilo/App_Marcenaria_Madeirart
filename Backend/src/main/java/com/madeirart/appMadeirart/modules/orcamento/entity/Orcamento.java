package com.madeirart.appMadeirart.modules.orcamento.entity;

import com.madeirart.appMadeirart.shared.enums.StatusOrcamento;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Entidade que representa um orçamento
 */
@Entity
@Table(name = "orcamentos")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Orcamento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Nome do cliente é obrigatório")
    @Column(nullable = false, length = 200)
    private String cliente;

    @NotBlank(message = "Descrição dos móveis é obrigatória")
    @Column(nullable = false, length = 1000)
    private String moveis;

    @NotNull(message = "Data é obrigatória")
    @Column(nullable = false)
    private LocalDate data;

    @Column(name = "previsao_entrega")
    private LocalDate previsaoEntrega;

    @NotNull(message = "Fator de mão de obra é obrigatório")
    @PositiveOrZero(message = "Fator de mão de obra não pode ser negativo")
    @Column(nullable = false, precision = 5, scale = 2, name = "fator_mao_obra")
    private BigDecimal fatorMaoDeObra;

    @PositiveOrZero(message = "Custos extras não podem ser negativos")
    @Column(precision = 10, scale = 2, name = "custos_extras")
    private BigDecimal custosExtras;

    @PositiveOrZero(message = "CPC não pode ser negativo")
    @Column(precision = 10, scale = 2)
    private BigDecimal cpc;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private StatusOrcamento status;

    @Builder.Default
    @OneToMany(mappedBy = "orcamento", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ItemMaterial> itens = new ArrayList<>();

    @Column(name = "created_at", updatable = false)
    private LocalDate createdAt;

    @Column(name = "updated_at")
    private LocalDate updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDate.now();
        updatedAt = LocalDate.now();
        if (status == null) {
            status = StatusOrcamento.AGUARDANDO;
        }
        if (custosExtras == null) {
            custosExtras = BigDecimal.ZERO;
        }
        if (cpc == null) {
            cpc = BigDecimal.ZERO;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDate.now();
    }

    /**
     * Adiciona um item ao orçamento mantendo a relação bidirecional
     */
    public void adicionarItem(ItemMaterial item) {
        itens.add(item);
        item.setOrcamento(this);
    }

    /**
     * Remove um item do orçamento
     */
    public void removerItem(ItemMaterial item) {
        itens.remove(item);
        item.setOrcamento(null);
    }

    /**
     * Calcula o subtotal de todos os materiais
     */
    public BigDecimal calcularSubtotalMateriais() {
        return itens.stream()
                .map(ItemMaterial::calcularSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    /**
     * Calcula o valor da mão de obra (subtotal × fator)
     */
    public BigDecimal calcularValorMaoDeObra() {
        BigDecimal subtotal = calcularSubtotalMateriais();
        return subtotal.multiply(fatorMaoDeObra);
    }

    /**
     * Calcula o valor total do orçamento
     * Total = Subtotal Materiais + Mão de Obra + Custos Extras + CPC
     */
    public BigDecimal calcularValorTotal() {
        BigDecimal subtotal = calcularSubtotalMateriais();
        BigDecimal maoDeObra = calcularValorMaoDeObra();
        BigDecimal extras = custosExtras != null ? custosExtras : BigDecimal.ZERO;
        BigDecimal cpcValor = cpc != null ? cpc : BigDecimal.ZERO;
        
        return subtotal.add(maoDeObra).add(extras).add(cpcValor);
    }
}
