package com.madeirart.appMadeirart.modules.backup.component;

import com.madeirart.appMadeirart.modules.backup.dto.BackupResponseDTO;
import com.madeirart.appMadeirart.modules.backup.service.BackupService;
import jakarta.annotation.PreDestroy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

/**
 * Componente responsável por executar backup automático no shutdown da
 * aplicação
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class BackupShutdownHook {

    private final BackupService backupService;
    private static final int BACKUP_TIMEOUT_SECONDS = 5;

    /**
     * Registra o shutdown hook quando a aplicação está pronta
     */
    @EventListener(ApplicationReadyEvent.class)
    public void registerShutdownHook() {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            log.info("Shutdown hook acionado - Iniciando backup automático");
            executeBackupOnShutdown();
        }));
        log.info("Shutdown hook de backup registrado com sucesso");
    }

    /**
     * Método @PreDestroy como backup alternativo
     * É executado antes do shutdown hook em alguns casos
     */
    @PreDestroy
    public void onDestroy() {
        log.info("@PreDestroy acionado - Verificando backup");
        // Este método serve como fallback caso o shutdown hook não execute
    }

    /**
     * Executa o backup de forma assíncrona com timeout
     */
    private void executeBackupOnShutdown() {
        try {
            CompletableFuture<BackupResponseDTO> backupFuture = CompletableFuture.supplyAsync(() -> {
                try {
                    return backupService.createBackup();
                } catch (Exception e) {
                    log.error("Erro durante backup no shutdown: ", e);
                    return BackupResponseDTO.builder()
                            .success(false)
                            .message("Erro: " + e.getMessage())
                            .build();
                }
            });

            // Aguarda o backup com timeout de 5 segundos
            BackupResponseDTO response = backupFuture.get(BACKUP_TIMEOUT_SECONDS, TimeUnit.SECONDS);

            if (response.success()) {
                log.info("✓ Backup automático concluído com sucesso no shutdown");
                log.info("  Arquivo: {}", response.backupPath());
                log.info("  Tamanho: {} bytes", response.backupSizeBytes());
            } else {
                log.warn("✗ Falha no backup automático: {}", response.message());
            }

        } catch (java.util.concurrent.TimeoutException e) {
            log.warn("Timeout ao executar backup no shutdown (máximo {} segundos)", BACKUP_TIMEOUT_SECONDS);
        } catch (Exception e) {
            log.error("Erro inesperado durante backup no shutdown: ", e);
        }
    }
}
