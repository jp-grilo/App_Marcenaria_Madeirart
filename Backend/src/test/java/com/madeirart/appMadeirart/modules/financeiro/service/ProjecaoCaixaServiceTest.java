package com.madeirart.appMadeirart.modules.financeiro.service;

import com.madeirart.appMadeirart.modules.custos.entity.CustoFixo;
import com.madeirart.appMadeirart.modules.custos.entity.CustoVariavel;
import com.madeirart.appMadeirart.modules.custos.repository.CustoFixoRepository;
import com.madeirart.appMadeirart.modules.custos.repository.CustoVariavelRepository;
import com.madeirart.appMadeirart.modules.financeiro.dto.*;
import com.madeirart.appMadeirart.modules.financeiro.entity.SaldoInicial;
import com.madeirart.appMadeirart.modules.financeiro.repository.SaldoInicialRepository;
import com.madeirart.appMadeirart.modules.orcamento.entity.Orcamento;
import com.madeirart.appMadeirart.modules.orcamento.entity.Parcela;
import com.madeirart.appMadeirart.modules.orcamento.repository.ParcelaRepository;
import com.madeirart.appMadeirart.shared.enums.StatusCusto;
import com.madeirart.appMadeirart.shared.enums.StatusOrcamento;
import com.madeirart.appMadeirart.shared.enums.StatusParcela;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Testes unitários para ProjecaoCaixaService
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("Testes do ProjecaoCaixaService")
class ProjecaoCaixaServiceTest {

    @Mock
    private SaldoInicialRepository saldoInicialRepository;

    @Mock
    private ParcelaRepository parcelaRepository;

    @Mock
    private CustoFixoRepository custoFixoRepository;

    @Mock
    private CustoVariavelRepository custoVariavelRepository;

    @InjectMocks
    private ProjecaoCaixaService projecaoCaixaService;

    private SaldoInicial saldoInicial;
    private Parcela parcelaPaga;
    private Parcela parcelaPendente;
    private CustoFixo custoFixo;
    private CustoVariavel custoVariavel;
    private Orcamento orcamento;

    @BeforeEach
    void setUp() {
        LocalDate hoje = LocalDate.now();

        saldoInicial = SaldoInicial.builder()
                .id(1L)
                .valor(new BigDecimal("5000.00"))
                .observacao("Saldo inicial do sistema")
                .dataRegistro(LocalDateTime.now())
                .dataAtualizacao(LocalDateTime.now())
                .build();

        orcamento = Orcamento.builder()
                .id(1L)
                .cliente("João Silva")
                .moveis("Armário")
                .status(StatusOrcamento.INICIADA)
                .build();

        parcelaPaga = Parcela.builder()
                .id(1L)
                .orcamento(orcamento)
                .numeroParcela(1)
                .valor(new BigDecimal("1000.00"))
                .dataVencimento(hoje.minusDays(10))
                .dataPagamento(hoje.minusDays(10))
                .status(StatusParcela.PAGO)
                .build();

        parcelaPendente = Parcela.builder()
                .id(2L)
                .orcamento(orcamento)
                .numeroParcela(2)
                .valor(new BigDecimal("1000.00"))
                .dataVencimento(hoje.plusDays(30))
                .status(StatusParcela.PENDENTE)
                .build();

        custoFixo = CustoFixo.builder()
                .id(1L)
                .nome("Aluguel")
                .valor(new BigDecimal("2000.00"))
                .diaVencimento(5)
                .ativo(true)
                .status(StatusCusto.PENDENTE)
                .createdAt(hoje.minusMonths(2))
                .build();

        custoVariavel = CustoVariavel.builder()
                .id(1L)
                .nome("Compra de ferramentas")
                .valor(new BigDecimal("500.00"))
                .dataLancamento(hoje.minusDays(5))
                .status(StatusCusto.PAGO)
                .build();
    }

    @Test
    @DisplayName("Deve calcular projeção de caixa com saldo inicial")
    void deveCalcularProjecaoCaixaComSaldoInicial() {
        when(saldoInicialRepository.findFirst()).thenReturn(Optional.of(saldoInicial));
        when(parcelaRepository.findAll()).thenReturn(List.of(parcelaPaga, parcelaPendente));
        when(custoVariavelRepository.findAll()).thenReturn(List.of(custoVariavel));
        when(custoFixoRepository.findByAtivoTrueOrderByDiaVencimento()).thenReturn(List.of(custoFixo));

        ProjecaoCaixaDTO resultado = projecaoCaixaService.getProjecaoCaixa();

        assertThat(resultado).isNotNull();
        assertThat(resultado.saldoInicialCadastrado()).isEqualTo(new BigDecimal("5000.00"));
        assertThat(resultado.saldoAtual()).isNotNull();
        assertThat(resultado.dataCalculo()).isNotNull();
        assertThat(resultado.mesesProjetados()).hasSize(2);

        verify(saldoInicialRepository).findFirst();
        verify(parcelaRepository, atLeastOnce()).findAll();
        verify(custoVariavelRepository, atLeastOnce()).findAll();
        verify(custoFixoRepository, atLeastOnce()).findByAtivoTrueOrderByDiaVencimento();
    }

    @Test
    @DisplayName("Deve calcular projeção de caixa sem saldo inicial cadastrado")
    void deveCalcularProjecaoCaixaSemSaldoInicial() {
        when(saldoInicialRepository.findFirst()).thenReturn(Optional.empty());
        when(parcelaRepository.findAll()).thenReturn(List.of(parcelaPaga));
        when(custoVariavelRepository.findAll()).thenReturn(List.of(custoVariavel));
        when(custoFixoRepository.findByAtivoTrueOrderByDiaVencimento()).thenReturn(List.of());

        ProjecaoCaixaDTO resultado = projecaoCaixaService.getProjecaoCaixa();

        assertThat(resultado).isNotNull();
        assertThat(resultado.saldoInicialCadastrado()).isEqualTo(BigDecimal.ZERO);
        assertThat(resultado.mesesProjetados()).hasSize(2);

        verify(saldoInicialRepository).findFirst();
    }

    @Test
    @DisplayName("Deve incluir parcelas pendentes na projeção futura")
    void deveIncluirParcelasPendentesNaProjecao() {
        LocalDate proximoMes = LocalDate.now().plusMonths(1);
        Parcela parcelaProximoMes = Parcela.builder()
                .id(3L)
                .orcamento(orcamento)
                .numeroParcela(3)
                .valor(new BigDecimal("1500.00"))
                .dataVencimento(proximoMes.withDayOfMonth(15))
                .status(StatusParcela.PENDENTE)
                .build();

        when(saldoInicialRepository.findFirst()).thenReturn(Optional.of(saldoInicial));
        when(parcelaRepository.findAll()).thenReturn(List.of(parcelaPaga, parcelaProximoMes));
        when(custoVariavelRepository.findAll()).thenReturn(List.of());
        when(custoFixoRepository.findByAtivoTrueOrderByDiaVencimento()).thenReturn(List.of());
        when(custoVariavelRepository.findByDataLancamentoBetween(any(), any())).thenReturn(List.of());

        ProjecaoCaixaDTO resultado = projecaoCaixaService.getProjecaoCaixa();

        assertThat(resultado.mesesProjetados()).isNotEmpty();

        // Verificar se a parcela está no mês correto
        MesProjecaoDTO primeiroMes = resultado.mesesProjetados().get(0);
        assertThat(primeiroMes.mesReferencia()).isEqualTo(proximoMes.getMonthValue());
        assertThat(primeiroMes.detalhesEntradas()).isNotEmpty();
        assertThat(primeiroMes.totalEntradasPrevistas()).isGreaterThan(BigDecimal.ZERO);
    }

    @Test
    @DisplayName("Deve incluir custos fixos na projeção futura")
    void deveIncluirCustosFixosNaProjecao() {
        when(saldoInicialRepository.findFirst()).thenReturn(Optional.of(saldoInicial));
        when(parcelaRepository.findAll()).thenReturn(List.of(parcelaPaga));
        when(custoVariavelRepository.findAll()).thenReturn(List.of());
        when(custoFixoRepository.findByAtivoTrueOrderByDiaVencimento()).thenReturn(List.of(custoFixo));
        when(custoVariavelRepository.findByDataLancamentoBetween(any(), any())).thenReturn(List.of());

        ProjecaoCaixaDTO resultado = projecaoCaixaService.getProjecaoCaixa();

        assertThat(resultado.mesesProjetados()).hasSize(2);

        // Cada mês deve ter o custo fixo projetado
        for (MesProjecaoDTO mes : resultado.mesesProjetados()) {
            assertThat(mes.detalhesSaidas()).isNotEmpty();
            assertThat(mes.totalSaidasPrevistas()).isGreaterThanOrEqualTo(custoFixo.getValor());
        }
    }

    @Test
    @DisplayName("Deve cadastrar saldo inicial com sucesso")
    void deveCadastrarSaldoInicial() {
        SaldoInicialRequestDTO requestDTO = SaldoInicialRequestDTO.builder()
                .valor(new BigDecimal("10000.00"))
                .observacao("Saldo de abertura")
                .build();

        when(saldoInicialRepository.findFirst()).thenReturn(Optional.empty());
        when(saldoInicialRepository.save(any(SaldoInicial.class))).thenReturn(saldoInicial);

        SaldoInicialResponseDTO resultado = projecaoCaixaService.setSaldoInicial(requestDTO);

        assertThat(resultado).isNotNull();
        assertThat(resultado.id()).isEqualTo(1L);
        assertThat(resultado.valor()).isEqualTo(new BigDecimal("5000.00"));

        verify(saldoInicialRepository).findFirst();
        verify(saldoInicialRepository).save(any(SaldoInicial.class));
    }

    @Test
    @DisplayName("Deve atualizar saldo inicial existente")
    void deveAtualizarSaldoInicialExistente() {
        SaldoInicialRequestDTO requestDTO = SaldoInicialRequestDTO.builder()
                .valor(new BigDecimal("15000.00"))
                .observacao("Saldo atualizado")
                .build();

        SaldoInicial saldoAtualizado = SaldoInicial.builder()
                .id(1L)
                .valor(new BigDecimal("15000.00"))
                .observacao("Saldo atualizado")
                .dataRegistro(saldoInicial.getDataRegistro())
                .dataAtualizacao(LocalDateTime.now())
                .build();

        when(saldoInicialRepository.findFirst()).thenReturn(Optional.of(saldoInicial));
        when(saldoInicialRepository.save(any(SaldoInicial.class))).thenReturn(saldoAtualizado);

        SaldoInicialResponseDTO resultado = projecaoCaixaService.setSaldoInicial(requestDTO);

        assertThat(resultado).isNotNull();
        assertThat(resultado.valor()).isEqualTo(new BigDecimal("15000.00"));
        assertThat(resultado.observacao()).isEqualTo("Saldo atualizado");

        verify(saldoInicialRepository).findFirst();
        verify(saldoInicialRepository).save(any(SaldoInicial.class));
    }

    @Test
    @DisplayName("Deve buscar saldo inicial cadastrado")
    void deveBuscarSaldoInicial() {
        when(saldoInicialRepository.findFirst()).thenReturn(Optional.of(saldoInicial));

        SaldoInicialResponseDTO resultado = projecaoCaixaService.getSaldoInicial();

        assertThat(resultado).isNotNull();
        assertThat(resultado.id()).isEqualTo(1L);
        assertThat(resultado.valor()).isEqualTo(new BigDecimal("5000.00"));
        assertThat(resultado.observacao()).isEqualTo("Saldo inicial do sistema");

        verify(saldoInicialRepository).findFirst();
    }

    @Test
    @DisplayName("Deve lançar exceção ao buscar saldo inicial não cadastrado")
    void deveLancarExcecaoAoBuscarSaldoInicialNaoCadastrado() {
        when(saldoInicialRepository.findFirst()).thenReturn(Optional.empty());

        assertThatThrownBy(() -> projecaoCaixaService.getSaldoInicial())
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("Saldo inicial não cadastrado");

        verify(saldoInicialRepository).findFirst();
    }

    @Test
    @DisplayName("Deve calcular saldo final projetado corretamente")
    void deveCalcularSaldoFinalProjetadoCorretamente() {
        LocalDate proximoMes = LocalDate.now().plusMonths(1);
        Parcela entrada = Parcela.builder()
                .id(3L)
                .orcamento(orcamento)
                .numeroParcela(1)
                .valor(new BigDecimal("3000.00"))
                .dataVencimento(proximoMes.withDayOfMonth(15))
                .status(StatusParcela.PENDENTE)
                .build();

        when(saldoInicialRepository.findFirst()).thenReturn(Optional.of(saldoInicial));
        when(parcelaRepository.findAll()).thenReturn(List.of(entrada));
        when(custoVariavelRepository.findAll()).thenReturn(List.of());
        when(custoFixoRepository.findByAtivoTrueOrderByDiaVencimento()).thenReturn(List.of(custoFixo));
        when(custoVariavelRepository.findByDataLancamentoBetween(any(), any())).thenReturn(List.of());

        ProjecaoCaixaDTO resultado = projecaoCaixaService.getProjecaoCaixa();

        MesProjecaoDTO primeiroMes = resultado.mesesProjetados().get(0);

        // Saldo Final = Saldo Inicial + Entradas - Saídas
        BigDecimal saldoEsperado = primeiroMes.saldoInicial()
                .add(primeiroMes.totalEntradasPrevistas())
                .subtract(primeiroMes.totalSaidasPrevistas());

        assertThat(primeiroMes.saldoFinalProjetado()).isEqualTo(saldoEsperado);
    }

    @Test
    @DisplayName("Deve usar saldo final do mês anterior como inicial do próximo")
    void deveUsarSaldoFinalComoInicialProximo() {
        when(saldoInicialRepository.findFirst()).thenReturn(Optional.of(saldoInicial));
        when(parcelaRepository.findAll()).thenReturn(List.of(parcelaPaga));
        when(custoVariavelRepository.findAll()).thenReturn(List.of());
        when(custoFixoRepository.findByAtivoTrueOrderByDiaVencimento()).thenReturn(List.of());
        when(custoVariavelRepository.findByDataLancamentoBetween(any(), any())).thenReturn(List.of());

        ProjecaoCaixaDTO resultado = projecaoCaixaService.getProjecaoCaixa();

        assertThat(resultado.mesesProjetados()).hasSize(2);

        MesProjecaoDTO mes1 = resultado.mesesProjetados().get(0);
        MesProjecaoDTO mes2 = resultado.mesesProjetados().get(1);

        // O saldo final do mês 1 deve ser igual ao saldo inicial do mês 2
        assertThat(mes1.saldoFinalProjetado()).isEqualTo(mes2.saldoInicial());
    }

    @Test
    @DisplayName("Deve projetar exatamente 2 meses futuros")
    void deveProjetarDoisMesesFuturos() {
        when(saldoInicialRepository.findFirst()).thenReturn(Optional.empty());
        when(parcelaRepository.findAll()).thenReturn(List.of());
        when(custoVariavelRepository.findAll()).thenReturn(List.of());
        when(custoFixoRepository.findByAtivoTrueOrderByDiaVencimento()).thenReturn(List.of());
        when(custoVariavelRepository.findByDataLancamentoBetween(any(), any())).thenReturn(List.of());

        ProjecaoCaixaDTO resultado = projecaoCaixaService.getProjecaoCaixa();

        assertThat(resultado.mesesProjetados()).hasSize(2);

        LocalDate hoje = LocalDate.now();
        LocalDate proximoMes = hoje.plusMonths(1);
        LocalDate mesDepois = hoje.plusMonths(2);

        assertThat(resultado.mesesProjetados().get(0).mesReferencia()).isEqualTo(proximoMes.getMonthValue());
        assertThat(resultado.mesesProjetados().get(1).mesReferencia()).isEqualTo(mesDepois.getMonthValue());
    }
}
