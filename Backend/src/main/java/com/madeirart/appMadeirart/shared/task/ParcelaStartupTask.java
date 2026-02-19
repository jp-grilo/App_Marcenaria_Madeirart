package com.madeirart.appMadeirart.shared.task;

import com.madeirart.appMadeirart.modules.orcamento.service.ParcelaService;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * Componente que executa tarefas de inicialização relacionadas a parcelas
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class ParcelaStartupTask {

    private final ParcelaService parcelaService;

    /**
     * Executa a atualização de parcelas atrasadas ao iniciar a aplicação
     */
    @PostConstruct
    public void atualizarParcelasAtrasadasNaInicializacao() {
        log.info("Iniciando verificação de parcelas atrasadas...");
        try {
            int quantidadeAtualizada = parcelaService.atualizarParcelasAtrasadas();
            log.info("Verificação de parcelas atrasadas concluída. Total de parcelas atualizadas: {}",
                    quantidadeAtualizada);
        } catch (Exception e) {
            log.error("Erro ao atualizar parcelas atrasadas na inicialização", e);
        }
    }
}
