package com.madeirart.appMadeirart.modules.orcamento.service;

import com.madeirart.appMadeirart.modules.orcamento.dto.ParcelaResponseDTO;
import com.madeirart.appMadeirart.modules.orcamento.entity.Orcamento;
import com.madeirart.appMadeirart.modules.orcamento.entity.Parcela;
import com.madeirart.appMadeirart.modules.orcamento.repository.ParcelaRepository;
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
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Testes unitários para ParcelaService
 */
@ExtendWith(MockitoExtension.class)
class ParcelaServiceTest {

    @Mock
    private ParcelaRepository parcelaRepository;

    @InjectMocks
    private ParcelaService parcelaService;

    private Orcamento orcamento;
    private Parcela parcela;

    @BeforeEach
    void setUp() {
        orcamento = new Orcamento();
        orcamento.setId(1L);

        parcela = Parcela.builder()
                .id(1L)
                .orcamento(orcamento)
                .numeroParcela(1)
                .valor(new BigDecimal("1000.00"))
                .dataVencimento(LocalDate.now().plusDays(30))
                .status(StatusParcela.PENDENTE)
                .createdAt(LocalDate.now())
                .build();
    }

    @Test
    @DisplayName("Deve listar parcelas por orçamento")
    void deveListarParcelasPorOrcamento() {
        Parcela parcela2 = Parcela.builder()
                .id(2L)
                .orcamento(orcamento)
                .numeroParcela(2)
                .valor(new BigDecimal("1000.00"))
                .dataVencimento(LocalDate.now().plusDays(60))
                .status(StatusParcela.PENDENTE)
                .build();

        when(parcelaRepository.findByOrcamentoIdOrderByNumeroParcela(1L))
                .thenReturn(List.of(parcela, parcela2));

        List<ParcelaResponseDTO> resultado = parcelaService.listarPorOrcamento(1L);

        assertThat(resultado).hasSize(2);
        assertThat(resultado.get(0).numeroParcela()).isEqualTo(1);
        assertThat(resultado.get(1).numeroParcela()).isEqualTo(2);
        verify(parcelaRepository).findByOrcamentoIdOrderByNumeroParcela(1L);
    }

    @Test
    @DisplayName("Deve buscar parcela por ID com sucesso")
    void deveBuscarParcelaPorId() {
        when(parcelaRepository.findById(1L)).thenReturn(Optional.of(parcela));

        ParcelaResponseDTO resultado = parcelaService.buscarPorId(1L);

        assertThat(resultado).isNotNull();
        assertThat(resultado.id()).isEqualTo(1L);
        assertThat(resultado.numeroParcela()).isEqualTo(1);
        assertThat(resultado.valor()).isEqualByComparingTo(new BigDecimal("1000.00"));
        verify(parcelaRepository).findById(1L);
    }

    @Test
    @DisplayName("Deve lançar exceção ao buscar parcela inexistente")
    void deveLancarExcecaoAoBuscarParcelaInexistente() {
        when(parcelaRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> parcelaService.buscarPorId(999L))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("Parcela não encontrada com ID: 999");
    }

    @Test
    @DisplayName("Deve confirmar pagamento de parcela com sucesso")
    void deveConfirmarPagamento() {
        when(parcelaRepository.findById(1L)).thenReturn(Optional.of(parcela));
        when(parcelaRepository.save(any(Parcela.class))).thenReturn(parcela);

        ParcelaResponseDTO resultado = parcelaService.confirmarPagamento(1L);

        assertThat(resultado).isNotNull();
        assertThat(resultado.status()).isEqualTo(StatusParcela.PAGO);
        assertThat(resultado.dataPagamento()).isNotNull();
        assertThat(resultado.dataPagamento()).isEqualTo(LocalDate.now());
        verify(parcelaRepository).findById(1L);
        verify(parcelaRepository).save(parcela);
    }

    @Test
    @DisplayName("Deve lançar exceção ao confirmar parcela já paga")
    void deveLancarExcecaoAoConfirmarParcelaJaPaga() {
        parcela.setStatus(StatusParcela.PAGO);
        parcela.setDataPagamento(LocalDate.now().minusDays(5));

        when(parcelaRepository.findById(1L)).thenReturn(Optional.of(parcela));

        assertThatThrownBy(() -> parcelaService.confirmarPagamento(1L))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Parcela já foi confirmada como paga");

        verify(parcelaRepository, never()).save(any());
    }

    @Test
    @DisplayName("Deve lançar exceção ao confirmar parcela inexistente")
    void deveLancarExcecaoAoConfirmarParcelaInexistente() {
        when(parcelaRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> parcelaService.confirmarPagamento(999L))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("Parcela não encontrada com ID: 999");

        verify(parcelaRepository, never()).save(any());
    }

    @Test
    @DisplayName("Deve retornar lista vazia quando orçamento não tem parcelas")
    void deveRetornarListaVaziaQuandoOrcamentoNaoTemParcelas() {
        when(parcelaRepository.findByOrcamentoIdOrderByNumeroParcela(1L))
                .thenReturn(List.of());

        List<ParcelaResponseDTO> resultado = parcelaService.listarPorOrcamento(1L);

        assertThat(resultado).isEmpty();
        verify(parcelaRepository).findByOrcamentoIdOrderByNumeroParcela(1L);
    }
}
