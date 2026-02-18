package com.madeirart.appMadeirart.modules.orcamento.service;

import com.madeirart.appMadeirart.modules.orcamento.dto.ItemMaterialDTO;
import com.madeirart.appMadeirart.modules.orcamento.dto.OrcamentoRequestDTO;
import com.madeirart.appMadeirart.modules.orcamento.dto.OrcamentoResponseDTO;
import com.madeirart.appMadeirart.modules.orcamento.entity.ItemMaterial;
import com.madeirart.appMadeirart.modules.orcamento.entity.Orcamento;
import com.madeirart.appMadeirart.modules.orcamento.repository.OrcamentoRepository;
import com.madeirart.appMadeirart.shared.enums.StatusOrcamento;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Service para gerenciamento de orçamentos
 */
@Service
@RequiredArgsConstructor
public class OrcamentoService {

    private final OrcamentoRepository orcamentoRepository;

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
     */
    @Transactional
    public OrcamentoResponseDTO atualizarOrcamento(Long id, OrcamentoRequestDTO dto) {
        Orcamento orcamento = orcamentoRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Orçamento não encontrado com ID: " + id));

        // Atualiza os campos básicos
        orcamento.setCliente(dto.cliente());
        orcamento.setMoveis(dto.moveis());
        orcamento.setData(dto.data());
        orcamento.setPrevisaoEntrega(dto.previsaoEntrega());
        orcamento.setFatorMaoDeObra(dto.fatorMaoDeObra());
        orcamento.setCustosExtras(dto.custosExtras());
        orcamento.setCpc(dto.cpc());

        // Remove todos os itens existentes
        orcamento.getItens().clear();

        // Adiciona os novos itens
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

        // Adiciona os itens mantendo a relação bidirecional
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
                        item.calcularSubtotal()
                ))
                .collect(Collectors.toList());

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
                .valorTotal(orcamento.calcularValorTotal())
                .createdAt(orcamento.getCreatedAt())
                .updatedAt(orcamento.getUpdatedAt())
                .build();
    }
}
