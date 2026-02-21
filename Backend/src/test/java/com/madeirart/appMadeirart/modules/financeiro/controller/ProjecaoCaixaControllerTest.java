package com.madeirart.appMadeirart.modules.financeiro.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.madeirart.appMadeirart.modules.financeiro.dto.*;
import com.madeirart.appMadeirart.modules.financeiro.service.ProjecaoCaixaService;
import com.madeirart.appMadeirart.shared.enums.OrigemTransacao;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Testes para ProjecaoCaixaController
 */
@WebMvcTest(ProjecaoCaixaController.class)
@DisplayName("Testes do ProjecaoCaixaController")
class ProjecaoCaixaControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private ProjecaoCaixaService projecaoCaixaService;

    private ProjecaoCaixaDTO projecaoCaixaDTO;
    private SaldoInicialResponseDTO saldoInicialResponseDTO;
    private SaldoInicialRequestDTO saldoInicialRequestDTO;

    @BeforeEach
    void setUp() {
        LocalDate hoje = LocalDate.now();
        LocalDate proximoMes = hoje.plusMonths(1);
        LocalDate mesDepois = hoje.plusMonths(2);

        // Criar itens de projeção
        ItemProjecaoDTO entrada1 = ItemProjecaoDTO.builder()
                .id(1L)
                .descricao("Parcela 1 - Orçamento #1")
                .valor(new BigDecimal("1000.00"))
                .data(proximoMes.withDayOfMonth(15))
                .origem(OrigemTransacao.PARCELA)
                .status("PENDENTE")
                .build();

        ItemProjecaoDTO saida1 = ItemProjecaoDTO.builder()
                .id(1L)
                .descricao("Aluguel (Fixo)")
                .valor(new BigDecimal("2000.00"))
                .data(proximoMes.withDayOfMonth(5))
                .origem(OrigemTransacao.CUSTO_FIXO)
                .status("PREVISTO")
                .build();

        // Criar projeção dos meses
        MesProjecaoDTO mes1 = MesProjecaoDTO.builder()
                .mesReferencia(proximoMes.getMonthValue())
                .anoReferencia(proximoMes.getYear())
                .saldoInicial(new BigDecimal("5000.00"))
                .totalEntradasPrevistas(new BigDecimal("1000.00"))
                .totalSaidasPrevistas(new BigDecimal("2000.00"))
                .saldoFinalProjetado(new BigDecimal("4000.00"))
                .detalhesEntradas(List.of(entrada1))
                .detalhesSaidas(List.of(saida1))
                .build();

        MesProjecaoDTO mes2 = MesProjecaoDTO.builder()
                .mesReferencia(mesDepois.getMonthValue())
                .anoReferencia(mesDepois.getYear())
                .saldoInicial(new BigDecimal("4000.00"))
                .totalEntradasPrevistas(BigDecimal.ZERO)
                .totalSaidasPrevistas(new BigDecimal("2000.00"))
                .saldoFinalProjetado(new BigDecimal("2000.00"))
                .detalhesEntradas(List.of())
                .detalhesSaidas(List.of(saida1))
                .build();

        // Criar projeção completa
        projecaoCaixaDTO = ProjecaoCaixaDTO.builder()
                .saldoAtual(new BigDecimal("5000.00"))
                .dataCalculo(LocalDateTime.now())
                .saldoInicialCadastrado(new BigDecimal("3000.00"))
                .mesesProjetados(List.of(mes1, mes2))
                .build();

        // Criar DTOs de saldo inicial
        saldoInicialRequestDTO = SaldoInicialRequestDTO.builder()
                .valor(new BigDecimal("10000.00"))
                .observacao("Saldo de abertura do sistema")
                .build();

        saldoInicialResponseDTO = SaldoInicialResponseDTO.builder()
                .id(1L)
                .valor(new BigDecimal("10000.00"))
                .observacao("Saldo de abertura do sistema")
                .dataRegistro(LocalDateTime.now())
                .dataAtualizacao(LocalDateTime.now())
                .build();
    }

    @Test
    @DisplayName("GET /api/financeiro/projecao-caixa deve retornar projeção completa")
    void deveRetornarProjecaoCaixa() throws Exception {
        when(projecaoCaixaService.getProjecaoCaixa()).thenReturn(projecaoCaixaDTO);

        mockMvc.perform(get("/api/financeiro/projecao-caixa"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.saldoAtual").value(5000.00))
                .andExpect(jsonPath("$.saldoInicialCadastrado").value(3000.00))
                .andExpect(jsonPath("$.dataCalculo").exists())
                .andExpect(jsonPath("$.mesesProjetados").isArray())
                .andExpect(jsonPath("$.mesesProjetados.length()").value(2))
                .andExpect(jsonPath("$.mesesProjetados[0].saldoInicial").value(5000.00))
                .andExpect(jsonPath("$.mesesProjetados[0].totalEntradasPrevistas").value(1000.00))
                .andExpect(jsonPath("$.mesesProjetados[0].totalSaidasPrevistas").value(2000.00))
                .andExpect(jsonPath("$.mesesProjetados[0].saldoFinalProjetado").value(4000.00))
                .andExpect(jsonPath("$.mesesProjetados[0].detalhesEntradas").isArray())
                .andExpect(jsonPath("$.mesesProjetados[0].detalhesSaidas").isArray());

        verify(projecaoCaixaService).getProjecaoCaixa();
    }

    @Test
    @DisplayName("GET /api/financeiro/projecao-caixa deve incluir detalhes das transações")
    void deveIncluirDetalhesTransacoes() throws Exception {
        when(projecaoCaixaService.getProjecaoCaixa()).thenReturn(projecaoCaixaDTO);

        mockMvc.perform(get("/api/financeiro/projecao-caixa"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.mesesProjetados[0].detalhesEntradas[0].id").value(1))
                .andExpect(jsonPath("$.mesesProjetados[0].detalhesEntradas[0].descricao")
                        .value("Parcela 1 - Orçamento #1"))
                .andExpect(jsonPath("$.mesesProjetados[0].detalhesEntradas[0].valor").value(1000.00))
                .andExpect(jsonPath("$.mesesProjetados[0].detalhesEntradas[0].origem").value("PARCELA"))
                .andExpect(jsonPath("$.mesesProjetados[0].detalhesEntradas[0].status").value("PENDENTE"))
                .andExpect(jsonPath("$.mesesProjetados[0].detalhesSaidas[0].descricao").value("Aluguel (Fixo)"))
                .andExpect(jsonPath("$.mesesProjetados[0].detalhesSaidas[0].valor").value(2000.00))
                .andExpect(jsonPath("$.mesesProjetados[0].detalhesSaidas[0].origem").value("CUSTO_FIXO"));

        verify(projecaoCaixaService).getProjecaoCaixa();
    }

    @Test
    @DisplayName("POST /api/financeiro/saldo-inicial deve cadastrar saldo inicial")
    void deveCadastrarSaldoInicial() throws Exception {
        when(projecaoCaixaService.setSaldoInicial(any(SaldoInicialRequestDTO.class)))
                .thenReturn(saldoInicialResponseDTO);

        mockMvc.perform(post("/api/financeiro/saldo-inicial")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(saldoInicialRequestDTO)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.valor").value(10000.00))
                .andExpect(jsonPath("$.observacao").value("Saldo de abertura do sistema"))
                .andExpect(jsonPath("$.dataRegistro").exists())
                .andExpect(jsonPath("$.dataAtualizacao").exists());

        verify(projecaoCaixaService).setSaldoInicial(any(SaldoInicialRequestDTO.class));
    }

    @Test
    @DisplayName("POST /api/financeiro/saldo-inicial deve validar valor obrigatório")
    void deveValidarValorObrigatorio() throws Exception {
        SaldoInicialRequestDTO dtoInvalido = SaldoInicialRequestDTO.builder()
                .valor(null)
                .observacao("Sem valor")
                .build();

        mockMvc.perform(post("/api/financeiro/saldo-inicial")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dtoInvalido)))
                .andExpect(status().isBadRequest());

        verify(projecaoCaixaService, never()).setSaldoInicial(any());
    }

    @Test
    @DisplayName("GET /api/financeiro/saldo-inicial deve retornar saldo cadastrado")
    void deveRetornarSaldoInicial() throws Exception {
        when(projecaoCaixaService.getSaldoInicial()).thenReturn(saldoInicialResponseDTO);

        mockMvc.perform(get("/api/financeiro/saldo-inicial"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.valor").value(10000.00))
                .andExpect(jsonPath("$.observacao").value("Saldo de abertura do sistema"));

        verify(projecaoCaixaService).getSaldoInicial();
    }

    @Test
    @DisplayName("GET /api/financeiro/saldo-inicial deve retornar 404 quando não cadastrado")
    void deveRetornar404QuandoSaldoNaoCadastrado() throws Exception {
        when(projecaoCaixaService.getSaldoInicial())
                .thenThrow(new EntityNotFoundException("Saldo inicial não cadastrado"));

        mockMvc.perform(get("/api/financeiro/saldo-inicial"))
                .andExpect(status().isNotFound());

        verify(projecaoCaixaService).getSaldoInicial();
    }

    @Test
    @DisplayName("POST /api/financeiro/saldo-inicial deve aceitar valor zero")
    void deveAceitarValorZero() throws Exception {
        SaldoInicialRequestDTO dtoComZero = SaldoInicialRequestDTO.builder()
                .valor(BigDecimal.ZERO)
                .observacao("Sem saldo inicial")
                .build();

        SaldoInicialResponseDTO responseComZero = SaldoInicialResponseDTO.builder()
                .id(1L)
                .valor(BigDecimal.ZERO)
                .observacao("Sem saldo inicial")
                .dataRegistro(LocalDateTime.now())
                .dataAtualizacao(LocalDateTime.now())
                .build();

        when(projecaoCaixaService.setSaldoInicial(any(SaldoInicialRequestDTO.class)))
                .thenReturn(responseComZero);

        mockMvc.perform(post("/api/financeiro/saldo-inicial")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dtoComZero)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.valor").value(0));

        verify(projecaoCaixaService).setSaldoInicial(any(SaldoInicialRequestDTO.class));
    }

    @Test
    @DisplayName("GET /api/financeiro/projecao-caixa deve retornar meses em ordem correta")
    void deveRetornarMesesEmOrdemCorreta() throws Exception {
        when(projecaoCaixaService.getProjecaoCaixa()).thenReturn(projecaoCaixaDTO);

        mockMvc.perform(get("/api/financeiro/projecao-caixa"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.mesesProjetados[0].mesReferencia").exists())
                .andExpect(jsonPath("$.mesesProjetados[1].mesReferencia").exists())
                .andExpect(jsonPath("$.mesesProjetados[0].anoReferencia").exists())
                .andExpect(jsonPath("$.mesesProjetados[1].anoReferencia").exists());

        verify(projecaoCaixaService).getProjecaoCaixa();
    }

    @Test
    @DisplayName("GET /api/financeiro/projecao-caixa deve retornar saldo acumulado correto")
    void deveRetornarSaldoAcumuladoCorreto() throws Exception {
        when(projecaoCaixaService.getProjecaoCaixa()).thenReturn(projecaoCaixaDTO);

        mockMvc.perform(get("/api/financeiro/projecao-caixa"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.mesesProjetados[0].saldoInicial").value(5000.00))
                .andExpect(jsonPath("$.mesesProjetados[0].saldoFinalProjetado").value(4000.00))
                .andExpect(jsonPath("$.mesesProjetados[1].saldoInicial").value(4000.00))
                .andExpect(jsonPath("$.mesesProjetados[1].saldoFinalProjetado").value(2000.00));

        verify(projecaoCaixaService).getProjecaoCaixa();
    }
}
