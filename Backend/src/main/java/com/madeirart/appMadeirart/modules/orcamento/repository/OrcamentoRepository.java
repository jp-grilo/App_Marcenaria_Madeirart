package com.madeirart.appMadeirart.modules.orcamento.repository;

import com.madeirart.appMadeirart.modules.orcamento.entity.Orcamento;
import com.madeirart.appMadeirart.shared.enums.StatusOrcamento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository para a entidade Orcamento
 */
@Repository
public interface OrcamentoRepository extends JpaRepository<Orcamento, Long> {

    /**
     * Busca orçamentos por status
     */
    List<Orcamento> findByStatus(StatusOrcamento status);

    /**
     * Busca orçamentos por cliente (case insensitive)
     */
    List<Orcamento> findByClienteContainingIgnoreCase(String cliente);
}
