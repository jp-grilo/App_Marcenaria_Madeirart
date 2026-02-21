package com.madeirart.appMadeirart.modules.backup.controller;

import com.madeirart.appMadeirart.modules.backup.dto.BackupInfoDTO;
import com.madeirart.appMadeirart.modules.backup.dto.BackupResponseDTO;
import com.madeirart.appMadeirart.modules.backup.service.BackupService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Controller REST para gerenciamento de backups
 */
@Slf4j
@RestController
@RequestMapping("/api/backup")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class BackupController {

    private final BackupService backupService;

    /**
     * Executa um backup manual do banco de dados
     * POST /api/backup/execute
     * 
     * @return ResponseEntity com informações do backup criado
     */
    @PostMapping("/execute")
    public ResponseEntity<BackupResponseDTO> executeBackup() {
        log.info("Requisição de backup manual recebida");

        BackupResponseDTO response = backupService.createBackup();

        if (response.success()) {
            log.info("Backup manual executado com sucesso: {}", response.backupPath());
            return ResponseEntity.ok(response);
        } else {
            log.error("Falha ao executar backup manual: {}", response.message());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * Lista todos os backups disponíveis
     * GET /api/backup/list
     * 
     * @return ResponseEntity com lista de backups
     */
    @GetMapping("/list")
    public ResponseEntity<List<BackupInfoDTO>> listBackups() {
        log.info("Requisição de listagem de backups recebida");

        List<BackupInfoDTO> backups = backupService.listBackups();
        return ResponseEntity.ok(backups);
    }

    /**
     * Obtém o caminho do diretório de backups
     * GET /api/backup/directory
     * 
     * @return ResponseEntity com o caminho do diretório
     */
    @GetMapping("/directory")
    public ResponseEntity<Map<String, String>> getBackupDirectory() {
        log.info("Requisição de caminho do diretório de backups recebida");

        String backupDir = backupService.getBackupDirectoryPath();
        Map<String, String> response = new HashMap<>();
        response.put("path", backupDir);

        return ResponseEntity.ok(response);
    }
}
