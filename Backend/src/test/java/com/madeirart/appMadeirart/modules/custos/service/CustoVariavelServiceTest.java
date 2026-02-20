package com.madeirart.appMadeirart.modules.custos.service;

import com.madeirart.appMadeirart.modules.custos.dto.CustoVariavelRequestDTO;
import com.madeirart.appMadeirart.modules.custos.dto.CustoVariavelResponseDTO;
import com.madeirart.appMadeirart.modules.custos.entity.CustoVariavel;
import com.madeirart.appMadeirart.modules.custos.repository.CustoVariavelRepository;
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
 * Testes unitários para CustoVariavelService
 */
@ExtendWith(MockitoExtension.class)
class CustoVariavelServiceTest {

    @Mock
    private CustoVariavelRepository custoVariavelRepository;

    @InjectMocks
    private CustoVariavelService custoVariavelService;

    private CustoVariavel custoVariavel;
    private CustoVariavelRequestDTO custoVariavelRequestDTO;

    @BeforeEach
    void setUp() {
        custoVariavel = CustoVariavel.builder()
                .id(1L)
                .nome("Material de Construção")
                .valor(new BigDecimal("500.00"))
                .dataLancamento(LocalDate.now())
                .descricao("Compra de materiais")
                .status(StatusCusto.PENDENTE)
                .parcelado(false)
                .createdAt(LocalDate.now())
                .updatedAt(LocalDate.now())
                .build();

        custoVariavelRequestDTO = new CustoVariavelRequestDTO(
                "Material de Construção",
                new BigDecimal("500.00"),
                LocalDate.now(),
                "Compra de materiais",
                null);
    }

    @Test
    @DisplayName("Deve listar todos os custos variáveis")
    void deveListarTodosCustosVariaveis() {
        CustoVariavel custo2 = CustoVariavel.builder()
                .id(2L)
                .nome("Transporte")
                .valor(new BigDecimal("200.00"))
                .dataLancamento(LocalDate.now().minusDays(1))
                .status(StatusCusto.PAGO)
                .parcelado(false)
                .build();

        when(custoVariavelRepository.findAllByOrderByDataLancamentoDesc())
                .thenReturn(List.of(custoVariavel, custo2));

        List<CustoVariavelResponseDTO> resultado = custoVariavelService.listarTodos();

        assertThat(resultado).hasSize(2);
        assertThat(resultado.get(0).nome()).isEqualTo("Material de Construção");
        assertThat(resultado.get(1).nome()).isEqualTo("Transporte");
        verify(custoVariavelRepository).findAllByOrderByDataLancamentoDesc();
    }

    @Test
    @DisplayName("Deve retornar lista vazia quando não há custos variáveis")
    void deveRetornarListaVaziaQuandoNaoHaCustosVariaveis() {
        when(custoVariavelRepository.findAllByOrderByDataLancamentoDesc())
                .thenReturn(List.of());

        List<CustoVariavelResponseDTO> resultado = custoVariavelService.listarTodos();

        assertThat(resultado).isEmpty();
        verify(custoVariavelRepository).findAllByOrderByDataLancamentoDesc();
    }

    @Test
    @DisplayName("Deve listar custos variáveis por período")
    void deveListarCustosVariaveisPorPeriodo() {
        LocalDate dataInicio = LocalDate.now().minusDays(7);
        LocalDate dataFim = LocalDate.now();

        when(custoVariavelRepository.findByDataLancamentoBetween(dataInicio, dataFim))
                .thenReturn(List.of(custoVariavel));

        List<CustoVariavelResponseDTO> resultado = custoVariavelService.listarPorPeriodo(dataInicio, dataFim);

        assertThat(resultado).hasSize(1);
        assertThat(resultado.get(0).nome()).isEqualTo("Material de Construção");
        verify(custoVariavelRepository).findByDataLancamentoBetween(dataInicio, dataFim);
    }

    @Test
    @DisplayName("Deve buscar custo variável por ID com sucesso")
    void deveBuscarCustoVariavelPorId() {
        when(custoVariavelRepository.findById(1L)).thenReturn(Optional.of(custoVariavel));

        CustoVariavelResponseDTO resultado = custoVariavelService.buscarPorId(1L);

        assertThat(resultado).isNotNull();
        assertThat(resultado.id()).isEqualTo(1L);
        assertThat(resultado.nome()).isEqualTo("Material de Construção");
        assertThat(resultado.valor()).isEqualByComparingTo(new BigDecimal("500.00"));
        assertThat(resultado.status()).isEqualTo(StatusCusto.PENDENTE);
        verify(custoVariavelRepository).findById(1L);
    }

    @Test
    @DisplayName("Deve lançar exceção ao buscar custo variável inexistente")
    void deveLancarExcecaoAoBuscarCustoVariavelInexistente() {
        when(custoVariavelRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> custoVariavelService.buscarPorId(999L))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("Custo variável não encontrado com ID: 999");
    }

    @Test
    @DisplayName("Deve criar custo variável com sucesso")
    void deveCriarCustoVariavelComSucesso() {
        when(custoVariavelRepository.save(any(CustoVariavel.class))).thenReturn(custoVariavel);

        List<CustoVariavelResponseDTO> resultado = custoVariavelService.criar(custoVariavelRequestDTO);

        assertThat(resultado).isNotNull();
        assertThat(resultado).hasSize(1);
        assertThat(resultado.get(0).nome()).isEqualTo("Material de Construção");
        assertThat(resultado.get(0).valor()).isEqualByComparingTo(new BigDecimal("500.00"));
        assertThat(resultado.get(0).status()).isEqualTo(StatusCusto.PENDENTE);
        verify(custoVariavelRepository).save(any(CustoVariavel.class));
    }

    @Test
    @DisplayName("Deve criar custo variável parcelado com sucesso")
    void deveCriarCustoVariavelParceladoComSucesso() {
        CustoVariavelRequestDTO requestParcelado = new CustoVariavelRequestDTO(
                "Máquina de cola",
                new BigDecimal("1000.00"),
                LocalDate.now(),
                "Compra parcelada",
                5);

        CustoVariavel parcela1 = CustoVariavel.builder()
                .id(1L)
                .nome("Máquina de cola (1/5)")
                .valor(new BigDecimal("200.00"))
                .dataLancamento(LocalDate.now())
                .parcelado(true)
                .numeroParcela(1)
                .totalParcelas(5)
                .custoOrigemId(1L)
                .status(StatusCusto.PENDENTE)
                .build();

        when(custoVariavelRepository.save(any(CustoVariavel.class))).thenReturn(parcela1);

        List<CustoVariavelResponseDTO> resultado = custoVariavelService.criar(requestParcelado);

        assertThat(resultado).isNotNull();
        assertThat(resultado).hasSize(5);
        verify(custoVariavelRepository, times(6)).save(any(CustoVariavel.class)); // 5 parcelas + 1 update da primeira
    }

    @Test
    @DisplayName("Deve atualizar custo variável com sucesso")
    void deveAtualizarCustoVariavelComSucesso() {
        CustoVariavelRequestDTO requestAtualizado = new CustoVariavelRequestDTO(
                "Material de Construção Atualizado",
                new BigDecimal("600.00"),
                LocalDate.now(),
                "Descrição atualizada",
                null);

        when(custoVariavelRepository.findById(1L)).thenReturn(Optional.of(custoVariavel));
        when(custoVariavelRepository.save(any(CustoVariavel.class))).thenReturn(custoVariavel);

        CustoVariavelResponseDTO resultado = custoVariavelService.atualizar(1L, requestAtualizado);

        assertThat(resultado).isNotNull();
        verify(custoVariavelRepository).findById(1L);
        verify(custoVariavelRepository).save(custoVariavel);
    }

    @Test
    @DisplayName("Deve lançar exceção ao atualizar custo variável inexistente")
    void deveLancarExcecaoAoAtualizarCustoVariavelInexistente() {
        when(custoVariavelRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> custoVariavelService.atualizar(999L, custoVariavelRequestDTO))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("Custo variável não encontrado com ID: 999");

        verify(custoVariavelRepository, never()).save(any());
    }

    @Test
    @DisplayName("Deve excluir custo variável com sucesso")
    void deveExcluirCustoVariavelComSucesso() {
        when(custoVariavelRepository.existsById(1L)).thenReturn(true);
        doNothing().when(custoVariavelRepository).deleteById(1L);

        custoVariavelService.excluir(1L);

        verify(custoVariavelRepository).existsById(1L);
        verify(custoVariavelRepository).deleteById(1L);
    }

    @Test
    @DisplayName("Deve lançar exceção ao excluir custo variável inexistente")
    void deveLancarExcecaoAoExcluirCustoVariavelInexistente() {
        when(custoVariavelRepository.existsById(999L)).thenReturn(false);

        assertThatThrownBy(() -> custoVariavelService.excluir(999L))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("Custo variável não encontrado com ID: 999");

        verify(custoVariavelRepository, never()).deleteById(any());
    }
}
