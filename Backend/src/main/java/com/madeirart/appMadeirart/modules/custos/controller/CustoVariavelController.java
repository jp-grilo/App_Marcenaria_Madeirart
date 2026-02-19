package com.madeirart.appMadeirart.modules.custos.controller;

import com.madeirart.appMadeirart.modules.custos.dto.CustoVariavelRequestDTO;
import com.madeirart.appMadeirart.modules.custos.dto.CustoVariavelResponseDTO;
import com.madeirart.appMadeirart.modules.custos.service.CustoVariavelService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

/**
 * Controller REST para gerenciamento de custos variáveis
 */
@RestController
@RequestMapping("/api/custos-variaveis")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class CustoVariavelController {

    private final CustoVariavelService custoVariavelService;

    /**
     * Lista todos os custos variáveis
     * GET /api/custos-variaveis
     * 
     * Parâmetros opcionais:
     * - dataInicio: Data de início do período
     * - dataFim: Data de fim do período
     */
    @GetMapping
    public ResponseEntity<List<CustoVariavelResponseDTO>> listarTodos(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataInicio,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataFim) {
        List<CustoVariavelResponseDTO> custos;

        if (dataInicio != null && dataFim != null) {
            custos = custoVariavelService.listarPorPeriodo(dataInicio, dataFim);
        } else {
            custos = custoVariavelService.listarTodos();
        }

        return ResponseEntity.ok(custos);
    }

    /**
     * Busca um custo variável por ID
     * GET /api/custos-variaveis/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<CustoVariavelResponseDTO> buscarPorId(@PathVariable Long id) {
        try {
            CustoVariavelResponseDTO custo = custoVariavelService.buscarPorId(id);
            return ResponseEntity.ok(custo);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Cria um novo custo variável
     * POST /api/custos-variaveis
     */
    @PostMapping
    public ResponseEntity<CustoVariavelResponseDTO> criar(@Valid @RequestBody CustoVariavelRequestDTO dto) {
        CustoVariavelResponseDTO custo = custoVariavelService.criar(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(custo);
    }

    /**
     * Atualiza um custo variável existente
     * PUT /api/custos-variaveis/{id}
     */
    @PutMapping("/{id}")
    public ResponseEntity<CustoVariavelResponseDTO> atualizar(
            @PathVariable Long id,
            @Valid @RequestBody CustoVariavelRequestDTO dto) {
        try {
            CustoVariavelResponseDTO custo = custoVariavelService.atualizar(id, dto);
            return ResponseEntity.ok(custo);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Exclui um custo variável
     * DELETE /api/custos-variaveis/{id}
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(@PathVariable Long id) {
        try {
            custoVariavelService.excluir(id);
            return ResponseEntity.noContent().build();
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }
}
