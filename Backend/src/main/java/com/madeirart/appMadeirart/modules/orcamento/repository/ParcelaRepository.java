package com.madeirart.appMadeirart.modules.orcamento.repository;

import com.madeirart.appMadeirart.modules.orcamento.entity.Parcela;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository para acesso aos dados de parcelas
 */
@Repository
public interface ParcelaRepository extends JpaRepository<Parcela, Long> {

    /**
     * Busca todas as parcelas de um orçamento ordenadas por número
     */
    List<Parcela> findByOrcamentoIdOrderByNumeroParcela(Long orcamentoId);
}
