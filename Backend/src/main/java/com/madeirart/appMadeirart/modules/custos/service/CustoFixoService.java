package com.madeirart.appMadeirart.modules.custos.service;

import com.madeirart.appMadeirart.modules.custos.dto.CustoFixoRequestDTO;
import com.madeirart.appMadeirart.modules.custos.dto.CustoFixoResponseDTO;
import com.madeirart.appMadeirart.modules.custos.entity.CustoFixo;
import com.madeirart.appMadeirart.modules.custos.repository.CustoFixoRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Service para gerenciamento de custos fixos
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CustoFixoService {

    private final CustoFixoRepository custoFixoRepository;

    /**
     * Lista todos os custos fixos
     */
    @Transactional(readOnly = true)
    public List<CustoFixoResponseDTO> listarTodos() {
        return custoFixoRepository.findAllByOrderByNome()
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Lista apenas os custos fixos ativos
     */
    @Transactional(readOnly = true)
    public List<CustoFixoResponseDTO> listarAtivos() {
        return custoFixoRepository.findByAtivoTrueOrderByNome()
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Lista custos fixos ativos que vencem dentro de um intervalo de dias do mês
     */
    @Transactional(readOnly = true)
    public List<CustoFixoResponseDTO> listarPorPeriodoDias(Integer diaInicio, Integer diaFim) {
        return custoFixoRepository.findByAtivoTrueAndDiaVencimentoBetween(diaInicio, diaFim)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Lista custos fixos ativos ordenados por dia de vencimento
     */
    @Transactional(readOnly = true)
    public List<CustoFixoResponseDTO> listarAtivosPorDiaVencimento() {
        return custoFixoRepository.findByAtivoTrueOrderByDiaVencimento()
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Busca um custo fixo por ID
     */
    @Transactional(readOnly = true)
    public CustoFixoResponseDTO buscarPorId(Long id) {
        CustoFixo custoFixo = custoFixoRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Custo fixo não encontrado com ID: " + id));
        return convertToDTO(custoFixo);
    }

    /**
     * Cria um novo custo fixo
     */
    @Transactional
    public CustoFixoResponseDTO criar(CustoFixoRequestDTO dto) {
        log.info("Criando novo custo fixo: {}", dto.nome());

        CustoFixo custoFixo = CustoFixo.builder()
                .nome(dto.nome())
                .valor(dto.valor())
                .diaVencimento(dto.diaVencimento())
                .descricao(dto.descricao())
                .ativo(true)
                .build();

        CustoFixo saved = custoFixoRepository.save(custoFixo);
        log.info("Custo fixo criado com sucesso - ID: {}", saved.getId());
        return convertToDTO(saved);
    }

    /**
     * Atualiza um custo fixo existente
     */
    @Transactional
    public CustoFixoResponseDTO atualizar(Long id, CustoFixoRequestDTO dto) {
        log.info("Atualizando custo fixo ID: {}", id);

        CustoFixo custoFixo = custoFixoRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Custo fixo não encontrado com ID: " + id));

        custoFixo.setNome(dto.nome());
        custoFixo.setValor(dto.valor());
        custoFixo.setDiaVencimento(dto.diaVencimento());
        custoFixo.setDescricao(dto.descricao());

        CustoFixo saved = custoFixoRepository.save(custoFixo);
        log.info("Custo fixo atualizado com sucesso - ID: {}", saved.getId());
        return convertToDTO(saved);
    }

    /**
     * Desativa um custo fixo (soft delete)
     */
    @Transactional
    public void desativar(Long id) {
        log.info("Desativando custo fixo ID: {}", id);

        CustoFixo custoFixo = custoFixoRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Custo fixo não encontrado com ID: " + id));

        custoFixo.setAtivo(false);
        custoFixoRepository.save(custoFixo);
        log.info("Custo fixo desativado com sucesso - ID: {}", id);
    }

    /**
     * Reativa um custo fixo
     */
    @Transactional
    public CustoFixoResponseDTO reativar(Long id) {
        log.info("Reativando custo fixo ID: {}", id);

        CustoFixo custoFixo = custoFixoRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Custo fixo não encontrado com ID: " + id));

        custoFixo.setAtivo(true);
        CustoFixo saved = custoFixoRepository.save(custoFixo);
        log.info("Custo fixo reativado com sucesso - ID: {}", id);
        return convertToDTO(saved);
    }

    /**
     * Exclui permanentemente um custo fixo
     */
    @Transactional
    public void excluir(Long id) {
        log.info("Excluindo custo fixo ID: {}", id);

        if (!custoFixoRepository.existsById(id)) {
            throw new EntityNotFoundException("Custo fixo não encontrado com ID: " + id);
        }

        custoFixoRepository.deleteById(id);
        log.info("Custo fixo excluído com sucesso - ID: {}", id);
    }

    /**
     * Converte entidade para DTO
     */
    private CustoFixoResponseDTO convertToDTO(CustoFixo custoFixo) {
        return new CustoFixoResponseDTO(
                custoFixo.getId(),
                custoFixo.getNome(),
                custoFixo.getValor(),
                custoFixo.getDiaVencimento(),
                custoFixo.getDescricao(),
                custoFixo.getAtivo(),
                custoFixo.getStatus(),
                custoFixo.getCreatedAt(),
                custoFixo.getUpdatedAt());
    }
}
