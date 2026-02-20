package com.madeirart.appMadeirart.modules.custos.service;

import com.madeirart.appMadeirart.modules.custos.dto.CustoVariavelRequestDTO;
import com.madeirart.appMadeirart.modules.custos.dto.CustoVariavelResponseDTO;
import com.madeirart.appMadeirart.modules.custos.entity.CustoVariavel;
import com.madeirart.appMadeirart.modules.custos.repository.CustoVariavelRepository;
import com.madeirart.appMadeirart.shared.enums.StatusCusto;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
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
     * Se quantidadeParcelas > 1, cria múltiplas parcelas
     */
    @Transactional
    public List<CustoVariavelResponseDTO> criar(CustoVariavelRequestDTO dto) {
        log.info("Criando novo custo variável: {}", dto.nome());

        List<CustoVariavelResponseDTO> custosCreated = new ArrayList<>();

        // Se não houver parcelamento ou for apenas 1 parcela
        if (dto.quantidadeParcelas() == null || dto.quantidadeParcelas() <= 1) {
            CustoVariavel custoVariavel = CustoVariavel.builder()
                    .nome(dto.nome())
                    .valor(dto.valor())
                    .dataLancamento(dto.dataLancamento())
                    .descricao(dto.descricao())
                    .parcelado(false)
                    .build();

            CustoVariavel saved = custoVariavelRepository.save(custoVariavel);
            log.info("Custo variável criado com sucesso - ID: {}", saved.getId());
            custosCreated.add(convertToDTO(saved));
        } else {
            int quantidadeParcelas = dto.quantidadeParcelas();
            BigDecimal valorParcela = dto.valor().divide(
                    BigDecimal.valueOf(quantidadeParcelas),
                    2,
                    RoundingMode.HALF_UP);

            CustoVariavel primeiraParcela = CustoVariavel.builder()
                    .nome(formatarNomeParcela(dto.nome(), 1, quantidadeParcelas))
                    .valor(valorParcela)
                    .dataLancamento(dto.dataLancamento())
                    .descricao(dto.descricao())
                    .parcelado(true)
                    .numeroParcela(1)
                    .totalParcelas(quantidadeParcelas)
                    .build();

            CustoVariavel savedPrimeira = custoVariavelRepository.save(primeiraParcela);
            savedPrimeira.setCustoOrigemId(savedPrimeira.getId());
            savedPrimeira = custoVariavelRepository.save(savedPrimeira);
            custosCreated.add(convertToDTO(savedPrimeira));

            Long custoOrigemId = savedPrimeira.getId();

            for (int i = 2; i <= quantidadeParcelas; i++) {
                LocalDate dataParcela = dto.dataLancamento().plusMonths(i - 1);

                CustoVariavel parcela = CustoVariavel.builder()
                        .nome(formatarNomeParcela(dto.nome(), i, quantidadeParcelas))
                        .valor(valorParcela)
                        .dataLancamento(dataParcela)
                        .descricao(dto.descricao())
                        .parcelado(true)
                        .numeroParcela(i)
                        .totalParcelas(quantidadeParcelas)
                        .custoOrigemId(custoOrigemId)
                        .build();

                CustoVariavel saved = custoVariavelRepository.save(parcela);
                custosCreated.add(convertToDTO(saved));
            }

            log.info("Custo variável parcelado criado com sucesso - {} parcelas geradas", quantidadeParcelas);
        }

        return custosCreated;
    }

    /**
     * Formata o nome da parcela: NomeCusto (N/K)
     */
    private String formatarNomeParcela(String nomeBase, int numeroParcela, int totalParcelas) {
        return String.format("%s (%d/%d)", nomeBase, numeroParcela, totalParcelas);
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
     * Marca um custo variável como pago
     */
    @Transactional
    public CustoVariavelResponseDTO marcarComoPago(Long id) {
        log.info("Marcando custo variável como PAGO - ID: {}", id);

        CustoVariavel custoVariavel = custoVariavelRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Custo variável não encontrado com ID: " + id));

        custoVariavel.setStatus(StatusCusto.PAGO);
        CustoVariavel saved = custoVariavelRepository.save(custoVariavel);
        log.info("Custo variável marcado como PAGO - ID: {}", id);
        return convertToDTO(saved);
    }

    /**
     * Marca um custo variável como pendente
     */
    @Transactional
    public CustoVariavelResponseDTO marcarComoPendente(Long id) {
        log.info("Marcando custo variável como PENDENTE - ID: {}", id);

        CustoVariavel custoVariavel = custoVariavelRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Custo variável não encontrado com ID: " + id));

        if (custoVariavel.getDataLancamento().isBefore(LocalDate.now())) {
            custoVariavel.setStatus(StatusCusto.ATRASADO);
        } else {
            custoVariavel.setStatus(StatusCusto.PENDENTE);
        }
        CustoVariavel saved = custoVariavelRepository.save(custoVariavel);
        log.info("Custo variável marcado como {} - ID: {}", custoVariavel.getStatus(), id);
        return convertToDTO(saved);
    }

    /**
     * Atualiza o status de custos variáveis pendentes com data de lançamento
     * vencida para ATRASADO
     * 
     * @return Quantidade de custos atualizados
     */
    @Transactional
    public int atualizarCustosAtrasados() {
        log.info("Iniciando atualização de custos variáveis atrasados");

        // Busca custos variáveis com status PENDENTE e data de lançamento anterior à
        // data atual
        List<CustoVariavel> custosAtrasados = custoVariavelRepository
                .findByStatusAndDataLancamentoBefore(StatusCusto.PENDENTE, LocalDate.now());

        if (custosAtrasados.isEmpty()) {
            log.info("Nenhum custo variável atrasado encontrado");
            return 0;
        }

        // Atualiza o status para ATRASADO
        custosAtrasados.forEach(custo -> custo.setStatus(StatusCusto.ATRASADO));
        custoVariavelRepository.saveAll(custosAtrasados);

        log.info("Total de {} custo(s) variável(is) atualizado(s) para status ATRASADO", custosAtrasados.size());
        return custosAtrasados.size();
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
                custoVariavel.getParcelado(),
                custoVariavel.getNumeroParcela(),
                custoVariavel.getTotalParcelas(),
                custoVariavel.getCustoOrigemId(),
                custoVariavel.getCreatedAt(),
                custoVariavel.getUpdatedAt());
    }
}
