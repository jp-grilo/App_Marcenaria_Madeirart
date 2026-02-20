package com.madeirart.appMadeirart.modules.dashboard.controller;

import com.madeirart.appMadeirart.modules.dashboard.dto.CalendarioDTO;
import com.madeirart.appMadeirart.modules.dashboard.service.CalendarioFinanceiroService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Controller REST para operações financeiras (calendário, extrato, etc)
 */
@RestController
@RequestMapping("/api/financeiro")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class FinanceiroController {

    private final CalendarioFinanceiroService calendarioFinanceiroService;

    /**
     * Retorna o calendário financeiro de um mês específico
     * GET /api/financeiro/calendario?mes=2&ano=2026
     */
    @GetMapping("/calendario")
    public ResponseEntity<CalendarioDTO> getCalendario(
            @RequestParam int mes,
            @RequestParam int ano) {
        
        if (mes < 1 || mes > 12) {
            return ResponseEntity.badRequest().build();
        }
        
        CalendarioDTO calendario = calendarioFinanceiroService.getCalendarioMensal(mes, ano);
        return ResponseEntity.ok(calendario);
    }
}
