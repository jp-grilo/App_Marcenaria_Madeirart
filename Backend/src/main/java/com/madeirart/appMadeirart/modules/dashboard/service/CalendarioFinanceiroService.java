package com.madeirart.appMadeirart.modules.dashboard.service;

import com.madeirart.appMadeirart.modules.custos.entity.CustoFixo;
import com.madeirart.appMadeirart.modules.custos.entity.CustoVariavel;
import com.madeirart.appMadeirart.modules.custos.repository.CustoFixoRepository;
import com.madeirart.appMadeirart.modules.custos.repository.CustoVariavelRepository;
import com.madeirart.appMadeirart.modules.dashboard.dto.CalendarioDTO;
import com.madeirart.appMadeirart.modules.dashboard.dto.DiaDadosDTO;
import com.madeirart.appMadeirart.modules.dashboard.dto.TransacaoDTO;
import com.madeirart.appMadeirart.modules.orcamento.entity.Parcela;
import com.madeirart.appMadeirart.modules.orcamento.repository.ParcelaRepository;
import com.madeirart.appMadeirart.shared.enums.OrigemTransacao;
import com.madeirart.appMadeirart.shared.enums.TipoTransacao;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Service para geração do calendário financeiro
 */
@Service
@RequiredArgsConstructor
public class CalendarioFinanceiroService {

    private final ParcelaRepository parcelaRepository;
    private final CustoFixoRepository custoFixoRepository;
    private final CustoVariavelRepository custoVariavelRepository;

    /**
     * Obtém o calendário financeiro de um mês específico
     */
    public CalendarioDTO getCalendarioMensal(int mes, int ano) {
        YearMonth yearMonth = YearMonth.of(ano, mes);
        LocalDate inicioDomes = yearMonth.atDay(1);
        LocalDate fimDoMes = yearMonth.atEndOfMonth();

        Map<Integer, DiaDadosDTO> diasMap = new HashMap<>();

        // Processar parcelas (entradas)
        processarParcelas(diasMap, inicioDomes, fimDoMes);

        // Processar custos fixos (saídas)
        processarCustosFixos(diasMap, mes);

        // Processar custos variáveis (saídas)
        processarCustosVariaveis(diasMap, inicioDomes, fimDoMes);

        return new CalendarioDTO(ano, mes, diasMap);
    }

    /**
     * Processa as parcelas e adiciona como entradas no calendário
     */
    private void processarParcelas(Map<Integer, DiaDadosDTO> diasMap, LocalDate inicio, LocalDate fim) {
        List<Parcela> parcelas = parcelaRepository.findAll().stream()
                .filter(p -> {
                    LocalDate dataReferencia = p.getDataPagamento() != null
                            ? p.getDataPagamento()
                            : p.getDataVencimento();
                    return !dataReferencia.isBefore(inicio) && !dataReferencia.isAfter(fim);
                })
                .toList();

        for (Parcela parcela : parcelas) {
            LocalDate dataReferencia = parcela.getDataPagamento() != null
                    ? parcela.getDataPagamento()
                    : parcela.getDataVencimento();
            int dia = dataReferencia.getDayOfMonth();

            DiaDadosDTO diaDados = diasMap.getOrDefault(dia, new DiaDadosDTO(dia));

            TransacaoDTO transacao = new TransacaoDTO(
                    parcela.getId(),
                    TipoTransacao.ENTRADA,
                    "Parcela " + parcela.getNumeroParcela() + " - " +
                            parcela.getOrcamento().getCliente(),
                    parcela.getValor(),
                    OrigemTransacao.PARCELA,
                    parcela.getStatus().getDescricao());

            // Criar nova lista com a transação adicionada
            List<TransacaoDTO> novasEntradas = new java.util.ArrayList<>(diaDados.entradas());
            novasEntradas.add(transacao);

            DiaDadosDTO novoDia = new DiaDadosDTO(
                    dia,
                    true,
                    diaDados.temSaidas(),
                    novasEntradas,
                    diaDados.saidas());
            diasMap.put(dia, novoDia);
        }
    }

    /**
     * Processa os custos fixos e adiciona como saídas no calendário
     */
    private void processarCustosFixos(Map<Integer, DiaDadosDTO> diasMap, int mes) {
        List<CustoFixo> custosFixos = custoFixoRepository.findAll().stream()
                .filter(CustoFixo::getAtivo)
                .toList();

        for (CustoFixo custo : custosFixos) {
            int dia = custo.getDiaVencimento();

            DiaDadosDTO diaDados = diasMap.getOrDefault(dia, new DiaDadosDTO(dia));

            TransacaoDTO transacao = new TransacaoDTO(
                    custo.getId(),
                    TipoTransacao.SAIDA,
                    custo.getNome(),
                    custo.getValor(),
                    OrigemTransacao.CUSTO_FIXO,
                    custo.getStatus().getDescricao());

            // Criar nova lista com a transação adicionada
            List<TransacaoDTO> novasSaidas = new java.util.ArrayList<>(diaDados.saidas());
            novasSaidas.add(transacao);

            DiaDadosDTO novoDia = new DiaDadosDTO(
                    dia,
                    diaDados.temEntradas(),
                    true,
                    diaDados.entradas(),
                    novasSaidas);
            diasMap.put(dia, novoDia);
        }
    }

    /**
     * Processa os custos variáveis e adiciona como saídas no calendário
     */
    private void processarCustosVariaveis(Map<Integer, DiaDadosDTO> diasMap, LocalDate inicio, LocalDate fim) {
        List<CustoVariavel> custosVariaveis = custoVariavelRepository.findAll().stream()
                .filter(c -> !c.getDataLancamento().isBefore(inicio) && !c.getDataLancamento().isAfter(fim))
                .toList();

        for (CustoVariavel custo : custosVariaveis) {
            int dia = custo.getDataLancamento().getDayOfMonth();

            DiaDadosDTO diaDados = diasMap.getOrDefault(dia, new DiaDadosDTO(dia));

            String descricao = custo.getNome();
            if (Boolean.TRUE.equals(custo.getParcelado())) {
                descricao += " (Parcela " + custo.getNumeroParcela() + "/" + custo.getTotalParcelas() + ")";
            }

            TransacaoDTO transacao = new TransacaoDTO(
                    custo.getId(),
                    TipoTransacao.SAIDA,
                    descricao,
                    custo.getValor(),
                    OrigemTransacao.CUSTO_VARIAVEL,
                    custo.getStatus().getDescricao());

            // Criar nova lista com a transação adicionada
            List<TransacaoDTO> novasSaidas = new java.util.ArrayList<>(diaDados.saidas());
            novasSaidas.add(transacao);

            DiaDadosDTO novoDia = new DiaDadosDTO(
                    dia,
                    diaDados.temEntradas(),
                    true,
                    diaDados.entradas(),
                    novasSaidas);
            diasMap.put(dia, novoDia);
        }
    }
}
