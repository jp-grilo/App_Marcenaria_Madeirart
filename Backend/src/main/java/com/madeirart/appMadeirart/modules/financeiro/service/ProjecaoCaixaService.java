package com.madeirart.appMadeirart.modules.financeiro.service;

import com.madeirart.appMadeirart.modules.custos.entity.CustoFixo;
import com.madeirart.appMadeirart.modules.custos.entity.CustoVariavel;
import com.madeirart.appMadeirart.modules.custos.repository.CustoFixoRepository;
import com.madeirart.appMadeirart.modules.custos.repository.CustoVariavelRepository;
import com.madeirart.appMadeirart.modules.financeiro.dto.*;
import com.madeirart.appMadeirart.modules.financeiro.entity.SaldoInicial;
import com.madeirart.appMadeirart.modules.financeiro.repository.SaldoInicialRepository;
import com.madeirart.appMadeirart.modules.orcamento.entity.Parcela;
import com.madeirart.appMadeirart.modules.orcamento.repository.ParcelaRepository;
import com.madeirart.appMadeirart.shared.enums.OrigemTransacao;
import com.madeirart.appMadeirart.shared.enums.StatusParcela;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Service para gerenciamento da projeção de caixa
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ProjecaoCaixaService {

    private final SaldoInicialRepository saldoInicialRepository;
    private final ParcelaRepository parcelaRepository;
    private final CustoFixoRepository custoFixoRepository;
    private final CustoVariavelRepository custoVariavelRepository;

    /**
     * Busca o saldo inicial cadastrado
     */
    private BigDecimal buscarSaldoInicial() {
        return saldoInicialRepository.findFirst()
                .map(SaldoInicial::getValor)
                .orElse(BigDecimal.ZERO);
    }

    /**
     * Calcula o saldo atual acumulado
     * Fórmula: Saldo Inicial + Parcelas PAGAS - Custos até hoje
     */
    private BigDecimal calcularSaldoAtual(BigDecimal saldoInicial) {
        LocalDate hoje = LocalDate.now();

        // Somar todas as parcelas já pagas
        BigDecimal totalParcelasPagas = parcelaRepository.findAll().stream()
                .filter(p -> p.getStatus() == StatusParcela.PAGO)
                .map(Parcela::getValor)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // Somar todos os custos variáveis até hoje
        BigDecimal totalCustosVariaveis = custoVariavelRepository.findAll().stream()
                .filter(c -> !c.getDataLancamento().isAfter(hoje))
                .map(CustoVariavel::getValor)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // Calcular custos fixos de todos os meses passados até hoje
        BigDecimal totalCustosFixosPassados = calcularCustosFixosAteHoje(hoje);

        // Fórmula final
        BigDecimal saldoAtual = saldoInicial
                .add(totalParcelasPagas)
                .subtract(totalCustosVariaveis)
                .subtract(totalCustosFixosPassados);

        log.info("Saldo calculado - Inicial: {}, Pagas: {}, Custos Var: {}, Custos Fixos: {}, Saldo Atual: {}",
                saldoInicial, totalParcelasPagas, totalCustosVariaveis, totalCustosFixosPassados, saldoAtual);

        return saldoAtual;
    }

    /**
     * Projeta os próximos N meses
     */
    private List<MesProjecaoDTO> projetarProximosMeses(int quantidadeMeses, BigDecimal saldoInicialProjecao) {
        List<MesProjecaoDTO> meses = new ArrayList<>();
        LocalDate hoje = LocalDate.now();
        BigDecimal saldoAcumulado = saldoInicialProjecao;

        // Começar do próximo mês
        YearMonth mesInicio = YearMonth.from(hoje).plusMonths(1);

        for (int i = 0; i < quantidadeMeses; i++) {
            YearMonth mesProjetado = mesInicio.plusMonths(i);

            // Buscar entradas previstas (parcelas PENDENTES)
            List<ItemProjecaoDTO> entradas = buscarEntradasPrevistas(mesProjetado);
            BigDecimal totalEntradas = entradas.stream()
                    .map(ItemProjecaoDTO::valor)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            // Buscar saídas previstas (custos fixos projetados + custos variáveis
            // cadastrados)
            List<ItemProjecaoDTO> saidas = buscarSaidasPrevistas(mesProjetado);
            BigDecimal totalSaidas = saidas.stream()
                    .map(ItemProjecaoDTO::valor)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            // Calcular saldo final do mês
            BigDecimal saldoFinal = saldoAcumulado.add(totalEntradas).subtract(totalSaidas);

            MesProjecaoDTO mesDTO = MesProjecaoDTO.builder()
                    .mesReferencia(mesProjetado.getMonthValue())
                    .anoReferencia(mesProjetado.getYear())
                    .saldoInicial(saldoAcumulado)
                    .totalEntradasPrevistas(totalEntradas)
                    .totalSaidasPrevistas(totalSaidas)
                    .saldoFinalProjetado(saldoFinal)
                    .detalhesEntradas(entradas)
                    .detalhesSaidas(saidas)
                    .build();

            meses.add(mesDTO);

            // O saldo final deste mês é o saldo inicial do próximo
            saldoAcumulado = saldoFinal;
        }

        return meses;
    }

    /**
     * Retorna a projeção completa de caixa
     */
    @Transactional(readOnly = true)
    public ProjecaoCaixaDTO getProjecaoCaixa() {
        log.info("Calculando projeção de caixa");

        BigDecimal saldoInicialCadastrado = buscarSaldoInicial();

        // Calcular saldo atual
        BigDecimal saldoAtual = calcularSaldoAtual(saldoInicialCadastrado);

        // Projetar próximos 2 meses
        List<MesProjecaoDTO> mesesProjetados = projetarProximosMeses(2, saldoAtual);

        return ProjecaoCaixaDTO.builder()
                .saldoAtual(saldoAtual)
                .dataCalculo(LocalDateTime.now())
                .saldoInicialCadastrado(saldoInicialCadastrado)
                .mesesProjetados(mesesProjetados)
                .build();
    }

    /**
     * Calcula o total de custos fixos desde o início até hoje
     */
    private BigDecimal calcularCustosFixosAteHoje(LocalDate hoje) {
        List<CustoFixo> custosFixosAtivos = custoFixoRepository.findByAtivoTrueOrderByDiaVencimento();

        if (custosFixosAtivos.isEmpty()) {
            return BigDecimal.ZERO;
        }

        BigDecimal total = BigDecimal.ZERO;

        // Assumir que o sistema começou no mês/ano do primeiro custo fixo criado
        // ou usar uma data fixa como referência
        // Para simplificar, vamos calcular desde janeiro do ano passado até o mês atual
        LocalDate dataInicio = LocalDate.of(hoje.getYear() - 1, 1, 1);
        YearMonth mesAtual = YearMonth.from(hoje);

        for (YearMonth mes = YearMonth.from(dataInicio); !mes.isAfter(mesAtual); mes = mes.plusMonths(1)) {
            for (CustoFixo custo : custosFixosAtivos) {
                // Verificar se a data de criação do custo é antes ou no mês atual da iteração
                if (custo.getCreatedAt() != null && YearMonth.from(custo.getCreatedAt()).isAfter(mes)) {
                    continue; // Custo ainda não existia nesse mês
                }

                total = total.add(custo.getValor());
            }
        }

        return total;
    }

    /**
     * Busca as entradas previstas para um mês específico
     */
    private List<ItemProjecaoDTO> buscarEntradasPrevistas(YearMonth mes) {
        LocalDate primeiroDia = mes.atDay(1);
        LocalDate ultimoDia = mes.atEndOfMonth();

        return parcelaRepository.findAll().stream()
                .filter(p -> p.getStatus() == StatusParcela.PENDENTE)
                .filter(p -> !p.getDataVencimento().isBefore(primeiroDia) && !p.getDataVencimento().isAfter(ultimoDia))
                .map(p -> ItemProjecaoDTO.builder()
                        .id(p.getId())
                        .descricao("Parcela " + p.getNumeroParcela() + " - Orçamento #" + p.getOrcamento().getId())
                        .valor(p.getValor())
                        .data(p.getDataVencimento())
                        .origem(OrigemTransacao.PARCELA)
                        .status(p.getStatus().name())
                        .build())
                .collect(Collectors.toList());
    }

    /**
     * Busca as saídas previstas para um mês específico
     */
    private List<ItemProjecaoDTO> buscarSaidasPrevistas(YearMonth mes) {
        List<ItemProjecaoDTO> saidas = new ArrayList<>();
        LocalDate primeiroDia = mes.atDay(1);
        LocalDate ultimoDia = mes.atEndOfMonth();

        // Adicionar custos fixos projetados
        List<CustoFixo> custosFixos = custoFixoRepository.findByAtivoTrueOrderByDiaVencimento();
        for (CustoFixo custo : custosFixos) {
            if (custo.getCreatedAt() != null && YearMonth.from(custo.getCreatedAt()).isAfter(mes)) {
                continue;
            }

            int diaVencimento = Math.min(custo.getDiaVencimento(), mes.lengthOfMonth());
            LocalDate dataVencimento = mes.atDay(diaVencimento);

            saidas.add(ItemProjecaoDTO.builder()
                    .id(custo.getId())
                    .descricao(custo.getNome() + " (Fixo)")
                    .valor(custo.getValor())
                    .data(dataVencimento)
                    .origem(OrigemTransacao.CUSTO_FIXO)
                    .status("PREVISTO")
                    .build());
        }

        List<CustoVariavel> custosVariaveis = custoVariavelRepository.findByDataLancamentoBetween(primeiroDia,
                ultimoDia);
        for (CustoVariavel custo : custosVariaveis) {
            saidas.add(ItemProjecaoDTO.builder()
                    .id(custo.getId())
                    .descricao(custo.getNome() + " (Variável)")
                    .valor(custo.getValor())
                    .data(custo.getDataLancamento())
                    .origem(OrigemTransacao.CUSTO_VARIAVEL)
                    .status("PREVISTO")
                    .build());
        }

        return saidas;
    }

    /**
     * Cadastra ou atualiza o saldo inicial
     */
    @Transactional
    public SaldoInicialResponseDTO setSaldoInicial(SaldoInicialRequestDTO dto) {
        log.info("Cadastrando/atualizando saldo inicial: {}", dto.valor());

        Optional<SaldoInicial> existente = saldoInicialRepository.findFirst();

        SaldoInicial saldoInicial;
        if (existente.isPresent()) {
            saldoInicial = existente.get();
            saldoInicial.setValor(dto.valor());
            saldoInicial.setObservacao(dto.observacao());
        } else {
            saldoInicial = SaldoInicial.builder()
                    .valor(dto.valor())
                    .observacao(dto.observacao())
                    .build();
        }

        saldoInicial = saldoInicialRepository.save(saldoInicial);

        return convertToDTO(saldoInicial);
    }

    /**
     * Busca o saldo inicial cadastrado
     */
    @Transactional(readOnly = true)
    public SaldoInicialResponseDTO getSaldoInicial() {
        return saldoInicialRepository.findFirst()
                .map(this::convertToDTO)
                .orElseThrow(() -> new EntityNotFoundException("Saldo inicial não cadastrado"));
    }

    /**
     * Converte entidade para DTO
     */
    private SaldoInicialResponseDTO convertToDTO(SaldoInicial entity) {
        return SaldoInicialResponseDTO.builder()
                .id(entity.getId())
                .valor(entity.getValor())
                .observacao(entity.getObservacao())
                .dataRegistro(entity.getDataRegistro())
                .dataAtualizacao(entity.getDataAtualizacao())
                .build();
    }
}
