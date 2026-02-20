package com.madeirart.appMadeirart.modules.custos.service;

import com.madeirart.appMadeirart.modules.custos.dto.CustoFixoRequestDTO;
import com.madeirart.appMadeirart.modules.custos.dto.CustoFixoResponseDTO;
import com.madeirart.appMadeirart.modules.custos.entity.CustoFixo;
import com.madeirart.appMadeirart.modules.custos.repository.CustoFixoRepository;
import com.madeirart.appMadeirart.shared.enums.StatusCusto;
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
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Testes unitários para CustoFixoService
 */
@ExtendWith(MockitoExtension.class)
class CustoFixoServiceTest {

    @Mock
    private CustoFixoRepository custoFixoRepository;

    @InjectMocks
    private CustoFixoService custoFixoService;

    private CustoFixo custoFixo;
    private CustoFixoRequestDTO custoFixoRequestDTO;

    @BeforeEach
    void setUp() {
        custoFixo = CustoFixo.builder()
                .id(1L)
                .nome("Aluguel")
                .valor(new BigDecimal("2000.00"))
                .diaVencimento(5)
                .descricao("Aluguel da oficina")
                .ativo(true)
                .status(StatusCusto.PENDENTE)
                .createdAt(LocalDate.now())
                .updatedAt(LocalDate.now())
                .build();

        custoFixoRequestDTO = new CustoFixoRequestDTO(
                "Aluguel",
                new BigDecimal("2000.00"),
                5,
                "Aluguel da oficina");
    }

    @Test
    @DisplayName("Deve listar todos os custos fixos")
    void deveListarTodosCustosFixos() {
        CustoFixo custo2 = CustoFixo.builder()
                .id(2L)
                .nome("Energia")
                .valor(new BigDecimal("500.00"))
                .diaVencimento(10)
                .ativo(true)
                .status(StatusCusto.PAGO)
                .build();

        when(custoFixoRepository.findAllByOrderByNome())
                .thenReturn(List.of(custoFixo, custo2));

        List<CustoFixoResponseDTO> resultado = custoFixoService.listarTodos();

        assertThat(resultado).hasSize(2);
        assertThat(resultado.get(0).nome()).isEqualTo("Aluguel");
        assertThat(resultado.get(1).nome()).isEqualTo("Energia");
        verify(custoFixoRepository).findAllByOrderByNome();
    }

    @Test
    @DisplayName("Deve retornar lista vazia quando não há custos fixos")
    void deveRetornarListaVaziaQuandoNaoHaCustosFixos() {
        when(custoFixoRepository.findAllByOrderByNome())
                .thenReturn(List.of());

        List<CustoFixoResponseDTO> resultado = custoFixoService.listarTodos();

        assertThat(resultado).isEmpty();
        verify(custoFixoRepository).findAllByOrderByNome();
    }

    @Test
    @DisplayName("Deve listar apenas custos fixos ativos")
    void deveListarApenasCustosFixosAtivos() {
        when(custoFixoRepository.findByAtivoTrueOrderByNome())
                .thenReturn(List.of(custoFixo));

        List<CustoFixoResponseDTO> resultado = custoFixoService.listarAtivos();

        assertThat(resultado).hasSize(1);
        assertThat(resultado.get(0).ativo()).isTrue();
        verify(custoFixoRepository).findByAtivoTrueOrderByNome();
    }

    @Test
    @DisplayName("Deve listar custos fixos por período de dias")
    void deveListarCustosFixosPorPeriodoDias() {
        when(custoFixoRepository.findByAtivoTrueAndDiaVencimentoBetween(1, 15))
                .thenReturn(List.of(custoFixo));

        List<CustoFixoResponseDTO> resultado = custoFixoService.listarPorPeriodoDias(1, 15);

        assertThat(resultado).hasSize(1);
        assertThat(resultado.get(0).diaVencimento()).isEqualTo(5);
        verify(custoFixoRepository).findByAtivoTrueAndDiaVencimentoBetween(1, 15);
    }

    @Test
    @DisplayName("Deve listar custos fixos ativos ordenados por dia de vencimento")
    void deveListarCustosFixosAtivosPorDiaVencimento() {
        CustoFixo custo2 = CustoFixo.builder()
                .id(2L)
                .nome("Energia")
                .valor(new BigDecimal("500.00"))
                .diaVencimento(10)
                .ativo(true)
                .build();

        when(custoFixoRepository.findByAtivoTrueOrderByDiaVencimento())
                .thenReturn(List.of(custoFixo, custo2));

        List<CustoFixoResponseDTO> resultado = custoFixoService.listarAtivosPorDiaVencimento();

        assertThat(resultado).hasSize(2);
        assertThat(resultado.get(0).diaVencimento()).isEqualTo(5);
        assertThat(resultado.get(1).diaVencimento()).isEqualTo(10);
        verify(custoFixoRepository).findByAtivoTrueOrderByDiaVencimento();
    }

    @Test
    @DisplayName("Deve buscar custo fixo por ID com sucesso")
    void deveBuscarCustoFixoPorId() {
        when(custoFixoRepository.findById(1L)).thenReturn(Optional.of(custoFixo));

        CustoFixoResponseDTO resultado = custoFixoService.buscarPorId(1L);

        assertThat(resultado).isNotNull();
        assertThat(resultado.id()).isEqualTo(1L);
        assertThat(resultado.nome()).isEqualTo("Aluguel");
        assertThat(resultado.valor()).isEqualByComparingTo(new BigDecimal("2000.00"));
        assertThat(resultado.diaVencimento()).isEqualTo(5);
        assertThat(resultado.status()).isEqualTo(StatusCusto.PENDENTE);
        verify(custoFixoRepository).findById(1L);
    }

    @Test
    @DisplayName("Deve lançar exceção ao buscar custo fixo inexistente")
    void deveLancarExcecaoAoBuscarCustoFixoInexistente() {
        when(custoFixoRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> custoFixoService.buscarPorId(999L))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("Custo fixo não encontrado com ID: 999");
    }

    @Test
    @DisplayName("Deve criar custo fixo com sucesso")
    void deveCriarCustoFixoComSucesso() {
        when(custoFixoRepository.save(any(CustoFixo.class))).thenReturn(custoFixo);

        CustoFixoResponseDTO resultado = custoFixoService.criar(custoFixoRequestDTO);

        assertThat(resultado).isNotNull();
        assertThat(resultado.nome()).isEqualTo("Aluguel");
        assertThat(resultado.valor()).isEqualByComparingTo(new BigDecimal("2000.00"));
        assertThat(resultado.diaVencimento()).isEqualTo(5);
        assertThat(resultado.status()).isEqualTo(StatusCusto.PENDENTE);
        verify(custoFixoRepository).save(any(CustoFixo.class));
    }

    @Test
    @DisplayName("Deve atualizar custo fixo com sucesso")
    void deveAtualizarCustoFixoComSucesso() {
        CustoFixoRequestDTO requestAtualizado = new CustoFixoRequestDTO(
                "Aluguel Atualizado",
                new BigDecimal("2500.00"),
                5,
                "Descrição atualizada");

        when(custoFixoRepository.findById(1L)).thenReturn(Optional.of(custoFixo));
        when(custoFixoRepository.save(any(CustoFixo.class))).thenReturn(custoFixo);

        CustoFixoResponseDTO resultado = custoFixoService.atualizar(1L, requestAtualizado);

        assertThat(resultado).isNotNull();
        verify(custoFixoRepository).findById(1L);
        verify(custoFixoRepository).save(custoFixo);
    }

    @Test
    @DisplayName("Deve lançar exceção ao atualizar custo fixo inexistente")
    void deveLancarExcecaoAoAtualizarCustoFixoInexistente() {
        when(custoFixoRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> custoFixoService.atualizar(999L, custoFixoRequestDTO))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("Custo fixo não encontrado com ID: 999");

        verify(custoFixoRepository, never()).save(any());
    }

    @Test
    @DisplayName("Deve desativar custo fixo com sucesso")
    void deveDesativarCustoFixoComSucesso() {
        when(custoFixoRepository.findById(1L)).thenReturn(Optional.of(custoFixo));
        when(custoFixoRepository.save(any(CustoFixo.class))).thenReturn(custoFixo);

        custoFixoService.desativar(1L);

        assertThat(custoFixo.getAtivo()).isFalse();
        verify(custoFixoRepository).findById(1L);
        verify(custoFixoRepository).save(custoFixo);
    }

    @Test
    @DisplayName("Deve lançar exceção ao desativar custo fixo inexistente")
    void deveLancarExcecaoAoDesativarCustoFixoInexistente() {
        when(custoFixoRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> custoFixoService.desativar(999L))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("Custo fixo não encontrado com ID: 999");

        verify(custoFixoRepository, never()).save(any());
    }

    @Test
    @DisplayName("Deve reativar custo fixo com sucesso")
    void deveReativarCustoFixoComSucesso() {
        custoFixo.setAtivo(false);
        when(custoFixoRepository.findById(1L)).thenReturn(Optional.of(custoFixo));
        when(custoFixoRepository.save(any(CustoFixo.class))).thenReturn(custoFixo);

        CustoFixoResponseDTO resultado = custoFixoService.reativar(1L);

        assertThat(custoFixo.getAtivo()).isTrue();
        assertThat(resultado).isNotNull();
        verify(custoFixoRepository).findById(1L);
        verify(custoFixoRepository).save(custoFixo);
    }

    @Test
    @DisplayName("Deve lançar exceção ao reativar custo fixo inexistente")
    void deveLancarExcecaoAoReativarCustoFixoInexistente() {
        when(custoFixoRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> custoFixoService.reativar(999L))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("Custo fixo não encontrado com ID: 999");

        verify(custoFixoRepository, never()).save(any());
    }

    @Test
    @DisplayName("Deve excluir custo fixo com sucesso")
    void deveExcluirCustoFixoComSucesso() {
        when(custoFixoRepository.existsById(1L)).thenReturn(true);
        doNothing().when(custoFixoRepository).deleteById(1L);

        custoFixoService.excluir(1L);

        verify(custoFixoRepository).existsById(1L);
        verify(custoFixoRepository).deleteById(1L);
    }

    @Test
    @DisplayName("Deve lançar exceção ao excluir custo fixo inexistente")
    void deveLancarExcecaoAoExcluirCustoFixoInexistente() {
        when(custoFixoRepository.existsById(999L)).thenReturn(false);

        assertThatThrownBy(() -> custoFixoService.excluir(999L))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("Custo fixo não encontrado com ID: 999");

        verify(custoFixoRepository, never()).deleteById(any());
    }
}
