package com.madeirart.appMadeirart.modules.backup.dto;

import lombok.Builder;

import java.time.LocalDateTime;

/**
 * DTO com informações de um arquivo de backup
 */
@Builder
public record BackupInfoDTO(
        String fileName,
        String fullPath,
        LocalDateTime createdAt,
        Long sizeBytes,
        String formattedSize) {
}
