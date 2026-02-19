package com.madeirart.appMadeirart.modules.orcamento.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Entidade que armazena o histórico de alterações de orçamentos
 */
@Entity
@Table(name = "orcamentos_auditoria")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrcamentoAuditoria {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "orcamento_id", nullable = false)
    private Long orcamentoId;

    @Column(name = "snapshot_json", columnDefinition = "TEXT", nullable = false)
    private String snapshotJson;

    @Column(name = "data_alteracao", nullable = false)
    private LocalDateTime dataAlteracao;

    @Column(name = "descricao_alteracao", length = 500)
    private String descricaoAlteracao;

    @PrePersist
    protected void onCreate() {
        if (dataAlteracao == null) {
            dataAlteracao = LocalDateTime.now();
        }
    }
}
