package com.madeirart.appMadeirart.shared.task;

import com.madeirart.appMadeirart.modules.custos.service.CustoFixoService;
import com.madeirart.appMadeirart.modules.custos.service.CustoVariavelService;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * Componente que executa tarefas de inicialização relacionadas a custos
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class CustoStartupTask {

    private final CustoFixoService custoFixoService;
    private final CustoVariavelService custoVariavelService;

    /**
     * Executa a atualização de custos atrasados ao iniciar a aplicação
     */
    @PostConstruct
    public void atualizarCustosAtrasadosNaInicializacao() {
        log.info("Iniciando verificação de custos atrasados...");
        try {
            int custosFixosAtualizados = custoFixoService.atualizarCustosAtrasados();
            int custosVariaveisAtualizados = custoVariavelService.atualizarCustosAtrasados();
            
            log.info("Verificação de custos atrasados concluída. Total de custos atualizados: {} (Fixos: {}, Variáveis: {})",
                    custosFixosAtualizados + custosVariaveisAtualizados,
                    custosFixosAtualizados,
                    custosVariaveisAtualizados);
        } catch (Exception e) {
            log.error("Erro ao atualizar custos atrasados na inicialização", e);
        }
    }
}
