package com.madeirart.appMadeirart.modules.backup.dto;

import lombok.Builder;

import java.time.LocalDateTime;

/**
 * DTO de resposta para operações de backup
 */
@Builder
public record BackupResponseDTO(
        boolean success,
        String message,
        String backupPath,
        LocalDateTime timestamp,
        Long backupSizeBytes) {
}
