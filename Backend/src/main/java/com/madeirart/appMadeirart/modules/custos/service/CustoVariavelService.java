package com.madeirart.appMadeirart.modules.custos.service;

import com.madeirart.appMadeirart.modules.custos.dto.CustoVariavelRequestDTO;
import com.madeirart.appMadeirart.modules.custos.dto.CustoVariavelResponseDTO;
import com.madeirart.appMadeirart.modules.custos.entity.CustoVariavel;
import com.madeirart.appMadeirart.modules.custos.repository.CustoVariavelRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Service para gerenciamento de custos variáveis
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CustoVariavelService {

    private final CustoVariavelRepository custoVariavelRepository;

    /**
     * Lista todos os custos variáveis
     */
    @Transactional(readOnly = true)
    public List<CustoVariavelResponseDTO> listarTodos() {
        return custoVariavelRepository.findAllByOrderByDataLancamentoDesc()
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Lista custos variáveis em um período específico
     */
    @Transactional(readOnly = true)
    public List<CustoVariavelResponseDTO> listarPorPeriodo(LocalDate dataInicio, LocalDate dataFim) {
        return custoVariavelRepository.findByDataLancamentoBetween(dataInicio, dataFim)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Busca um custo variável por ID
     */
    @Transactional(readOnly = true)
    public CustoVariavelResponseDTO buscarPorId(Long id) {
        CustoVariavel custoVariavel = custoVariavelRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Custo variável não encontrado com ID: " + id));
        return convertToDTO(custoVariavel);
    }

    /**
     * Cria um novo custo variável
     */
    @Transactional
    public CustoVariavelResponseDTO criar(CustoVariavelRequestDTO dto) {
        log.info("Criando novo custo variável: {}", dto.nome());

        CustoVariavel custoVariavel = CustoVariavel.builder()
                .nome(dto.nome())
                .valor(dto.valor())
                .dataLancamento(dto.dataLancamento())
                .descricao(dto.descricao())
                .build();

        CustoVariavel saved = custoVariavelRepository.save(custoVariavel);
        log.info("Custo variável criado com sucesso - ID: {}", saved.getId());
        return convertToDTO(saved);
    }

    /**
     * Atualiza um custo variável existente
     */
    @Transactional
    public CustoVariavelResponseDTO atualizar(Long id, CustoVariavelRequestDTO dto) {
        log.info("Atualizando custo variável ID: {}", id);

        CustoVariavel custoVariavel = custoVariavelRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Custo variável não encontrado com ID: " + id));

        custoVariavel.setNome(dto.nome());
        custoVariavel.setValor(dto.valor());
        custoVariavel.setDataLancamento(dto.dataLancamento());
        custoVariavel.setDescricao(dto.descricao());

        CustoVariavel saved = custoVariavelRepository.save(custoVariavel);
        log.info("Custo variável atualizado com sucesso - ID: {}", saved.getId());
        return convertToDTO(saved);
    }

    /**
     * Exclui um custo variável
     */
    @Transactional
    public void excluir(Long id) {
        log.info("Excluindo custo variável ID: {}", id);

        if (!custoVariavelRepository.existsById(id)) {
            throw new EntityNotFoundException("Custo variável não encontrado com ID: " + id);
        }

        custoVariavelRepository.deleteById(id);
        log.info("Custo variável excluído com sucesso - ID: {}", id);
    }

    /**
     * Converte entidade para DTO
     */
    private CustoVariavelResponseDTO convertToDTO(CustoVariavel custoVariavel) {
        return new CustoVariavelResponseDTO(
                custoVariavel.getId(),
                custoVariavel.getNome(),
                custoVariavel.getValor(),
                custoVariavel.getDataLancamento(),
                custoVariavel.getDescricao(),
                custoVariavel.getStatus(),
                custoVariavel.getCreatedAt(),
                custoVariavel.getUpdatedAt());
    }
}
