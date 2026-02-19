package com.madeirart.appMadeirart.modules.custos.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.madeirart.appMadeirart.modules.custos.dto.CustoVariavelRequestDTO;
import com.madeirart.appMadeirart.modules.custos.dto.CustoVariavelResponseDTO;
import com.madeirart.appMadeirart.modules.custos.service.CustoVariavelService;
import com.madeirart.appMadeirart.shared.enums.StatusCusto;
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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Testes para CustoVariavelController
 */
@WebMvcTest(CustoVariavelController.class)
class CustoVariavelControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private CustoVariavelService custoVariavelService;

    private CustoVariavelResponseDTO custoVariavelResponseDTO;
    private CustoVariavelRequestDTO custoVariavelRequestDTO;

    @BeforeEach
    void setUp() {
        custoVariavelResponseDTO = new CustoVariavelResponseDTO(
                1L,
                "Material de Construção",
                new BigDecimal("500.00"),
                LocalDate.now(),
                "Compra de materiais",
                StatusCusto.PENDENTE,
                LocalDate.now(),
                LocalDate.now());

        custoVariavelRequestDTO = new CustoVariavelRequestDTO(
                "Material de Construção",
                new BigDecimal("500.00"),
                LocalDate.now(),
                "Compra de materiais");
    }

    @Test
    @DisplayName("GET /api/custos-variaveis deve retornar lista de custos variáveis")
    void deveListarTodosCustosVariaveis() throws Exception {
        CustoVariavelResponseDTO custo2 = new CustoVariavelResponseDTO(
                2L,
                "Transporte",
                new BigDecimal("200.00"),
                LocalDate.now().minusDays(1),
                "Frete",
                StatusCusto.PAGO,
                LocalDate.now(),
                LocalDate.now());

        when(custoVariavelService.listarTodos())
                .thenReturn(List.of(custoVariavelResponseDTO, custo2));

        mockMvc.perform(get("/api/custos-variaveis"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].nome").value("Material de Construção"))
                .andExpect(jsonPath("$[1].id").value(2))
                .andExpect(jsonPath("$[1].nome").value("Transporte"));

        verify(custoVariavelService).listarTodos();
    }

    @Test
    @DisplayName("GET /api/custos-variaveis deve retornar lista vazia")
    void deveRetornarListaVaziaQuandoNaoHaCustos() throws Exception {
        when(custoVariavelService.listarTodos()).thenReturn(List.of());

        mockMvc.perform(get("/api/custos-variaveis"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    @DisplayName("GET /api/custos-variaveis com filtro de período deve retornar custos filtrados")
    void deveListarCustosPorPeriodo() throws Exception {
        LocalDate dataInicio = LocalDate.now().minusDays(7);
        LocalDate dataFim = LocalDate.now();

        when(custoVariavelService.listarPorPeriodo(dataInicio, dataFim))
                .thenReturn(List.of(custoVariavelResponseDTO));

        mockMvc.perform(get("/api/custos-variaveis")
                .param("dataInicio", dataInicio.toString())
                .param("dataFim", dataFim.toString()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].nome").value("Material de Construção"));

        verify(custoVariavelService).listarPorPeriodo(dataInicio, dataFim);
    }

    @Test
    @DisplayName("GET /api/custos-variaveis/{id} deve retornar custo por ID")
    void deveBuscarCustoPorId() throws Exception {
        when(custoVariavelService.buscarPorId(1L)).thenReturn(custoVariavelResponseDTO);

        mockMvc.perform(get("/api/custos-variaveis/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.nome").value("Material de Construção"))
                .andExpect(jsonPath("$.valor").value(500.00))
                .andExpect(jsonPath("$.status").value("PENDENTE"));

        verify(custoVariavelService).buscarPorId(1L);
    }

    @Test
    @DisplayName("GET /api/custos-variaveis/{id} deve retornar 404 quando custo não existe")
    void deveRetornar404QuandoCustoNaoExiste() throws Exception {
        when(custoVariavelService.buscarPorId(999L))
                .thenThrow(new EntityNotFoundException("Custo variável não encontrado com ID: 999"));

        mockMvc.perform(get("/api/custos-variaveis/999"))
                .andExpect(status().isNotFound());

        verify(custoVariavelService).buscarPorId(999L);
    }

    @Test
    @DisplayName("POST /api/custos-variaveis deve criar custo variável")
    void deveCriarCustoVariavel() throws Exception {
        when(custoVariavelService.criar(any(CustoVariavelRequestDTO.class)))
                .thenReturn(custoVariavelResponseDTO);

        mockMvc.perform(post("/api/custos-variaveis")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(custoVariavelRequestDTO)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.nome").value("Material de Construção"))
                .andExpect(jsonPath("$.valor").value(500.00));

        verify(custoVariavelService).criar(any(CustoVariavelRequestDTO.class));
    }

    @Test
    @DisplayName("POST /api/custos-variaveis deve retornar 400 quando dados inválidos")
    void deveRetornar400QuandoDadosInvalidos() throws Exception {
        CustoVariavelRequestDTO requestInvalido = new CustoVariavelRequestDTO(
                "", // nome vazio
                new BigDecimal("500.00"),
                LocalDate.now(),
                "Descrição");

        mockMvc.perform(post("/api/custos-variaveis")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestInvalido)))
                .andExpect(status().isBadRequest());

        verify(custoVariavelService, never()).criar(any());
    }

    @Test
    @DisplayName("PUT /api/custos-variaveis/{id} deve atualizar custo variável")
    void deveAtualizarCustoVariavel() throws Exception {
        when(custoVariavelService.atualizar(eq(1L), any(CustoVariavelRequestDTO.class)))
                .thenReturn(custoVariavelResponseDTO);

        mockMvc.perform(put("/api/custos-variaveis/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(custoVariavelRequestDTO)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.nome").value("Material de Construção"));

        verify(custoVariavelService).atualizar(eq(1L), any(CustoVariavelRequestDTO.class));
    }

    @Test
    @DisplayName("PUT /api/custos-variaveis/{id} deve retornar 404 quando custo não existe")
    void deveRetornar404AoAtualizarCustoInexistente() throws Exception {
        when(custoVariavelService.atualizar(eq(999L), any(CustoVariavelRequestDTO.class)))
                .thenThrow(new EntityNotFoundException("Custo variável não encontrado com ID: 999"));

        mockMvc.perform(put("/api/custos-variaveis/999")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(custoVariavelRequestDTO)))
                .andExpect(status().isNotFound());

        verify(custoVariavelService).atualizar(eq(999L), any(CustoVariavelRequestDTO.class));
    }

    @Test
    @DisplayName("DELETE /api/custos-variaveis/{id} deve excluir custo variável")
    void deveExcluirCustoVariavel() throws Exception {
        doNothing().when(custoVariavelService).excluir(1L);

        mockMvc.perform(delete("/api/custos-variaveis/1"))
                .andExpect(status().isNoContent());

        verify(custoVariavelService).excluir(1L);
    }

    @Test
    @DisplayName("DELETE /api/custos-variaveis/{id} deve retornar 404 quando custo não existe")
    void deveRetornar404AoExcluirCustoInexistente() throws Exception {
        doThrow(new EntityNotFoundException("Custo variável não encontrado com ID: 999"))
                .when(custoVariavelService).excluir(999L);

        mockMvc.perform(delete("/api/custos-variaveis/999"))
                .andExpect(status().isNotFound());

        verify(custoVariavelService).excluir(999L);
    }
}
