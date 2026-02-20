package com.madeirart.appMadeirart.modules.dashboard.service;

import com.madeirart.appMadeirart.modules.dashboard.dto.DashboardResumoDTO;
import com.madeirart.appMadeirart.modules.dashboard.dto.OrcamentoResumoDTO;
import com.madeirart.appMadeirart.modules.orcamento.entity.Orcamento;
import com.madeirart.appMadeirart.modules.orcamento.repository.OrcamentoRepository;
import com.madeirart.appMadeirart.shared.enums.StatusOrcamento;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Service para operações de resumo do dashboard
 */
@Service
@RequiredArgsConstructor
public class DashboardService {

    private final OrcamentoRepository orcamentoRepository;

    /**
     * Obtém o resumo de orçamentos para o dashboard
     */
    public DashboardResumoDTO getResumoOrcamentos() {
        List<Orcamento> orcamentosAtivos = orcamentoRepository.findByStatus(StatusOrcamento.AGUARDANDO);
        List<Orcamento> orcamentosEmProducao = orcamentoRepository.findByStatus(StatusOrcamento.INICIADA);

        // Próximos orçamentos a vencer (5 dias)
        List<OrcamentoResumoDTO> orcamentosProximos = getOrcamentosProximosEntrega(5);

        return new DashboardResumoDTO(
                (long) orcamentosAtivos.size(),
                (long) orcamentosEmProducao.size(),
                orcamentosProximos);
    }

    /**
     * Obtém orçamentos que estão próximos da data de entrega
     * 
     * @param dias Número de dias de antecedência
     */
    public List<OrcamentoResumoDTO> getOrcamentosProximosEntrega(int dias) {
        LocalDate dataLimite = LocalDate.now().plusDays(dias);

        return orcamentoRepository.findAll().stream()
                .filter(o -> o.getPrevisaoEntrega() != null)
                .filter(o -> o.getStatus() == StatusOrcamento.INICIADA)
                .filter(o -> !o.getPrevisaoEntrega().isAfter(dataLimite))
                .sorted((o1, o2) -> o1.getPrevisaoEntrega().compareTo(o2.getPrevisaoEntrega()))
                .map(this::toOrcamentoResumoDTO)
                .collect(Collectors.toList());
    }

    /**
     * Converte Orcamento para OrcamentoResumoDTO
     */
    private OrcamentoResumoDTO toOrcamentoResumoDTO(Orcamento orcamento) {
        return new OrcamentoResumoDTO(
                orcamento.getId(),
                orcamento.getCliente(),
                orcamento.getMoveis(),
                orcamento.getPrevisaoEntrega(),
                orcamento.getStatus().getDescricao());
    }
}
