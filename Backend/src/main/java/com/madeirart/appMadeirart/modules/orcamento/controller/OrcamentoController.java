package com.madeirart.appMadeirart.modules.orcamento.controller;

import com.madeirart.appMadeirart.modules.orcamento.dto.IniciarProducaoDTO;
import com.madeirart.appMadeirart.modules.orcamento.dto.OrcamentoAuditoriaDTO;
import com.madeirart.appMadeirart.modules.orcamento.dto.OrcamentoRequestDTO;
import com.madeirart.appMadeirart.modules.orcamento.dto.OrcamentoResponseDTO;
import com.madeirart.appMadeirart.modules.orcamento.service.OrcamentoService;
import com.madeirart.appMadeirart.shared.enums.StatusOrcamento;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller REST para gerenciamento de orçamentos
 */
@RestController
@RequestMapping("/api/orcamentos")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class OrcamentoController {

    private final OrcamentoService orcamentoService;

    /**
     * Cria um novo orçamento
     * POST /api/orcamentos
     */
    @PostMapping
    public ResponseEntity<OrcamentoResponseDTO> criarOrcamento(@Valid @RequestBody OrcamentoRequestDTO dto) {
        OrcamentoResponseDTO response = orcamentoService.criarOrcamento(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Busca um orçamento por ID
     * GET /api/orcamentos/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<OrcamentoResponseDTO> buscarPorId(@PathVariable Long id) {
        try {
            OrcamentoResponseDTO response = orcamentoService.buscarPorId(id);
            return ResponseEntity.ok(response);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Lista todos os orçamentos ou filtra por status
     * GET /api/orcamentos
     * GET /api/orcamentos?status=AGUARDANDO
     */
    @GetMapping
    public ResponseEntity<List<OrcamentoResponseDTO>> listarOrcamentos(
            @RequestParam(required = false) StatusOrcamento status) {
        List<OrcamentoResponseDTO> response;
        if (status != null) {
            response = orcamentoService.listarPorStatus(status);
        } else {
            response = orcamentoService.listarTodos();
        }
        return ResponseEntity.ok(response);
    }

    /**
     * Atualiza um orçamento existente
     * PUT /api/orcamentos/{id}
     */
    @PutMapping("/{id}")
    public ResponseEntity<OrcamentoResponseDTO> atualizarOrcamento(
            @PathVariable Long id,
            @Valid @RequestBody OrcamentoRequestDTO dto) {
        try {
            OrcamentoResponseDTO response = orcamentoService.atualizarOrcamento(id, dto);
            return ResponseEntity.ok(response);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Deleta um orçamento
     * DELETE /api/orcamentos/{id}
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletarOrcamento(@PathVariable Long id) {
        try {
            orcamentoService.deletarOrcamento(id);
            return ResponseEntity.noContent().build();
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Inicia a produção de um orçamento com o plano de parcelas
     * PATCH /api/orcamentos/{id}/iniciar
     */
    @PatchMapping("/{id}/iniciar")
    public ResponseEntity<OrcamentoResponseDTO> iniciarProducao(
            @PathVariable Long id,
            @Valid @RequestBody IniciarProducaoDTO dto) {
        try {
            OrcamentoResponseDTO response = orcamentoService.iniciarProducao(id, dto);
            return ResponseEntity.ok(response);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (IllegalStateException | IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Busca o histórico de auditoria de um orçamento
     * GET /api/orcamentos/{id}/historico
     */
    @GetMapping("/{id}/historico")
    public ResponseEntity<List<OrcamentoAuditoriaDTO>> buscarHistorico(@PathVariable Long id) {
        try {
            List<OrcamentoAuditoriaDTO> historico = orcamentoService.buscarHistorico(id);
            return ResponseEntity.ok(historico);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Altera o status de um orçamento
     * PATCH /api/orcamentos/{id}/status?novoStatus=FINALIZADA
     */
    @PatchMapping("/{id}/status")
    public ResponseEntity<OrcamentoResponseDTO> alterarStatus(
            @PathVariable Long id,
            @RequestParam StatusOrcamento novoStatus) {
        try {
            OrcamentoResponseDTO response = orcamentoService.alterarStatus(id, novoStatus);
            return ResponseEntity.ok(response);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }
}
