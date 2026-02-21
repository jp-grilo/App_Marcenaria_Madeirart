package com.madeirart.appMadeirart.modules.financeiro.repository;

import com.madeirart.appMadeirart.modules.financeiro.entity.SaldoInicial;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository para a entidade SaldoInicial
 */
@Repository
public interface SaldoInicialRepository extends JpaRepository<SaldoInicial, Long> {

    /**
     * Busca o primeiro registro de saldo inicial (deve existir apenas um)
     */
    @Query("SELECT s FROM SaldoInicial s ORDER BY s.id ASC")
    Optional<SaldoInicial> findFirst();
}
