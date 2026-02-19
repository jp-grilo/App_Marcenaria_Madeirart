package com.madeirart.appMadeirart.modules.orcamento.controller;

import com.madeirart.appMadeirart.modules.orcamento.dto.ParcelaResponseDTO;
import com.madeirart.appMadeirart.modules.orcamento.service.ParcelaService;
import com.madeirart.appMadeirart.shared.enums.StatusParcela;
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
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Testes para ParcelaController
 */
@WebMvcTest(ParcelaController.class)
class ParcelaControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ParcelaService parcelaService;

    private ParcelaResponseDTO parcelaResponseDTO;

    @BeforeEach
    void setUp() {
        parcelaResponseDTO = ParcelaResponseDTO.builder()
                .id(1L)
                .orcamentoId(1L)
                .numeroParcela(1)
                .valor(new BigDecimal("1000.00"))
                .dataVencimento(LocalDate.now().plusDays(30))
                .dataPagamento(null)
                .status(StatusParcela.PENDENTE)
                .createdAt(LocalDate.now())
                .build();
    }

    @Test
    @DisplayName("GET /api/parcelas/orcamento/{id} deve retornar lista de parcelas")
    void deveListarParcelasPorOrcamento() throws Exception {
        ParcelaResponseDTO parcela2 = ParcelaResponseDTO.builder()
                .id(2L)
                .orcamentoId(1L)
                .numeroParcela(2)
                .valor(new BigDecimal("1000.00"))
                .dataVencimento(LocalDate.now().plusDays(60))
                .status(StatusParcela.PENDENTE)
                .build();

        when(parcelaService.listarPorOrcamento(1L))
                .thenReturn(List.of(parcelaResponseDTO, parcela2));

        mockMvc.perform(get("/api/parcelas/orcamento/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].numeroParcela").value(1))
                .andExpect(jsonPath("$[1].id").value(2))
                .andExpect(jsonPath("$[1].numeroParcela").value(2));

        verify(parcelaService).listarPorOrcamento(1L);
    }

    @Test
    @DisplayName("GET /api/parcelas/orcamento/{id} deve retornar lista vazia")
    void deveRetornarListaVaziaQuandoNaoHaParcelas() throws Exception {
        when(parcelaService.listarPorOrcamento(1L)).thenReturn(List.of());

        mockMvc.perform(get("/api/parcelas/orcamento/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    @DisplayName("GET /api/parcelas/{id} deve retornar parcela por ID")
    void deveBuscarParcelaPorId() throws Exception {
        when(parcelaService.buscarPorId(1L)).thenReturn(parcelaResponseDTO);

        mockMvc.perform(get("/api/parcelas/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.numeroParcela").value(1))
                .andExpect(jsonPath("$.valor").value(1000.00))
                .andExpect(jsonPath("$.status").value("PENDENTE"));

        verify(parcelaService).buscarPorId(1L);
    }

    @Test
    @DisplayName("GET /api/parcelas/{id} deve retornar 404 quando parcela não existe")
    void deveRetornar404QuandoParcelaNaoExiste() throws Exception {
        when(parcelaService.buscarPorId(999L))
                .thenThrow(new EntityNotFoundException("Parcela não encontrada com ID: 999"));

        mockMvc.perform(get("/api/parcelas/999"))
                .andExpect(status().isNotFound());

        verify(parcelaService).buscarPorId(999L);
    }

    @Test
    @DisplayName("PATCH /api/parcelas/{id}/confirmar deve confirmar pagamento")
    void deveConfirmarPagamento() throws Exception {
        ParcelaResponseDTO parcelaPaga = ParcelaResponseDTO.builder()
                .id(1L)
                .orcamentoId(1L)
                .numeroParcela(1)
                .valor(new BigDecimal("1000.00"))
                .dataVencimento(LocalDate.now().plusDays(30))
                .dataPagamento(LocalDate.now())
                .status(StatusParcela.PAGO)
                .createdAt(LocalDate.now())
                .build();

        when(parcelaService.confirmarPagamento(1L)).thenReturn(parcelaPaga);

        mockMvc.perform(patch("/api/parcelas/1/confirmar"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.status").value("PAGO"))
                .andExpect(jsonPath("$.dataPagamento").exists());

        verify(parcelaService).confirmarPagamento(1L);
    }

    @Test
    @DisplayName("PATCH /api/parcelas/{id}/confirmar deve retornar 404 quando parcela não existe")
    void deveRetornar404AoConfirmarParcelaInexistente() throws Exception {
        when(parcelaService.confirmarPagamento(999L))
                .thenThrow(new EntityNotFoundException("Parcela não encontrada com ID: 999"));

        mockMvc.perform(patch("/api/parcelas/999/confirmar"))
                .andExpect(status().isNotFound());

        verify(parcelaService).confirmarPagamento(999L);
    }

    @Test
    @DisplayName("PATCH /api/parcelas/{id}/confirmar deve retornar 400 quando parcela já está paga")
    void deveRetornar400AoConfirmarParcelaJaPaga() throws Exception {
        when(parcelaService.confirmarPagamento(1L))
                .thenThrow(new IllegalStateException("Parcela já foi confirmada como paga"));

        mockMvc.perform(patch("/api/parcelas/1/confirmar"))
                .andExpect(status().isBadRequest());

        verify(parcelaService).confirmarPagamento(1L);
    }
}
