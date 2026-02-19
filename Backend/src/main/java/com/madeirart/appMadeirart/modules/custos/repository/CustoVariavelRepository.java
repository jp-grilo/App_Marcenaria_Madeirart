package com.madeirart.appMadeirart.modules.custos.repository;

import com.madeirart.appMadeirart.modules.custos.entity.CustoVariavel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

/**
 * Repository para acesso aos dados de custos variáveis
 */
@Repository
public interface CustoVariavelRepository extends JpaRepository<CustoVariavel, Long> {

    /**
     * Busca todos os custos variáveis ordenados por data de lançamento (mais recentes primeiro)
     */
    List<CustoVariavel> findAllByOrderByDataLancamentoDesc();

    /**
     * Busca custos variáveis em um período específico
     */
    List<CustoVariavel> findByDataLancamentoBetween(LocalDate dataInicio, LocalDate dataFim);
}
