package com.madeirart.appMadeirart.modules.orcamento.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.madeirart.appMadeirart.modules.orcamento.dto.IniciarProducaoDTO;
import com.madeirart.appMadeirart.modules.orcamento.dto.ItemMaterialDTO;
import com.madeirart.appMadeirart.modules.orcamento.dto.OrcamentoAuditoriaDTO;
import com.madeirart.appMadeirart.modules.orcamento.dto.OrcamentoRequestDTO;
import com.madeirart.appMadeirart.modules.orcamento.dto.OrcamentoResponseDTO;
import com.madeirart.appMadeirart.modules.orcamento.dto.ParcelaDTO;
import com.madeirart.appMadeirart.modules.orcamento.service.OrcamentoService;
import com.madeirart.appMadeirart.shared.enums.StatusOrcamento;

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
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Testes essenciais do OrcamentoController
 * Foca apenas nos fluxos principais da API REST
 */
@WebMvcTest(OrcamentoController.class)
@DisplayName("Testes do OrcamentoController")
class OrcamentoControllerTest {

        @Autowired
        private MockMvc mockMvc;

        @Autowired
        private ObjectMapper objectMapper;

        @MockitoBean
        private OrcamentoService orcamentoService;

        private OrcamentoRequestDTO requestDTO;
        private OrcamentoResponseDTO responseDTO;

        @BeforeEach
        void setUp() {
                ItemMaterialDTO item = new ItemMaterialDTO(
                                1L,
                                new BigDecimal("4"),
                                "Placa MDF 15mm",
                                new BigDecimal("180.00"),
                                new BigDecimal("720.00"));

                requestDTO = new OrcamentoRequestDTO(
                                "João Silva",
                                "Armário planejado",
                                LocalDate.now(),
                                LocalDate.now().plusDays(30),
                                new BigDecimal("1.5"),
                                new BigDecimal("250.00"),
                                new BigDecimal("150.00"),
                                List.of(item));

                responseDTO = OrcamentoResponseDTO.builder()
                                .id(1L)
                                .cliente("João Silva")
                                .moveis("Armário planejado")
                                .data(LocalDate.now())
                                .previsaoEntrega(LocalDate.now().plusDays(30))
                                .status(StatusOrcamento.AGUARDANDO)
                                .subtotalMateriais(new BigDecimal("720.00"))
                                .valorMaoDeObra(new BigDecimal("1080.00"))
                                .valorTotal(new BigDecimal("2200.00"))
                                .itens(List.of(item))
                                .build();
        }

        @Test
        @DisplayName("POST /api/orcamentos - Deve criar orçamento")
        void deveCriarOrcamento() throws Exception {
                when(orcamentoService.criarOrcamento(any())).thenReturn(responseDTO);

                mockMvc.perform(post("/api/orcamentos")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(requestDTO)))
                                .andExpect(status().isCreated())
                                .andExpect(jsonPath("$.id").value(1))
                                .andExpect(jsonPath("$.cliente").value("João Silva"));
        }

        @Test
        @DisplayName("POST /api/orcamentos - Deve rejeitar dados inválidos")
        void deveRejeitarDadosInvalidos() throws Exception {
                OrcamentoRequestDTO invalidDTO = new OrcamentoRequestDTO(
                                "",
                                "Armário",
                                LocalDate.now(),
                                LocalDate.now().plusDays(30),
                                new BigDecimal("1.5"),
                                new BigDecimal("250.00"),
                                new BigDecimal("150.00"),
                                List.of());

                mockMvc.perform(post("/api/orcamentos")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(invalidDTO)))
                                .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("GET /api/orcamentos/{id} - Deve buscar orçamento")
        void deveBuscarOrcamento() throws Exception {
                when(orcamentoService.buscarPorId(1L)).thenReturn(responseDTO);

                mockMvc.perform(get("/api/orcamentos/1"))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.id").value(1))
                                .andExpect(jsonPath("$.cliente").value("João Silva"));
        }

        @Test
        @DisplayName("GET /api/orcamentos/{id} - Deve retornar 404 quando não encontrado")
        void deveRetornar404QuandoNaoEncontrado() throws Exception {
                when(orcamentoService.buscarPorId(999L))
                                .thenThrow(new EntityNotFoundException("Orçamento não encontrado"));

                mockMvc.perform(get("/api/orcamentos/999"))
                                .andExpect(status().isNotFound());
        }

        @Test
        @DisplayName("GET /api/orcamentos - Deve listar todos os orçamentos")
        void deveListarOrcamentos() throws Exception {
                when(orcamentoService.listarTodos()).thenReturn(List.of(responseDTO));

                mockMvc.perform(get("/api/orcamentos"))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$[0].id").value(1));
        }

        @Test
        @DisplayName("GET /api/orcamentos?status=AGUARDANDO - Deve filtrar por status")
        void deveFiltrarPorStatus() throws Exception {
                when(orcamentoService.listarPorStatus(StatusOrcamento.AGUARDANDO))
                                .thenReturn(List.of(responseDTO));

                mockMvc.perform(get("/api/orcamentos")
                                .param("status", "AGUARDANDO"))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$[0].status").value("AGUARDANDO"));
        }

        @Test
        @DisplayName("PUT /api/orcamentos/{id} - Deve atualizar orçamento")
        void deveAtualizarOrcamento() throws Exception {
                when(orcamentoService.atualizarOrcamento(eq(1L), any())).thenReturn(responseDTO);

                mockMvc.perform(put("/api/orcamentos/1")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(requestDTO)))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.id").value(1));
        }

        @Test
        @DisplayName("DELETE /api/orcamentos/{id} - Deve deletar orçamento")
        void deveDeletarOrcamento() throws Exception {
                mockMvc.perform(delete("/api/orcamentos/1"))
                                .andExpect(status().isNoContent());
        }

        @Test
        @DisplayName("GET /api/orcamentos/{id}/historico - Deve buscar histórico de auditoria")
        void deveBuscarHistorico() throws Exception {
                OrcamentoAuditoriaDTO auditoria1 = OrcamentoAuditoriaDTO.builder()
                                .id(1L)
                                .orcamentoId(1L)
                                .snapshotJson("{\"id\":1,\"cliente\":\"João Silva\"}")
                                .dataAlteracao(LocalDateTime.now())
                                .descricaoAlteracao("Atualização de orçamento")
                                .build();

                OrcamentoAuditoriaDTO auditoria2 = OrcamentoAuditoriaDTO.builder()
                                .id(2L)
                                .orcamentoId(1L)
                                .snapshotJson("{\"id\":1,\"cliente\":\"João Silva Atualizado\"}")
                                .dataAlteracao(LocalDateTime.now().plusHours(1))
                                .descricaoAlteracao("Atualização de orçamento")
                                .build();

                when(orcamentoService.buscarHistorico(1L)).thenReturn(List.of(auditoria2, auditoria1));

                mockMvc.perform(get("/api/orcamentos/1/historico"))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$[0].id").value(2))
                                .andExpect(jsonPath("$[1].id").value(1))
                                .andExpect(jsonPath("$[0].orcamentoId").value(1));
        }

        @Test
        @DisplayName("GET /api/orcamentos/{id}/historico - Deve retornar 404 para orçamento inexistente")
        void deveRetornar404AoBuscarHistoricoDeOrcamentoInexistente() throws Exception {
                when(orcamentoService.buscarHistorico(999L))
                                .thenThrow(new EntityNotFoundException("Orçamento não encontrado"));

                mockMvc.perform(get("/api/orcamentos/999/historico"))
                                .andExpect(status().isNotFound());
        }

        @Test
        @DisplayName("GET /api/orcamentos/{id}/historico - Deve retornar lista vazia para orçamento sem histórico")
        void deveRetornarListaVaziaParaOrcamentoSemHistorico() throws Exception {
                when(orcamentoService.buscarHistorico(1L)).thenReturn(List.of());

                mockMvc.perform(get("/api/orcamentos/1/historico"))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$").isArray())
                                .andExpect(jsonPath("$").isEmpty());
        }

        @Test
        @DisplayName("PATCH /api/orcamentos/{id}/iniciar - Deve iniciar produção")
        void deveIniciarProducao() throws Exception {
                ParcelaDTO parcela1 = new ParcelaDTO(new BigDecimal("500.00"), LocalDate.now().plusDays(30));
                ParcelaDTO parcela2 = new ParcelaDTO(new BigDecimal("700.00"), LocalDate.now().plusDays(60));

                IniciarProducaoDTO dto = new IniciarProducaoDTO(
                                new BigDecimal("1000.00"),
                                LocalDate.now(),
                                List.of(parcela1, parcela2));

                OrcamentoResponseDTO responseIniciado = OrcamentoResponseDTO.builder()
                                .id(1L)
                                .cliente("João Silva")
                                .moveis("Armário planejado")
                                .status(StatusOrcamento.INICIADA)
                                .valorTotal(new BigDecimal("2200.00"))
                                .build();

                when(orcamentoService.iniciarProducao(eq(1L), any(IniciarProducaoDTO.class)))
                                .thenReturn(responseIniciado);

                mockMvc.perform(patch("/api/orcamentos/1/iniciar")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(dto)))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.id").value(1))
                                .andExpect(jsonPath("$.status").value("INICIADA"));
        }

        @Test
        @DisplayName("PATCH /api/orcamentos/{id}/iniciar - Deve iniciar produção com pagamento integral")
        void deveIniciarProducaoComPagamentoIntegral() throws Exception {
                IniciarProducaoDTO dto = new IniciarProducaoDTO(
                                new BigDecimal("2200.00"),
                                LocalDate.now(),
                                List.of());  // sem parcelas

                OrcamentoResponseDTO responseIniciado = OrcamentoResponseDTO.builder()
                                .id(1L)
                                .cliente("João Silva")
                                .moveis("Armário planejado")
                                .status(StatusOrcamento.INICIADA)
                                .valorTotal(new BigDecimal("2200.00"))
                                .build();

                when(orcamentoService.iniciarProducao(eq(1L), any(IniciarProducaoDTO.class)))
                                .thenReturn(responseIniciado);

                mockMvc.perform(patch("/api/orcamentos/1/iniciar")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(dto)))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.id").value(1))
                                .andExpect(jsonPath("$.status").value("INICIADA"));
        }

        @Test
        @DisplayName("PATCH /api/orcamentos/{id}/iniciar - Deve retornar 404 para orçamento inexistente")
        void deveRetornar404AoIniciarProducaoDeOrcamentoInexistente() throws Exception {
                IniciarProducaoDTO dto = new IniciarProducaoDTO(
                                new BigDecimal("1000.00"),
                                LocalDate.now(),
                                List.of());

                when(orcamentoService.iniciarProducao(eq(999L), any(IniciarProducaoDTO.class)))
                                .thenThrow(new EntityNotFoundException("Orçamento não encontrado"));

                mockMvc.perform(patch("/api/orcamentos/999/iniciar")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(dto)))
                                .andExpect(status().isNotFound());
        }

        @Test
        @DisplayName("PATCH /api/orcamentos/{id}/iniciar - Deve retornar 400 para status inválido")
        void deveRetornar400AoIniciarProducaoComStatusInvalido() throws Exception {
                IniciarProducaoDTO dto = new IniciarProducaoDTO(
                                new BigDecimal("1000.00"),
                                LocalDate.now(),
                                List.of());

                when(orcamentoService.iniciarProducao(eq(1L), any(IniciarProducaoDTO.class)))
                                .thenThrow(new IllegalStateException("Status deve ser AGUARDANDO"));

                mockMvc.perform(patch("/api/orcamentos/1/iniciar")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(dto)))
                                .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("PATCH /api/orcamentos/{id}/iniciar - Deve retornar 400 quando soma não bate")
        void deveRetornar400QuandoSomaParcelasNaoBate() throws Exception {
                ParcelaDTO parcela1 = new ParcelaDTO(new BigDecimal("500.00"), LocalDate.now().plusDays(30));

                IniciarProducaoDTO dto = new IniciarProducaoDTO(
                                new BigDecimal("500.00"),  // Total incorreto
                                LocalDate.now(),
                                List.of(parcela1));

                when(orcamentoService.iniciarProducao(eq(1L), any(IniciarProducaoDTO.class)))
                                .thenThrow(new IllegalArgumentException("Soma não corresponde ao valor total"));

                mockMvc.perform(patch("/api/orcamentos/1/iniciar")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(dto)))
                                .andExpect(status().isBadRequest());
        }
}
