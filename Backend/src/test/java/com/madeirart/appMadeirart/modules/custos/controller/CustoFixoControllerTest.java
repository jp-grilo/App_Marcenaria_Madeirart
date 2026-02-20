package com.madeirart.appMadeirart.modules.custos.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.madeirart.appMadeirart.modules.custos.dto.CustoFixoRequestDTO;
import com.madeirart.appMadeirart.modules.custos.dto.CustoFixoResponseDTO;
import com.madeirart.appMadeirart.modules.custos.service.CustoFixoService;
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
 * Testes para CustoFixoController
 */
@WebMvcTest(CustoFixoController.class)
class CustoFixoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private CustoFixoService custoFixoService;

    private CustoFixoResponseDTO custoFixoResponseDTO;
    private CustoFixoRequestDTO custoFixoRequestDTO;

    @BeforeEach
    void setUp() {
        custoFixoResponseDTO = new CustoFixoResponseDTO(
                1L,
                "Aluguel",
                new BigDecimal("2000.00"),
                5,
                "Aluguel da oficina",
                true,
                StatusCusto.PENDENTE,
                LocalDate.now(),
                LocalDate.now());

        custoFixoRequestDTO = new CustoFixoRequestDTO(
                "Aluguel",
                new BigDecimal("2000.00"),
                5,
                "Aluguel da oficina");
    }

    @Test
    @DisplayName("GET /api/custos-fixos deve retornar lista de custos fixos")
    void deveListarTodosCustosFixos() throws Exception {
        CustoFixoResponseDTO custo2 = new CustoFixoResponseDTO(
                2L,
                "Energia",
                new BigDecimal("500.00"),
                10,
                "Conta de energia",
                true,
                StatusCusto.PAGO,
                LocalDate.now(),
                LocalDate.now());

        when(custoFixoService.listarTodos())
                .thenReturn(List.of(custoFixoResponseDTO, custo2));

        mockMvc.perform(get("/api/custos-fixos"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].nome").value("Aluguel"))
                .andExpect(jsonPath("$[1].id").value(2))
                .andExpect(jsonPath("$[1].nome").value("Energia"));

        verify(custoFixoService).listarTodos();
    }

    @Test
    @DisplayName("GET /api/custos-fixos com apenasAtivos=true deve retornar apenas custos ativos")
    void deveListarApenasCustosAtivos() throws Exception {
        when(custoFixoService.listarAtivos())
                .thenReturn(List.of(custoFixoResponseDTO));

        mockMvc.perform(get("/api/custos-fixos")
                .param("apenasAtivos", "true"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].ativo").value(true));

        verify(custoFixoService).listarAtivos();
    }

    @Test
    @DisplayName("GET /api/custos-fixos com orderByDiaVencimento=true deve retornar ordenado por dia")
    void deveListarOrdenadoPorDiaVencimento() throws Exception {
        when(custoFixoService.listarAtivosPorDiaVencimento())
                .thenReturn(List.of(custoFixoResponseDTO));

        mockMvc.perform(get("/api/custos-fixos")
                .param("apenasAtivos", "true")
                .param("orderByDiaVencimento", "true"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.length()").value(1));

        verify(custoFixoService).listarAtivosPorDiaVencimento();
    }

    @Test
    @DisplayName("GET /api/custos-fixos com filtro de dias deve retornar custos filtrados")
    void deveListarCustosPorPeriodoDias() throws Exception {
        when(custoFixoService.listarPorPeriodoDias(1, 15))
                .thenReturn(List.of(custoFixoResponseDTO));

        mockMvc.perform(get("/api/custos-fixos")
                .param("diaInicio", "1")
                .param("diaFim", "15"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].diaVencimento").value(5));

        verify(custoFixoService).listarPorPeriodoDias(1, 15);
    }

    @Test
    @DisplayName("GET /api/custos-fixos deve retornar lista vazia")
    void deveRetornarListaVaziaQuandoNaoHaCustos() throws Exception {
        when(custoFixoService.listarTodos()).thenReturn(List.of());

        mockMvc.perform(get("/api/custos-fixos"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    @DisplayName("GET /api/custos-fixos/{id} deve retornar custo por ID")
    void deveBuscarCustoPorId() throws Exception {
        when(custoFixoService.buscarPorId(1L)).thenReturn(custoFixoResponseDTO);

        mockMvc.perform(get("/api/custos-fixos/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.nome").value("Aluguel"))
                .andExpect(jsonPath("$.valor").value(2000.00))
                .andExpect(jsonPath("$.diaVencimento").value(5))
                .andExpect(jsonPath("$.status").value("PENDENTE"));

        verify(custoFixoService).buscarPorId(1L);
    }

    @Test
    @DisplayName("GET /api/custos-fixos/{id} deve retornar 404 quando custo não existe")
    void deveRetornar404QuandoCustoNaoExiste() throws Exception {
        when(custoFixoService.buscarPorId(999L))
                .thenThrow(new EntityNotFoundException("Custo fixo não encontrado com ID: 999"));

        mockMvc.perform(get("/api/custos-fixos/999"))
                .andExpect(status().isNotFound());

        verify(custoFixoService).buscarPorId(999L);
    }

    @Test
    @DisplayName("POST /api/custos-fixos deve criar custo fixo")
    void deveCriarCustoFixo() throws Exception {
        when(custoFixoService.criar(any(CustoFixoRequestDTO.class)))
                .thenReturn(custoFixoResponseDTO);

        mockMvc.perform(post("/api/custos-fixos")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(custoFixoRequestDTO)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.nome").value("Aluguel"))
                .andExpect(jsonPath("$.valor").value(2000.00))
                .andExpect(jsonPath("$.diaVencimento").value(5));

        verify(custoFixoService).criar(any(CustoFixoRequestDTO.class));
    }

    @Test
    @DisplayName("POST /api/custos-fixos deve retornar 400 quando dados inválidos")
    void deveRetornar400QuandoDadosInvalidos() throws Exception {
        CustoFixoRequestDTO requestInvalido = new CustoFixoRequestDTO(
                "", // nome vazio
                new BigDecimal("2000.00"),
                5,
                "Descrição");

        mockMvc.perform(post("/api/custos-fixos")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestInvalido)))
                .andExpect(status().isBadRequest());

        verify(custoFixoService, never()).criar(any());
    }

    @Test
    @DisplayName("PUT /api/custos-fixos/{id} deve atualizar custo fixo")
    void deveAtualizarCustoFixo() throws Exception {
        when(custoFixoService.atualizar(eq(1L), any(CustoFixoRequestDTO.class)))
                .thenReturn(custoFixoResponseDTO);

        mockMvc.perform(put("/api/custos-fixos/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(custoFixoRequestDTO)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.nome").value("Aluguel"));

        verify(custoFixoService).atualizar(eq(1L), any(CustoFixoRequestDTO.class));
    }

    @Test
    @DisplayName("PUT /api/custos-fixos/{id} deve retornar 404 quando custo não existe")
    void deveRetornar404AoAtualizarCustoInexistente() throws Exception {
        when(custoFixoService.atualizar(eq(999L), any(CustoFixoRequestDTO.class)))
                .thenThrow(new EntityNotFoundException("Custo fixo não encontrado com ID: 999"));

        mockMvc.perform(put("/api/custos-fixos/999")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(custoFixoRequestDTO)))
                .andExpect(status().isNotFound());

        verify(custoFixoService).atualizar(eq(999L), any(CustoFixoRequestDTO.class));
    }

    @Test
    @DisplayName("PATCH /api/custos-fixos/{id}/desativar deve desativar custo fixo")
    void deveDesativarCustoFixo() throws Exception {
        doNothing().when(custoFixoService).desativar(1L);

        mockMvc.perform(patch("/api/custos-fixos/1/desativar"))
                .andExpect(status().isNoContent());

        verify(custoFixoService).desativar(1L);
    }

    @Test
    @DisplayName("PATCH /api/custos-fixos/{id}/desativar deve retornar 404 quando custo não existe")
    void deveRetornar404AoDesativarCustoInexistente() throws Exception {
        doThrow(new EntityNotFoundException("Custo fixo não encontrado com ID: 999"))
                .when(custoFixoService).desativar(999L);

        mockMvc.perform(patch("/api/custos-fixos/999/desativar"))
                .andExpect(status().isNotFound());

        verify(custoFixoService).desativar(999L);
    }

    @Test
    @DisplayName("PATCH /api/custos-fixos/{id}/reativar deve reativar custo fixo")
    void deveReativarCustoFixo() throws Exception {
        when(custoFixoService.reativar(1L)).thenReturn(custoFixoResponseDTO);

        mockMvc.perform(patch("/api/custos-fixos/1/reativar"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.ativo").value(true));

        verify(custoFixoService).reativar(1L);
    }

    @Test
    @DisplayName("PATCH /api/custos-fixos/{id}/reativar deve retornar 404 quando custo não existe")
    void deveRetornar404AoReativarCustoInexistente() throws Exception {
        when(custoFixoService.reativar(999L))
                .thenThrow(new EntityNotFoundException("Custo fixo não encontrado com ID: 999"));

        mockMvc.perform(patch("/api/custos-fixos/999/reativar"))
                .andExpect(status().isNotFound());

        verify(custoFixoService).reativar(999L);
    }

    @Test
    @DisplayName("DELETE /api/custos-fixos/{id} deve excluir custo fixo")
    void deveExcluirCustoFixo() throws Exception {
        doNothing().when(custoFixoService).excluir(1L);

        mockMvc.perform(delete("/api/custos-fixos/1"))
                .andExpect(status().isNoContent());

        verify(custoFixoService).excluir(1L);
    }

    @Test
    @DisplayName("DELETE /api/custos-fixos/{id} deve retornar 404 quando custo não existe")
    void deveRetornar404AoExcluirCustoInexistente() throws Exception {
        doThrow(new EntityNotFoundException("Custo fixo não encontrado com ID: 999"))
                .when(custoFixoService).excluir(999L);

        mockMvc.perform(delete("/api/custos-fixos/999"))
                .andExpect(status().isNotFound());

        verify(custoFixoService).excluir(999L);
    }
}
