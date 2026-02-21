package com.madeirart.appMadeirart.modules.financeiro.controller;

import com.madeirart.appMadeirart.modules.financeiro.dto.ProjecaoCaixaDTO;
import com.madeirart.appMadeirart.modules.financeiro.dto.SaldoInicialRequestDTO;
import com.madeirart.appMadeirart.modules.financeiro.dto.SaldoInicialResponseDTO;
import com.madeirart.appMadeirart.modules.financeiro.service.ProjecaoCaixaService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Controller REST para gerenciamento da projeção de caixa
 */
@Slf4j
@RestController
@RequestMapping("/api/financeiro")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class ProjecaoCaixaController {

    private final ProjecaoCaixaService projecaoCaixaService;

    /**
     * Retorna a projeção completa de caixa
     * GET /api/financeiro/projecao-caixa
     * 
     * Retorna:
     * - Saldo atual acumulado
     * - Saldo inicial cadastrado
     * - Projeção dos próximos 2 meses com detalhamento de entradas e saídas
     */
    @GetMapping("/projecao-caixa")
    public ResponseEntity<ProjecaoCaixaDTO> getProjecaoCaixa() {
        log.info("GET /api/financeiro/projecao-caixa - Buscando projeção de caixa");
        ProjecaoCaixaDTO projecao = projecaoCaixaService.getProjecaoCaixa();
        return ResponseEntity.ok(projecao);
    }

    /**
     * Cadastra ou atualiza o saldo inicial do sistema
     * POST /api/financeiro/saldo-inicial
     * 
     * Body: { "valor": 5000.00, "observacao": "Saldo em caixa ao iniciar o sistema"
     * }
     */
    @PostMapping("/saldo-inicial")
    public ResponseEntity<SaldoInicialResponseDTO> setSaldoInicial(@Valid @RequestBody SaldoInicialRequestDTO dto) {
        log.info("POST /api/financeiro/saldo-inicial - Cadastrando saldo inicial: {}", dto.valor());
        SaldoInicialResponseDTO response = projecaoCaixaService.setSaldoInicial(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Busca o saldo inicial cadastrado
     * GET /api/financeiro/saldo-inicial
     */
    @GetMapping("/saldo-inicial")
    public ResponseEntity<SaldoInicialResponseDTO> getSaldoInicial() {
        log.info("GET /api/financeiro/saldo-inicial - Buscando saldo inicial");
        try {
            SaldoInicialResponseDTO response = projecaoCaixaService.getSaldoInicial();
            return ResponseEntity.ok(response);
        } catch (EntityNotFoundException e) {
            log.warn("Saldo inicial não encontrado");
            return ResponseEntity.notFound().build();
        }
    }
}
