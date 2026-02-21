package com.madeirart.appMadeirart.modules.backup.service;

import com.madeirart.appMadeirart.modules.backup.dto.BackupInfoDTO;
import com.madeirart.appMadeirart.modules.backup.dto.BackupResponseDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Service para gerenciamento de backups do banco de dados
 */
@Slf4j
@Service
public class BackupService {

    private static final int MAX_BACKUPS = 10;
    private static final String BACKUP_PREFIX = "marcenaria_backup_";
    private static final String BACKUP_EXTENSION = ".db";
    private static final DateTimeFormatter TIMESTAMP_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss");

    @Value("${spring.datasource.url}")
    private String datasourceUrl;

    /**
     * Cria um backup do banco de dados
     */
    public BackupResponseDTO createBackup() {
        try {
            log.info("Iniciando processo de backup do banco de dados");

            // 1. Obter caminho do banco de dados original
            Path dbPath = getDatabasePath();
            if (!Files.exists(dbPath)) {
                log.error("Arquivo de banco de dados não encontrado: {}", dbPath);
                return BackupResponseDTO.builder()
                        .success(false)
                        .message("Banco de dados não encontrado")
                        .timestamp(LocalDateTime.now())
                        .build();
            }

            // 2. Criar diretório de backup se não existir
            Path backupDir = getBackupDirectory();
            if (!Files.exists(backupDir)) {
                Files.createDirectories(backupDir);
                log.info("Diretório de backup criado: {}", backupDir);
            }

            // 3. Gerar nome do arquivo de backup com timestamp
            String backupFileName = generateBackupFileName();
            Path backupPath = backupDir.resolve(backupFileName);

            // 4. Copiar arquivo do banco de dados
            Files.copy(dbPath, backupPath, StandardCopyOption.REPLACE_EXISTING);
            long backupSize = Files.size(backupPath);
            log.info("Backup criado com sucesso: {} ({} bytes)", backupPath, backupSize);

            // 5. Executar rotação de backups (manter apenas os últimos 10)
            rotateBackups(backupDir);

            return BackupResponseDTO.builder()
                    .success(true)
                    .message("Backup criado com sucesso")
                    .backupPath(backupPath.toString())
                    .timestamp(LocalDateTime.now())
                    .backupSizeBytes(backupSize)
                    .build();

        } catch (IOException e) {
            log.error("Erro ao criar backup: ", e);
            return BackupResponseDTO.builder()
                    .success(false)
                    .message("Erro ao criar backup: " + e.getMessage())
                    .timestamp(LocalDateTime.now())
                    .build();
        }
    }

    /**
     * Lista todos os backups disponíveis
     */
    public List<BackupInfoDTO> listBackups() {
        try {
            Path backupDir = getBackupDirectory();
            if (!Files.exists(backupDir)) {
                return new ArrayList<>();
            }

            try (Stream<Path> paths = Files.list(backupDir)) {
                return paths
                        .filter(path -> path.getFileName().toString().startsWith(BACKUP_PREFIX))
                        .filter(path -> path.getFileName().toString().endsWith(BACKUP_EXTENSION))
                        .map(this::convertToBackupInfo)
                        .sorted(Comparator.comparing(BackupInfoDTO::createdAt).reversed())
                        .collect(Collectors.toList());
            }

        } catch (IOException e) {
            log.error("Erro ao listar backups: ", e);
            return new ArrayList<>();
        }
    }

    /**
     * Obtém o caminho do diretório de backups
     */
    public String getBackupDirectoryPath() {
        return getBackupDirectory().toString();
    }

    /**
     * Extrai o caminho do banco de dados da URL do datasource
     */
    private Path getDatabasePath() {
        // URL format: jdbc:sqlite:${user.home}/AppData/Roaming/madeirart/marcenaria.db
        String dbPathStr = datasourceUrl.replace("jdbc:sqlite:", "");
        return Paths.get(dbPathStr);
    }

    /**
     * Obtém o diretório de backups (%USERPROFILE%/Documents/Madeirart Backups/)
     */
    private Path getBackupDirectory() {
        String userHome = System.getProperty("user.home");
        return Paths.get(userHome, "Documents", "Madeirart Backups");
    }

    /**
     * Gera nome do arquivo de backup com timestamp
     */
    private String generateBackupFileName() {
        String timestamp = LocalDateTime.now().format(TIMESTAMP_FORMATTER);
        return BACKUP_PREFIX + timestamp + BACKUP_EXTENSION;
    }

    /**
     * Remove backups antigos, mantendo apenas os últimos MAX_BACKUPS
     */
    private void rotateBackups(Path backupDir) throws IOException {
        try (Stream<Path> paths = Files.list(backupDir)) {
            List<Path> backups = paths
                    .filter(path -> path.getFileName().toString().startsWith(BACKUP_PREFIX))
                    .filter(path -> path.getFileName().toString().endsWith(BACKUP_EXTENSION))
                    .sorted(Comparator.comparing(this::extractTimestampFromBackup).reversed())
                    .collect(Collectors.toList());

            if (backups.size() > MAX_BACKUPS) {
                List<Path> backupsToDelete = backups.subList(MAX_BACKUPS, backups.size());
                for (Path backup : backupsToDelete) {
                    Files.deleteIfExists(backup);
                    log.info("Backup antigo removido: {}", backup.getFileName());
                }
            }
        }
    }

    /**
     * Extrai o timestamp do nome do arquivo de backup
     */
    private LocalDateTime extractTimestampFromBackup(Path backupPath) {
        try {
            String fileName = backupPath.getFileName().toString();
            String timestampStr = fileName
                    .replace(BACKUP_PREFIX, "")
                    .replace(BACKUP_EXTENSION, "");
            return LocalDateTime.parse(timestampStr, TIMESTAMP_FORMATTER);
        } catch (Exception e) {
            log.warn("Não foi possível extrair timestamp de {}", backupPath.getFileName());
            return LocalDateTime.MIN;
        }
    }

    /**
     * Converte Path para BackupInfoDTO
     */
    private BackupInfoDTO convertToBackupInfo(Path path) {
        try {
            LocalDateTime createdAt = extractTimestampFromBackup(path);
            long sizeBytes = Files.size(path);
            String formattedSize = formatBytes(sizeBytes);

            return BackupInfoDTO.builder()
                    .fileName(path.getFileName().toString())
                    .fullPath(path.toString())
                    .createdAt(createdAt)
                    .sizeBytes(sizeBytes)
                    .formattedSize(formattedSize)
                    .build();

        } catch (IOException e) {
            log.error("Erro ao obter informações do backup {}: ", path, e);
            return BackupInfoDTO.builder()
                    .fileName(path.getFileName().toString())
                    .fullPath(path.toString())
                    .createdAt(LocalDateTime.MIN)
                    .sizeBytes(0L)
                    .formattedSize("0 B")
                    .build();
        }
    }

    /**
     * Formata bytes para string legível
     */
    private String formatBytes(long bytes) {
        if (bytes < 1024) return bytes + " B";
        int exp = (int) (Math.log(bytes) / Math.log(1024));
        String pre = "KMGTPE".charAt(exp - 1) + "";
        return String.format("%.1f %sB", bytes / Math.pow(1024, exp), pre);
    }
}
