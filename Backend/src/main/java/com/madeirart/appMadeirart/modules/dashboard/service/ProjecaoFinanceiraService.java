package com.madeirart.appMadeirart.modules.dashboard.service;

import com.madeirart.appMadeirart.modules.custos.entity.CustoFixo;
import com.madeirart.appMadeirart.modules.custos.entity.CustoVariavel;
import com.madeirart.appMadeirart.modules.custos.repository.CustoFixoRepository;
import com.madeirart.appMadeirart.modules.custos.repository.CustoVariavelRepository;
import com.madeirart.appMadeirart.modules.dashboard.dto.ProjecaoFinanceiraDTO;
import com.madeirart.appMadeirart.modules.orcamento.entity.Parcela;
import com.madeirart.appMadeirart.modules.orcamento.repository.ParcelaRepository;
import com.madeirart.appMadeirart.shared.enums.StatusParcela;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.TextStyle;
import java.util.List;
import java.util.Locale;

/**
 * Service para cálculo de projeções financeiras
 */
@Service
@RequiredArgsConstructor
public class ProjecaoFinanceiraService {

    private final ParcelaRepository parcelaRepository;
    private final CustoFixoRepository custoFixoRepository;
    private final CustoVariavelRepository custoVariavelRepository;

    /**
     * Calcula a projeção financeira para um mês específico
     */
    public ProjecaoFinanceiraDTO calcularProjecaoMensal(int mes, int ano) {
        YearMonth yearMonth = YearMonth.of(ano, mes);
        LocalDate inicioDomes = yearMonth.atDay(1);
        LocalDate fimDoMes = yearMonth.atEndOfMonth();

        // Calcular receita prevista (parcelas pendentes do mês)
        BigDecimal receitaPrevista = calcularReceitaPrevista(inicioDomes, fimDoMes);

        // Calcular despesa prevista (custos fixos e variáveis do mês)
        BigDecimal despesaPrevista = calcularDespesaPrevista(inicioDomes, fimDoMes, mes);

        // Calcular saldo projetado
        BigDecimal saldoProjetado = receitaPrevista.subtract(despesaPrevista);

        // Formatar mês de referência
        Locale localeBR = Locale.forLanguageTag("pt-BR");
        String mesReferencia = yearMonth.getMonth()
                .getDisplayName(TextStyle.FULL, localeBR)
                + " de " + ano;

        return new ProjecaoFinanceiraDTO(
                receitaPrevista,
                despesaPrevista,
                saldoProjetado,
                mesReferencia
        );
    }

    /**
     * Calcula a receita prevista baseada em parcelas PENDENTES do período
     */
    private BigDecimal calcularReceitaPrevista(LocalDate inicio, LocalDate fim) {
        List<Parcela> parcelas = parcelaRepository.findAll().stream()
                .filter(p -> p.getStatus() == StatusParcela.PENDENTE)
                .filter(p -> !p.getDataVencimento().isBefore(inicio) && !p.getDataVencimento().isAfter(fim))
                .toList();

        return parcelas.stream()
                .map(Parcela::getValor)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    /**
     * Calcula a despesa prevista baseada em custos fixos e variáveis do período
     */
    private BigDecimal calcularDespesaPrevista(LocalDate inicio, LocalDate fim, int mes) {
        BigDecimal despesaCustosFixos = calcularDespesaCustosFixos(mes);
        BigDecimal despesaCustosVariaveis = calcularDespesaCustosVariaveis(inicio, fim);

        return despesaCustosFixos.add(despesaCustosVariaveis);
    }

    /**
     * Calcula a despesa com custos fixos do mês
     */
    private BigDecimal calcularDespesaCustosFixos(int mes) {
        List<CustoFixo> custosFixos = custoFixoRepository.findAll().stream()
                .filter(CustoFixo::getAtivo)
                .toList();

        return custosFixos.stream()
                .map(CustoFixo::getValor)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    /**
     * Calcula a despesa com custos variáveis do período
     */
    private BigDecimal calcularDespesaCustosVariaveis(LocalDate inicio, LocalDate fim) {
        List<CustoVariavel> custosVariaveis = custoVariavelRepository.findAll().stream()
                .filter(c -> !c.getDataLancamento().isBefore(inicio) && !c.getDataLancamento().isAfter(fim))
                .toList();

        return custosVariaveis.stream()
                .map(CustoVariavel::getValor)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
