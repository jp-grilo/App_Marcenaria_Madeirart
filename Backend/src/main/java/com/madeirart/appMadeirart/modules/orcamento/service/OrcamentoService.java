package com.madeirart.appMadeirart.modules.orcamento.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.madeirart.appMadeirart.modules.orcamento.dto.ItemMaterialDTO;
import com.madeirart.appMadeirart.modules.orcamento.dto.IniciarProducaoDTO;
import com.madeirart.appMadeirart.modules.orcamento.dto.OrcamentoAuditoriaDTO;
import com.madeirart.appMadeirart.modules.orcamento.dto.OrcamentoRequestDTO;
import com.madeirart.appMadeirart.modules.orcamento.dto.OrcamentoResponseDTO;
import com.madeirart.appMadeirart.modules.orcamento.dto.ParcelaResponseDTO;
import com.madeirart.appMadeirart.modules.orcamento.dto.StatusRecebimentoDTO;
import com.madeirart.appMadeirart.modules.orcamento.entity.ItemMaterial;
import com.madeirart.appMadeirart.modules.orcamento.entity.Orcamento;
import com.madeirart.appMadeirart.modules.orcamento.entity.OrcamentoAuditoria;
import com.madeirart.appMadeirart.modules.orcamento.entity.Parcela;
import com.madeirart.appMadeirart.modules.orcamento.repository.OrcamentoAuditoriaRepository;
import com.madeirart.appMadeirart.modules.orcamento.repository.OrcamentoRepository;
import com.madeirart.appMadeirart.modules.orcamento.repository.ParcelaRepository;
import com.madeirart.appMadeirart.shared.enums.StatusOrcamento;
import com.madeirart.appMadeirart.shared.enums.StatusParcela;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Service para gerenciamento de orçamentos
 */
@Service
@RequiredArgsConstructor
public class OrcamentoService {

    private final OrcamentoRepository orcamentoRepository;
    private final OrcamentoAuditoriaRepository auditoriaRepository;
    private final ParcelaRepository parcelaRepository;
    private final ObjectMapper objectMapper;

    /**
     * Cria um novo orçamento
     */
    @Transactional
    public OrcamentoResponseDTO criarOrcamento(OrcamentoRequestDTO dto) {
        Orcamento orcamento = convertToEntity(dto);
        Orcamento saved = orcamentoRepository.save(orcamento);
        return convertToResponseDTO(saved);
    }

    /**
     * Busca um orçamento por ID
     */
    @Transactional(readOnly = true)
    public OrcamentoResponseDTO buscarPorId(Long id) {
        Orcamento orcamento = orcamentoRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Orçamento não encontrado com ID: " + id));
        return convertToResponseDTO(orcamento);
    }

    /**
     * Lista todos os orçamentos
     */
    @Transactional(readOnly = true)
    public List<OrcamentoResponseDTO> listarTodos() {
        return orcamentoRepository.findAll().stream()
                .map(this::convertToResponseDTO)
                .collect(Collectors.toList());
    }

    /**
     * Lista orçamentos por status
     */
    @Transactional(readOnly = true)
    public List<OrcamentoResponseDTO> listarPorStatus(StatusOrcamento status) {
        return orcamentoRepository.findByStatus(status).stream()
                .map(this::convertToResponseDTO)
                .collect(Collectors.toList());
    }

    /**
     * Atualiza um orçamento existente
     * Antes de atualizar, salva um snapshot do estado anterior na tabela de
     * auditoria
     */
    @Transactional
    public OrcamentoResponseDTO atualizarOrcamento(Long id, OrcamentoRequestDTO dto) {
        Orcamento orcamento = orcamentoRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Orçamento não encontrado com ID: " + id));

        salvarAuditoria(orcamento);

        orcamento.setCliente(dto.cliente());
        orcamento.setMoveis(dto.moveis());
        orcamento.setData(dto.data());
        orcamento.setPrevisaoEntrega(dto.previsaoEntrega());
        orcamento.setFatorMaoDeObra(dto.fatorMaoDeObra());
        orcamento.setCustosExtras(dto.custosExtras());
        orcamento.setCpc(dto.cpc());

        orcamento.getItens().clear();

        dto.itens().forEach(itemDTO -> {
            ItemMaterial item = new ItemMaterial();
            item.setQuantidade(itemDTO.quantidade());
            item.setDescricao(itemDTO.descricao());
            item.setValorUnitario(itemDTO.valorUnitario());
            orcamento.adicionarItem(item);
        });

        Orcamento saved = orcamentoRepository.save(orcamento);
        return convertToResponseDTO(saved);
    }

    /**
     * Deleta um orçamento
     */
    @Transactional
    public void deletarOrcamento(Long id) {
        if (!orcamentoRepository.existsById(id)) {
            throw new EntityNotFoundException("Orçamento não encontrado com ID: " + id);
        }
        orcamentoRepository.deleteById(id);
    }

    /**
     * Converte DTO de request para entidade
     */
    private Orcamento convertToEntity(OrcamentoRequestDTO dto) {
        Orcamento orcamento = new Orcamento();
        orcamento.setCliente(dto.cliente());
        orcamento.setMoveis(dto.moveis());
        orcamento.setData(dto.data());
        orcamento.setPrevisaoEntrega(dto.previsaoEntrega());
        orcamento.setFatorMaoDeObra(dto.fatorMaoDeObra());
        orcamento.setCustosExtras(dto.custosExtras());
        orcamento.setCpc(dto.cpc());

        dto.itens().forEach(itemDTO -> {
            ItemMaterial item = new ItemMaterial();
            item.setQuantidade(itemDTO.quantidade());
            item.setDescricao(itemDTO.descricao());
            item.setValorUnitario(itemDTO.valorUnitario());
            orcamento.adicionarItem(item);
        });

        return orcamento;
    }

    /**
     * Converte entidade para DTO de response
     */
    private OrcamentoResponseDTO convertToResponseDTO(Orcamento orcamento) {
        List<ItemMaterialDTO> itensDTO = orcamento.getItens().stream()
                .map(item -> new ItemMaterialDTO(
                        item.getId(),
                        item.getQuantidade(),
                        item.getDescricao(),
                        item.getValorUnitario(),
                        item.calcularSubtotal()))
                .collect(Collectors.toList());

        BigDecimal valorTotal = orcamento.calcularValorTotal();
        StatusRecebimentoDTO statusRecebimento = calcularStatusRecebimento(orcamento.getId(), valorTotal);

        return OrcamentoResponseDTO.builder()
                .id(orcamento.getId())
                .cliente(orcamento.getCliente())
                .moveis(orcamento.getMoveis())
                .data(orcamento.getData())
                .previsaoEntrega(orcamento.getPrevisaoEntrega())
                .fatorMaoDeObra(orcamento.getFatorMaoDeObra())
                .custosExtras(orcamento.getCustosExtras())
                .cpc(orcamento.getCpc())
                .status(orcamento.getStatus())
                .itens(itensDTO)
                .subtotalMateriais(orcamento.calcularSubtotalMateriais())
                .valorMaoDeObra(orcamento.calcularValorMaoDeObra())
                .valorTotal(valorTotal)
                .statusRecebimento(statusRecebimento)
                .createdAt(orcamento.getCreatedAt())
                .updatedAt(orcamento.getUpdatedAt())
                .build();
    }

    /**
     * Salva um snapshot do orçamento para auditoria
     */
    private void salvarAuditoria(Orcamento orcamento) {
        try {
            OrcamentoResponseDTO dto = convertToResponseDTO(orcamento);

            String snapshotJson = objectMapper.writeValueAsString(dto);

            OrcamentoAuditoria auditoria = OrcamentoAuditoria.builder()
                    .orcamentoId(orcamento.getId())
                    .snapshotJson(snapshotJson)
                    .dataAlteracao(LocalDateTime.now())
                    .descricaoAlteracao("Atualização de orçamento")
                    .build();

            auditoriaRepository.save(auditoria);
        } catch (Exception e) {
            System.err.println("Erro ao salvar auditoria: " + e.getMessage());
        }
    }

    /**
     * Inicia a produção de um orçamento e define o plano de parcelas
     */
    @Transactional
    public OrcamentoResponseDTO iniciarProducao(Long id, IniciarProducaoDTO dto) {
        Orcamento orcamento = orcamentoRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Orçamento não encontrado com ID: " + id));

        if (orcamento.getStatus() != StatusOrcamento.AGUARDANDO) {
            throw new IllegalStateException(
                    "Orçamento deve estar com status AGUARDANDO para iniciar produção. Status atual: "
                            + orcamento.getStatus());
        }

        BigDecimal valorTotal = orcamento.calcularValorTotal();
        BigDecimal somaParcelas = dto.parcelas() != null && !dto.parcelas().isEmpty()
                ? dto.parcelas().stream()
                        .map(p -> p.valor())
                        .reduce(BigDecimal.ZERO, BigDecimal::add)
                : BigDecimal.ZERO;
        BigDecimal somaEntradaEParcelas = dto.valorEntrada().add(somaParcelas);

        if (somaEntradaEParcelas.compareTo(valorTotal) != 0) {
            throw new IllegalArgumentException(
                    String.format(
                            "A soma da entrada (%s) e parcelas (%s) deve ser igual ao valor total do orçamento (%s)",
                            dto.valorEntrada(),
                            somaParcelas,
                            valorTotal));
        }

        salvarAuditoria(orcamento);

        orcamento.setStatus(StatusOrcamento.INICIADA);

        Parcela entrada = Parcela.builder()
                .orcamento(orcamento)
                .numeroParcela(1)
                .valor(dto.valorEntrada())
                .dataVencimento(dto.dataEntrada())
                .build();
        parcelaRepository.save(entrada);

        if (dto.parcelas() != null && !dto.parcelas().isEmpty()) {
            List<Parcela> parcelas = new ArrayList<>();
            for (int i = 0; i < dto.parcelas().size(); i++) {
                var parcelaDTO = dto.parcelas().get(i);
                Parcela parcela = Parcela.builder()
                        .orcamento(orcamento)
                        .numeroParcela(i + 2)
                        .valor(parcelaDTO.valor())
                        .dataVencimento(parcelaDTO.dataVencimento())
                        .build();
                parcelas.add(parcela);
            }
            parcelaRepository.saveAll(parcelas);
        }

        orcamento = orcamentoRepository.save(orcamento);

        return convertToResponseDTO(orcamento);
    }

    /**
     * Altera o status de um orçamento
     */
    @Transactional
    public OrcamentoResponseDTO alterarStatus(Long id, StatusOrcamento novoStatus) {
        Orcamento orcamento = orcamentoRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Orçamento não encontrado com ID: " + id));

        salvarAuditoria(orcamento);
        orcamento.setStatus(novoStatus);
        orcamento = orcamentoRepository.save(orcamento);

        return convertToResponseDTO(orcamento);
    }

    /**
     * Busca o histórico de auditoria de um orçamento
     */
    @Transactional(readOnly = true)
    public List<OrcamentoAuditoriaDTO> buscarHistorico(Long orcamentoId) {
        if (!orcamentoRepository.existsById(orcamentoId)) {
            throw new EntityNotFoundException("Orçamento não encontrado com ID: " + orcamentoId);
        }

        return auditoriaRepository.findByOrcamentoIdOrderByDataAlteracaoDesc(orcamentoId)
                .stream()
                .map(this::convertToAuditoriaDTO)
                .collect(Collectors.toList());
    }

    /**
     * Converte entidade de auditoria para DTO
     */
    private OrcamentoAuditoriaDTO convertToAuditoriaDTO(OrcamentoAuditoria auditoria) {
        return OrcamentoAuditoriaDTO.builder()
                .id(auditoria.getId())
                .orcamentoId(auditoria.getOrcamentoId())
                .snapshotJson(auditoria.getSnapshotJson())
                .dataAlteracao(auditoria.getDataAlteracao())
                .descricaoAlteracao(auditoria.getDescricaoAlteracao())
                .build();
    }

    /**
     * Calcula o status de recebimento de um orçamento
     * Retorna informações sobre parcelas pagas e pendentes
     */
    private StatusRecebimentoDTO calcularStatusRecebimento(Long orcamentoId, BigDecimal valorTotalOrcamento) {
        List<Parcela> parcelas = parcelaRepository.findByOrcamentoIdOrderByNumeroParcela(orcamentoId);

        List<ParcelaResponseDTO> parcelasDTO = parcelas.stream()
                .map(this::convertParcelaToDTO)
                .collect(Collectors.toList());

        BigDecimal totalJaConfirmado = parcelas.stream()
                .filter(p -> p.getStatus() == StatusParcela.PAGO)
                .map(Parcela::getValor)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalPendente = valorTotalOrcamento.subtract(totalJaConfirmado);

        Double percentualRecebido = 0.0;
        if (valorTotalOrcamento.compareTo(BigDecimal.ZERO) > 0) {
            percentualRecebido = totalJaConfirmado
                    .divide(valorTotalOrcamento, 4, RoundingMode.HALF_UP)
                    .multiply(new BigDecimal("100"))
                    .doubleValue();
        }

        return StatusRecebimentoDTO.builder()
                .valorTotalOrcamento(valorTotalOrcamento)
                .totalJaConfirmado(totalJaConfirmado)
                .totalPendente(totalPendente)
                .percentualRecebido(percentualRecebido)
                .parcelas(parcelasDTO)
                .build();
    }

    /**
     * Converte entidade Parcela para ParcelaResponseDTO
     */
    private ParcelaResponseDTO convertParcelaToDTO(Parcela parcela) {
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
