package com.madeirart.appMadeirart.modules.dashboard.controller;

import com.madeirart.appMadeirart.modules.dashboard.dto.DashboardResumoDTO;
import com.madeirart.appMadeirart.modules.dashboard.dto.ProjecaoFinanceiraDTO;
import com.madeirart.appMadeirart.modules.dashboard.service.DashboardService;
import com.madeirart.appMadeirart.modules.dashboard.service.ProjecaoFinanceiraService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Controller REST para o dashboard e funcionalidades financeiras
 */
@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class DashboardController {

    private final DashboardService dashboardService;
    private final ProjecaoFinanceiraService projecaoFinanceiraService;

    /**
     * Retorna o resumo de orçamentos para o dashboard
     * GET /api/dashboard/resumo
     */
    @GetMapping("/resumo")
    public ResponseEntity<DashboardResumoDTO> getResumo() {
        DashboardResumoDTO resumo = dashboardService.getResumoOrcamentos();
        return ResponseEntity.ok(resumo);
    }

    /**
     * Retorna a projeção financeira de um mês específico
     * GET /api/dashboard/projecao?mes=2&ano=2026
     */
    @GetMapping("/projecao")
    public ResponseEntity<ProjecaoFinanceiraDTO> getProjecao(
            @RequestParam int mes,
            @RequestParam int ano) {
        
        if (mes < 1 || mes > 12) {
            return ResponseEntity.badRequest().build();
        }
        
        ProjecaoFinanceiraDTO projecao = projecaoFinanceiraService.calcularProjecaoMensal(mes, ano);
        return ResponseEntity.ok(projecao);
    }
}
