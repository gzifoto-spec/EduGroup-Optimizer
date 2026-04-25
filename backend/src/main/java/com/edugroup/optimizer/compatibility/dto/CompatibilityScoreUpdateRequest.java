package com.edugroup.optimizer.compatibility.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CompatibilityScoreUpdateRequest(
        @NotNull @Min(-5) @Max(5) Integer score,
        @Size(max = 255) String notes
) {
}
