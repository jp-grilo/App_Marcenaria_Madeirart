package com.madeirart.appMadeirart.modules.orcamento.repository;

import com.madeirart.appMadeirart.modules.orcamento.entity.OrcamentoAuditoria;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository para acesso aos dados de auditoria de orçamentos
 */
@Repository
public interface OrcamentoAuditoriaRepository extends JpaRepository<OrcamentoAuditoria, Long> {

    /**
     * Busca todo o histórico de alterações de um orçamento
     * Ordenado por data de alteração (mais recente primeiro)
     */
    List<OrcamentoAuditoria> findByOrcamentoIdOrderByDataAlteracaoDesc(Long orcamentoId);
}
