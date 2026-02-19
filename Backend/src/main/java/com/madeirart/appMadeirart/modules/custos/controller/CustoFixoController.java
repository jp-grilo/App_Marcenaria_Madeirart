package com.madeirart.appMadeirart.modules.custos.controller;

import com.madeirart.appMadeirart.modules.custos.dto.CustoFixoRequestDTO;
import com.madeirart.appMadeirart.modules.custos.dto.CustoFixoResponseDTO;
import com.madeirart.appMadeirart.modules.custos.service.CustoFixoService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller REST para gerenciamento de custos fixos
 */
@RestController
@RequestMapping("/api/custos-fixos")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class CustoFixoController {

    private final CustoFixoService custoFixoService;

    /**
     * Lista todos os custos fixos
     * GET /api/custos-fixos
     * 
     * Parâmetros opcionais:
     * - apenasAtivos: Se true, retorna apenas custos ativos (default: false)
     * - orderByDiaVencimento: Se true, ordena por dia de vencimento (default:
     * false, ordena por nome)
     * - diaInicio: Dia inicial do intervalo de vencimento (1-31)
     * - diaFim: Dia final do intervalo de vencimento (1-31)
     */
    @GetMapping
    public ResponseEntity<List<CustoFixoResponseDTO>> listarTodos(
            @RequestParam(required = false, defaultValue = "false") Boolean apenasAtivos,
            @RequestParam(required = false, defaultValue = "false") Boolean orderByDiaVencimento,
            @RequestParam(required = false) Integer diaInicio,
            @RequestParam(required = false) Integer diaFim) {
        List<CustoFixoResponseDTO> custos;

        if (diaInicio != null && diaFim != null) {
            custos = custoFixoService.listarPorPeriodoDias(diaInicio, diaFim);
        } else if (orderByDiaVencimento && apenasAtivos) {
            custos = custoFixoService.listarAtivosPorDiaVencimento();
        } else if (apenasAtivos) {
            custos = custoFixoService.listarAtivos();
        }
        else {
            custos = custoFixoService.listarTodos();
        }

        return ResponseEntity.ok(custos);
    }

    /**
     * Busca um custo fixo por ID
     * GET /api/custos-fixos/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<CustoFixoResponseDTO> buscarPorId(@PathVariable Long id) {
        try {
            CustoFixoResponseDTO custo = custoFixoService.buscarPorId(id);
            return ResponseEntity.ok(custo);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Cria um novo custo fixo
     * POST /api/custos-fixos
     */
    @PostMapping
    public ResponseEntity<CustoFixoResponseDTO> criar(@Valid @RequestBody CustoFixoRequestDTO dto) {
        CustoFixoResponseDTO custo = custoFixoService.criar(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(custo);
    }

    /**
     * Atualiza um custo fixo existente
     * PUT /api/custos-fixos/{id}
     */
    @PutMapping("/{id}")
    public ResponseEntity<CustoFixoResponseDTO> atualizar(
            @PathVariable Long id,
            @Valid @RequestBody CustoFixoRequestDTO dto) {
        try {
            CustoFixoResponseDTO custo = custoFixoService.atualizar(id, dto);
            return ResponseEntity.ok(custo);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Desativa um custo fixo (soft delete)
     * PATCH /api/custos-fixos/{id}/desativar
     */
    @PatchMapping("/{id}/desativar")
    public ResponseEntity<Void> desativar(@PathVariable Long id) {
        try {
            custoFixoService.desativar(id);
            return ResponseEntity.noContent().build();
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Reativa um custo fixo
     * PATCH /api/custos-fixos/{id}/reativar
     */
    @PatchMapping("/{id}/reativar")
    public ResponseEntity<CustoFixoResponseDTO> reativar(@PathVariable Long id) {
        try {
            CustoFixoResponseDTO custo = custoFixoService.reativar(id);
            return ResponseEntity.ok(custo);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Exclui permanentemente um custo fixo
     * DELETE /api/custos-fixos/{id}
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(@PathVariable Long id) {
        try {
            custoFixoService.excluir(id);
            return ResponseEntity.noContent().build();
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }
}
