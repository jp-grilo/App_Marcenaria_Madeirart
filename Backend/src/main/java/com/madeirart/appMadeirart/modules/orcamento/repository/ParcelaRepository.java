package com.madeirart.appMadeirart.modules.orcamento.repository;

import com.madeirart.appMadeirart.modules.orcamento.entity.Parcela;
import com.madeirart.appMadeirart.shared.enums.StatusParcela;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
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

    /**
     * Busca todas as parcelas com status específico e data de vencimento anterior a
     * uma data
     */
    List<Parcela> findByStatusAndDataVencimentoBefore(StatusParcela status, LocalDate data);
}
