package com.madeirart.appMadeirart.modules.custos.repository;

import com.madeirart.appMadeirart.modules.custos.entity.CustoFixo;
import com.madeirart.appMadeirart.shared.enums.StatusCusto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository para acesso aos dados de custos fixos
 */
@Repository
public interface CustoFixoRepository extends JpaRepository<CustoFixo, Long> {

    /**
     * Busca todos os custos fixos ativos ordenados por nome
     */
    List<CustoFixo> findByAtivoTrueOrderByNome();

    /**
     * Busca todos os custos fixos ordenados por nome
     */
    List<CustoFixo> findAllByOrderByNome();

    /**
     * Busca custos fixos ativos que vencem entre dois dias do mês
     */
    List<CustoFixo> findByAtivoTrueAndDiaVencimentoBetween(Integer diaInicio, Integer diaFim);

    /**
     * Busca todos os custos fixos ativos ordenados por dia de vencimento
     */
    List<CustoFixo> findByAtivoTrueOrderByDiaVencimento();

    /**
     * Busca custos fixos por status
     */
    List<CustoFixo> findByStatus(StatusCusto status);
}
