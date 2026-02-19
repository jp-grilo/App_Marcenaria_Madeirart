package com.madeirart.appMadeirart.modules.orcamento.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.madeirart.appMadeirart.modules.orcamento.dto.ItemMaterialDTO;
import com.madeirart.appMadeirart.modules.orcamento.dto.OrcamentoAuditoriaDTO;
import com.madeirart.appMadeirart.modules.orcamento.dto.OrcamentoRequestDTO;
import com.madeirart.appMadeirart.modules.orcamento.dto.OrcamentoResponseDTO;
import com.madeirart.appMadeirart.modules.orcamento.entity.Orcamento;
import com.madeirart.appMadeirart.modules.orcamento.entity.OrcamentoAuditoria;
import com.madeirart.appMadeirart.modules.orcamento.repository.OrcamentoAuditoriaRepository;
import com.madeirart.appMadeirart.modules.orcamento.repository.OrcamentoRepository;
import com.madeirart.appMadeirart.shared.enums.StatusOrcamento;

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

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Testes essenciais do OrcamentoService
 * Foca apenas nos cenários críticos de lógica de negócio
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("Testes do OrcamentoService")
class OrcamentoServiceTest {

    @Mock
    private OrcamentoRepository orcamentoRepository;

    @Mock
    private OrcamentoAuditoriaRepository auditoriaRepository;

    @Mock
    private ObjectMapper objectMapper;

    @InjectMocks
    private OrcamentoService orcamentoService;

    private OrcamentoRequestDTO requestDTO;
    private Orcamento orcamento;

    @BeforeEach
    void setUp() {
        ItemMaterialDTO item = new ItemMaterialDTO(
                null,
                new BigDecimal("4"),
                "Placa MDF 15mm",
                new BigDecimal("180.00"),
                null
        );

        requestDTO = new OrcamentoRequestDTO(
                "João Silva",
                "Armário planejado",
                LocalDate.now(),
                LocalDate.now().plusDays(30),
                new BigDecimal("1.5"),
                new BigDecimal("250.00"),
                new BigDecimal("150.00"),
                List.of(item)
        );

        orcamento = new Orcamento();
        orcamento.setId(1L);
        orcamento.setCliente("João Silva");
        orcamento.setMoveis("Armário planejado");
        orcamento.setData(LocalDate.now());
        orcamento.setPrevisaoEntrega(LocalDate.now().plusDays(30));
        orcamento.setStatus(StatusOrcamento.AGUARDANDO);
        orcamento.setFatorMaoDeObra(new BigDecimal("1.5"));
        orcamento.setCustosExtras(new BigDecimal("250.00"));
        orcamento.setCpc(new BigDecimal("150.00"));
    }

    @Test
    @DisplayName("Deve criar orçamento com sucesso")
    void deveCriarOrcamento() {
        when(orcamentoRepository.save(any(Orcamento.class))).thenReturn(orcamento);

        OrcamentoResponseDTO response = orcamentoService.criarOrcamento(requestDTO);

        assertThat(response).isNotNull();
        assertThat(response.id()).isEqualTo(1L);
        assertThat(response.cliente()).isEqualTo("João Silva");
        verify(orcamentoRepository).save(any(Orcamento.class));
    }

    @Test
    @DisplayName("Deve buscar orçamento por ID")
    void deveBuscarPorId() {
        when(orcamentoRepository.findById(1L)).thenReturn(Optional.of(orcamento));

        OrcamentoResponseDTO response = orcamentoService.buscarPorId(1L);

        assertThat(response).isNotNull();
        assertThat(response.id()).isEqualTo(1L);
    }

    @Test
    @DisplayName("Deve lançar exceção ao buscar orçamento inexistente")
    void deveLancarExcecaoQuandoNaoEncontrado() {
        when(orcamentoRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> orcamentoService.buscarPorId(999L))
                .isInstanceOf(EntityNotFoundException.class);
    }

    @Test
    @DisplayName("Deve listar orçamentos por status")
    void deveListarPorStatus() {
        when(orcamentoRepository.findByStatus(StatusOrcamento.AGUARDANDO))
                .thenReturn(List.of(orcamento));

        List<OrcamentoResponseDTO> response = orcamentoService.listarPorStatus(StatusOrcamento.AGUARDANDO);

        assertThat(response).hasSize(1);
        assertThat(response.get(0).status()).isEqualTo(StatusOrcamento.AGUARDANDO);
    }

    @Test
    @DisplayName("Deve atualizar orçamento")
    void deveAtualizarOrcamento() {
        when(orcamentoRepository.findById(1L)).thenReturn(Optional.of(orcamento));
        when(orcamentoRepository.save(any(Orcamento.class))).thenReturn(orcamento);

        OrcamentoResponseDTO response = orcamentoService.atualizarOrcamento(1L, requestDTO);

        assertThat(response).isNotNull();
        verify(orcamentoRepository).save(any(Orcamento.class));
    }

    @Test
    @DisplayName("Deve lançar exceção ao atualizar orçamento inexistente")
    void deveLancarExcecaoAoAtualizar() {
        when(orcamentoRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> orcamentoService.atualizarOrcamento(999L, requestDTO))
                .isInstanceOf(EntityNotFoundException.class);
    }

    @Test
    @DisplayName("Deve deletar orçamento")
    void deveDeletarOrcamento() {
        when(orcamentoRepository.existsById(1L)).thenReturn(true);

        orcamentoService.deletarOrcamento(1L);

        verify(orcamentoRepository).deleteById(1L);
    }

    @Test
    @DisplayName("Deve salvar auditoria ao atualizar orçamento")
    void deveSalvarAuditoriaAoAtualizar() throws Exception {
        when(orcamentoRepository.findById(1L)).thenReturn(Optional.of(orcamento));
        when(orcamentoRepository.save(any(Orcamento.class))).thenReturn(orcamento);
        when(objectMapper.writeValueAsString(any())).thenReturn("{\"id\":1}");
        when(auditoriaRepository.save(any(OrcamentoAuditoria.class))).thenReturn(new OrcamentoAuditoria());

        orcamentoService.atualizarOrcamento(1L, requestDTO);

        verify(auditoriaRepository).save(any(OrcamentoAuditoria.class));
    }

    @Test
    @DisplayName("Deve buscar histórico de auditoria")
    void deveBuscarHistorico() {
        OrcamentoAuditoria auditoria1 = OrcamentoAuditoria.builder()
                .id(1L)
                .orcamentoId(1L)
                .snapshotJson("{\"id\":1,\"cliente\":\"João Silva\"}")
                .dataAlteracao(LocalDateTime.now())
                .descricaoAlteracao("Atualização de orçamento")
                .build();

        OrcamentoAuditoria auditoria2 = OrcamentoAuditoria.builder()
                .id(2L)
                .orcamentoId(1L)
                .snapshotJson("{\"id\":1,\"cliente\":\"João Silva Atualizado\"}")
                .dataAlteracao(LocalDateTime.now().plusHours(1))
                .descricaoAlteracao("Atualização de orçamento")
                .build();

        when(orcamentoRepository.existsById(1L)).thenReturn(true);
        when(auditoriaRepository.findByOrcamentoIdOrderByDataAlteracaoDesc(1L))
                .thenReturn(List.of(auditoria2, auditoria1));

        List<OrcamentoAuditoriaDTO> historico = orcamentoService.buscarHistorico(1L);

        assertThat(historico).hasSize(2);
        assertThat(historico.get(0).id()).isEqualTo(2L);
        assertThat(historico.get(1).id()).isEqualTo(1L);
        verify(auditoriaRepository).findByOrcamentoIdOrderByDataAlteracaoDesc(1L);
    }

    @Test
    @DisplayName("Deve lançar exceção ao buscar histórico de orçamento inexistente")
    void deveLancarExcecaoAoBuscarHistoricoDeOrcamentoInexistente() {
        when(orcamentoRepository.existsById(999L)).thenReturn(false);

        assertThatThrownBy(() -> orcamentoService.buscarHistorico(999L))
                .isInstanceOf(EntityNotFoundException.class);
    }

    @Test
    @DisplayName("Deve retornar histórico vazio para orçamento sem alterações")
    void deveRetornarHistoricoVazio() {
        when(orcamentoRepository.existsById(1L)).thenReturn(true);
        when(auditoriaRepository.findByOrcamentoIdOrderByDataAlteracaoDesc(1L))
                .thenReturn(List.of());

        List<OrcamentoAuditoriaDTO> historico = orcamentoService.buscarHistorico(1L);

        assertThat(historico).isEmpty();
    }
}
