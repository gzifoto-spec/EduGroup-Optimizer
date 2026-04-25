package com.edugroup.optimizer.compatibility.dto;

import java.time.LocalDateTime;

public record CompatibilityScoreResponse(
        Long id,
        Long studentAId,
        String studentAName,
        Long studentBId,
        String studentBName,
        Integer score,
        String notes,
        LocalDateTime updatedAt
) {
}
