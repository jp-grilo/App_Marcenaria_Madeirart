package com.madeirart.appMadeirart.modules.orcamento.controller;

import com.madeirart.appMadeirart.modules.orcamento.dto.ParcelaResponseDTO;
import com.madeirart.appMadeirart.modules.orcamento.service.ParcelaService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller REST para gerenciamento de parcelas
 */
@RestController
@RequestMapping("/api/parcelas")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class ParcelaController {

    private final ParcelaService parcelaService;

    /**
     * Lista todas as parcelas de um orçamento
     * GET /api/parcelas/orcamento/{orcamentoId}
     */
    @GetMapping("/orcamento/{orcamentoId}")
    public ResponseEntity<List<ParcelaResponseDTO>> listarPorOrcamento(@PathVariable Long orcamentoId) {
        List<ParcelaResponseDTO> parcelas = parcelaService.listarPorOrcamento(orcamentoId);
        return ResponseEntity.ok(parcelas);
    }

    /**
     * Busca uma parcela por ID
     * GET /api/parcelas/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<ParcelaResponseDTO> buscarPorId(@PathVariable Long id) {
        try {
            ParcelaResponseDTO parcela = parcelaService.buscarPorId(id);
            return ResponseEntity.ok(parcela);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Confirma o pagamento de uma parcela
     * PATCH /api/parcelas/{id}/confirmar
     */
    @PatchMapping("/{id}/confirmar")
    public ResponseEntity<ParcelaResponseDTO> confirmarPagamento(@PathVariable Long id) {
        try {
            ParcelaResponseDTO parcela = parcelaService.confirmarPagamento(id);
            return ResponseEntity.ok(parcela);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest().build();
        }
    }
}
