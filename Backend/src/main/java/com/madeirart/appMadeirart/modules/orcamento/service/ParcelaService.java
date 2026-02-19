package com.madeirart.appMadeirart.modules.orcamento.service;

import com.madeirart.appMadeirart.modules.orcamento.dto.ParcelaResponseDTO;
import com.madeirart.appMadeirart.modules.orcamento.entity.Parcela;
import com.madeirart.appMadeirart.modules.orcamento.repository.ParcelaRepository;
import com.madeirart.appMadeirart.shared.enums.StatusParcela;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Service para gerenciamento de parcelas
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ParcelaService {

    private final ParcelaRepository parcelaRepository;

    /**
     * Busca todas as parcelas de um orçamento
     */
    @Transactional(readOnly = true)
    public List<ParcelaResponseDTO> listarPorOrcamento(Long orcamentoId) {
        return parcelaRepository.findByOrcamentoIdOrderByNumeroParcela(orcamentoId)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Busca uma parcela por ID
     */
    @Transactional(readOnly = true)
    public ParcelaResponseDTO buscarPorId(Long id) {
        Parcela parcela = parcelaRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Parcela não encontrada com ID: " + id));
        return convertToDTO(parcela);
    }

    /**
     * Confirma o pagamento de uma parcela
     */
    @Transactional
    public ParcelaResponseDTO confirmarPagamento(Long id) {
        Parcela parcela = parcelaRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Parcela não encontrada com ID: " + id));

        if (parcela.getStatus() == StatusParcela.PAGO) {
            throw new IllegalStateException("Parcela já foi confirmada como paga");
        }

        parcela.setStatus(StatusParcela.PAGO);
        parcela.setDataPagamento(LocalDate.now());

        Parcela saved = parcelaRepository.save(parcela);
        return convertToDTO(saved);
    }

    /**
     * Atualiza o status de parcelas pendentes com vencimento vencido para ATRASADO
     * 
     * @return Quantidade de parcelas atualizadas
     */
    @Transactional
    public int atualizarParcelasAtrasadas() {
        List<Parcela> parcelasAtrasadas = parcelaRepository
                .findByStatusAndDataVencimentoBefore(StatusParcela.PENDENTE, LocalDate.now());

        if (parcelasAtrasadas.isEmpty()) {
            log.info("Nenhuma parcela atrasada encontrada");
            return 0;
        }

        parcelasAtrasadas.forEach(parcela -> parcela.setStatus(StatusParcela.ATRASADO));
        parcelaRepository.saveAll(parcelasAtrasadas);

        log.info("Total de {} parcela(s) atualizada(s) para status ATRASADO", parcelasAtrasadas.size());
        return parcelasAtrasadas.size();
    }

    /**
     * Converte entidade para DTO
     */
    private ParcelaResponseDTO convertToDTO(Parcela parcela) {
        return ParcelaResponseDTO.builder()
                .id(parcela.getId())
                .orcamentoId(parcela.getOrcamento().getId())
                .numeroParcela(parcela.getNumeroParcela())
                .valor(parcela.getValor())
                .dataVencimento(parcela.getDataVencimento())
                .dataPagamento(parcela.getDataPagamento())
                .status(parcela.getStatus())
                .createdAt(parcela.getCreatedAt())
                .build();
    }
}
